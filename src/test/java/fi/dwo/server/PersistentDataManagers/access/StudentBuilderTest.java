/**
 * Copyrighted Mar 12, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Gert van der Plas
 */
public class StudentBuilderTest {

    private static final Logger LOG = Logger.getLogger(StudentBuilderTest.class.getName());

    static DatabaseManager dbInstance = null;

    public StudentBuilderTest() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbInstance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        dbInstance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
    }

    @Before
    public void setUp() {
        dbInstance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        dbInstance.ClearDatabase();
    }

    /**
     * Test of updateStudentModelData method, of class StudentBuilder.
     */
    @Test
    public void testSetStudentModelData() throws Exception {

        //Build StudentModelStructure
        DomStudentModelStructure model = new DomStudentModelStructure();
        Map<String, String> titleMap = new HashMap<>();
        Map<String, String> descrMap = new HashMap<>();
        titleMap.put("nl", "C model");
        titleMap.put("en", "C model");
        descrMap.put("nl", "C description");
        model.setInfo(new DomStudentModelContextInfo(titleMap, descrMap));
        List<DomStudentModelCategory> catList = new ArrayList<>(3);
        model.setCategories(catList);
        for (int c = 0; c < 3; c++) {
            catList.add(new DomStudentModelCategory());
            List<DomStudentModelObj> objList = new ArrayList<>(2);
            catList.get(c).setObjectives(objList);
            Map<String, String> catTitle = new HashMap<>();
            Map<String, String> catDescr = new HashMap<>();
            catTitle.put("nl", "C cat " + c);
            catTitle.put("en", "C cat " + c);
            catDescr.put("nl", "C description");
            catDescr.put("en", "C description");
            catList.get(c).setInfo(new DomStudentModelContextInfo(catTitle, catDescr));
            for (int o = 0; o < 2; o++) {
                {
                    Map<String, String> objTitle = new HashMap<>();
                    Map<String, String> objDescr = new HashMap<>();
                    objTitle.put("nl", "C obj " + o);
                    objTitle.put("en", "C obj " + o);
                    objDescr.put("nl", "C description");
                    objDescr.put("en", "C description");
                    objList.add(new DomStudentModelObj());
                    objList.get(o).setInfo(new DomStudentModelContextInfo(objTitle, objDescr));
                }
            }
        }

        //build RestStudentModelContext
        //scoId =3, schoolId=3, modelId=1, courseId=. userId = 9 "user02"
        DomStudentModelData domData = new DomStudentModelData();
        DomStudentModelContextId modelId = new DomStudentModelContextId();
        modelId.setId(PersistentStudentModelContext.buildPersistenceId(1L));
        domData.setModelId(modelId);
        DomScoContextId scoId = new DomScoContextId();
        scoId.setId(PersistentScoContext.buildPersistenceId(3L));
        domData.setScoContextId(scoId);
        domData.setDomStudentModelStructureScore(model.generateStudentModelStructureScore());
        StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user02")
                .setDefaultHasRole()//
                .buildStudent();
        try {
            //update existing data
            state.setStudentModelData(domData);
            fail("There is a unique constraint, that did not work possible a column is NULL (classID?).");
        } catch (Dwo2Exception e) {
            //success
        }
        state = AnonDomainAuthorizer.build().submitUser("user04")
                .setDefaultHasRole()//
                .buildStudent();
        try {
            //update existing data
            state.setStudentModelData(domData);
            //success
        } catch (Dwo2Exception e) {
            fail("Something went wrong.");
        }
        PersistentStudentModelData data = StudentModelDataManager.findEntity(ScoContextManager.findEntity(3L), state.getContext().getUserCtx().getHasRole());
        assertEquals(data.getModelID().longValue(), 1L);
        assertEquals(data.getScoID().longValue(), 3L);
        assertEquals(data.getModelDataId().longValue(), 3L);
        //compare input and output
        // TODO review the generated test code and remove the default call to fail.
        //  fail("The test case is a prototype.");
    }

    /**
     * Test of getStudentModelDataScore method, of class StudentBuilder.
     */
    @Test
    public void testGetStudentModelData() throws Exception {
        System.out.println("getStudentModelData");

        StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user02")
                .setDefaultHasRole()//
                .buildStudent();
        try {
            DomScoContextId scoId = new DomScoContextId();
            scoId.setId(PersistentScoContext.buildPersistenceId(3L));
            state.getStudentModelData(scoId);
        } catch (Dwo2Exception e) {
            fail("Did not find matching student model data for a student.");
        }

    }

}
