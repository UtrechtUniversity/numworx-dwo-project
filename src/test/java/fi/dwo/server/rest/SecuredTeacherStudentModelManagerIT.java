/**
 * Copyrighted Feb 12, 2018
 */
package fi.dwo.server.rest;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author plas0006
 */
public class SecuredTeacherStudentModelManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherStudentModelManagerIT.class.getName());

    static DatabaseManager dbInstance = null;

    public SecuredTeacherStudentModelManagerIT() {
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
     * Test of getStudentModels method, of class
     * SecuredTeacherStudentModelManager.
     */
    @Test
    public void testGetStudentModels() {

        System.out.println("getStudentModels");
        SecurityContext sc = new TestSecurityContext("user03", RoleType.TEACHER);
        RestContext restContext = new RestContext();
        DomHasRole hr = new DomHasRole();
        //MYSQL;PersistentHasRole;00000000000000000010;00000000000000000003 TEACHER School01
        PersistentHasRolePK key = new PersistentHasRolePK(10L, 3L);
        PersistenceId id = PersistentHasRole.buildPersistenceId(key);
        hr.setId(id);
        hr.setUserId(PersistentUser.buildPersistenceId(10L));
        hr.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(10L));
        DomContext context = new DomContext();
        context.setDomHasRole(hr);
        restContext.setRestContext(context);
        SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
        List<DomStudentModelContext> expResult = null;
        List<DomStudentModelContext> result = instance.getStudentModels(sc, restContext);
        assertEquals(2, result.size());
    }

    /**
     * Test of getStudentModels method, of class
     * SecuredTeacherStudentModelManager.
     */
    @Test
    public void testGetReducedStudentModels() {

        System.out.println("getReducedStudentModels");
        SecurityContext sc = new TestSecurityContext("user03", RoleType.TEACHER);
        RestContext restContext = new RestContext();
        DomHasRole hr = new DomHasRole();
        //MYSQL;PersistentHasRole;00000000000000000010;00000000000000000003 TEACHER School01
        PersistentHasRolePK key = new PersistentHasRolePK(10L, 3L);
        PersistenceId id = PersistentHasRole.buildPersistenceId(key);
        hr.setId(id);
        hr.setUserId(PersistentUser.buildPersistenceId(10L));
        hr.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(10L));
        DomContext context = new DomContext();
        context.setDomHasRole(hr);
        restContext.setRestContext(context);
        SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
        List<DomStudentModelContext> expResult = null;
        List<DomStudentModelContext> result = instance.getReducedStudentModels(sc, restContext);
        assertEquals(2, result.size());
        assertNull(result.get(0).getModelStructure().getInfo().getDescription());
    }
  
    
    
    /**
     * Test of addStudentModel method, of class
     * SecuredTeacherStudentModelManager.
     * @throws JSONException 
     */
    @Test
    public void testAddStudentModel() throws JSONException {
        System.out.println("addStudentModel");

        //build security context
        SecurityContext sc = new TestSecurityContext("user03", RoleType.TEACHER);
        RestContext restContext = new RestContext();
        DomHasRole hr = new DomHasRole();
        //MYSQL;PersistentHasRole;00000000000000000010;00000000000000000003 TEACHER School01
        PersistentHasRolePK key = new PersistentHasRolePK(10L, 3L);
        PersistenceId id = PersistentHasRole.buildPersistenceId(key);
        hr.setId(id);
        hr.setUserId(PersistentUser.buildPersistenceId(10L));
        hr.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(10L));

        //build RestContext
        DomContext context = new DomContext();
        context.setDomHasRole(hr);
        restContext.setRestContext(context);

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
        RestStudentModelContext restModel = new RestStudentModelContext();
        restModel.setRestContext(context);
        DomStudentModelContext domModel = new DomStudentModelContext();
        domModel.setModelStructure(model);
        //domModel
///        DomStudentModelStructureScore ds = domModel.getModelStructure().generateStudentModelStructureScore();
        
        restModel.setDomStudentModelContext(domModel);
        DomStudentModelContext expResult = null;
        SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
        expResult = instance.addStudentModel(sc, restModel);

        jsonAssert(restModel, expResult);
        
    }

    private void jsonAssert(RestStudentModelContext restModel, DomStudentModelContext expResult)
        throws JSONException {
      //comparing structure attribute between submitted data and returned data.
      Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();        
      String expModel = g.serialize(expResult.getModelStructure());
      System.out.println(expModel);

      String out = g.serialize(restModel);
      String jsonModel = g.serialize(restModel.getDomStudentModelContext().getModelStructure());
      System.out.println(jsonModel);
      JSONAssert.assertEquals(expModel, jsonModel, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test public void testRemoveStudentModels() throws Exception {
      System.out.println("removeStudentModels");
      SecurityContext sc = new TestSecurityContext("user03", RoleType.TEACHER);
      DomHasRole hr = new DomHasRole();
      //MYSQL;PersistentHasRole;00000000000000000010;00000000000000000003 TEACHER School01
      PersistentHasRolePK key = new PersistentHasRolePK(10L, 3L);
      PersistenceId id = PersistentHasRole.buildPersistenceId(key);
      hr.setId(id);
      hr.setUserId(PersistentUser.buildPersistenceId(10L));
      hr.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(10L));
      DomContext context = new DomContext();
      context.setDomHasRole(hr);
      SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
      RestStudentModelContext model = new RestStudentModelContext();
      model.setRestContext(context);
      DomStudentModelContext dom = new DomStudentModelContext();
      dom.setId(PersistentStudentModelContext.buildPersistenceId(1L));
      model.setDomStudentModelContext(dom);
      Boolean result = instance.removeStudentModel(sc, model);
      assertTrue(result.booleanValue());
      assertEquals("size", 1, StudentModelContextManager.findEntities().size());
    }
    
    @Test public void testUpdateStudentModel() throws Exception {
      System.out.println("updateStudentModels");
      SecurityContext sc = new TestSecurityContext("user03", RoleType.TEACHER);
      DomHasRole hr = new DomHasRole();
      //MYSQL;PersistentHasRole;00000000000000000010;00000000000000000003 TEACHER School01
      PersistentHasRolePK key = new PersistentHasRolePK(10L, 3L);
      PersistenceId id = PersistentHasRole.buildPersistenceId(key);
      hr.setId(id);
      hr.setUserId(PersistentUser.buildPersistenceId(10L));
      hr.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(10L));
      DomContext context = new DomContext();
      context.setDomHasRole(hr);
      SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
      RestStudentModelContext model = new RestStudentModelContext();
      model.setRestContext(context);
      DomStudentModelContext dom = StudentModelContextManager.findEntity(1L).buildDomStudentModelContext();
      model.setDomStudentModelContext(dom);
      dom.getModelStructure().getInfo().getTitle().put("jp", "?????");
      DomStudentModelContext result = instance.updateStudentModel(sc, model);
      jsonAssert(model,result);
    }
    
}
