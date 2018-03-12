/**
 * Copyrighted Mar 12, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gert van der Plas
 */
public class StudentBuilderTest {
    
    public StudentBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of updateStudentModelData method, of class StudentBuilder.
     */
    @Test
    public void testUpdateStudentModelData() throws Exception {


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
        DomStudentModelData domData = new DomStudentModelData();
        domData.setModelId(new DomStudentModelContextId());
        domData.setScoContextId(new DomScoContextId());
        domData.setDomStudentModelStructureScore(model.generateStudentModelStructureScore());
        StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user05")
                .setDefaultHasRole()
                .buildStudent();
        state.updateStudentModelData(domData);
        //DomStudentModelData expResult = state.updateStudentModelData(domData);
        
        //compare input and output
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
