/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.core;

import nl.uu.fi.dwo.rest.dom.entities.DomAnalyticalModelScoreTree;
import nl.uu.fi.dwo.rest.dom.entities.DomJsonModelScoreNode;
import nl.uu.fi.dwo.rest.dom.entities.DomAnalyticalModelTemplateTree;
import nl.uu.fi.dwo.rest.dom.entities.DomJsonModelTemplateNode;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.util.UEscape;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * SchoolManager persistence integration tests (PIT).
 * <p/>
 *
 * Light testing. Every method assumes other SchoolManager methods work proper.
 *
 * @author G.A.J. van der Plas
 */
public class StudentModelManagerPIT {

    PersistentStudentModelContext modelA = new PersistentStudentModelContext();
    PersistentStudentModelContext modelB = new PersistentStudentModelContext();

    static DatabaseManager instance = null;

    public StudentModelManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
    public static void setUpClass() {
//        DwoEmfFactory.setDefaultEntityManagerFactory();
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
//        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    @Before
    public void setUp() {
//        JSONObject json = new JSONObject();
//        json.put("nl", "Model A");
//        modelA.setModelStructure(json);
//        modelA.setSchoolID(2L);
//        json = new JSONObject();
//        json.put("nl", "Model B");
//        modelB.setModelStructure(json);
//        modelB.setSchoolID(2L);
        instance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testDomStudentModelContextTree() {
        Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
        DomAnalyticalModelTemplateTree tree = new DomAnalyticalModelTemplateTree("nl", "root");
        DomJsonModelTemplateNode c1 = new DomJsonModelTemplateNode("nl", "1");
        c1.getChildren().add(new DomJsonModelTemplateNode("nl", "1.1"));
        c1.getChildren().add(new DomJsonModelTemplateNode("nl", "1.2"));
        DomJsonModelTemplateNode c2 = new DomJsonModelTemplateNode("nl", "2");
        c2.getChildren().add(new DomJsonModelTemplateNode("nl", "2.1"));
        c2.getChildren().add(new DomJsonModelTemplateNode("nl", "2.2"));
        tree.getChildren().add(c1);
        tree.getChildren().add(c2);
        String jsonTree = g.serialize(tree);
        System.out.println(jsonTree);
        DomAnalyticalModelTemplateTree rTree = g.deserialize(jsonTree, DomAnalyticalModelTemplateTree.class);
        String out = g.serialize(rTree);
        System.out.println(out);
        try {
            JSONAssert.assertEquals(out, jsonTree, JSONCompareMode.NON_EXTENSIBLE);
        } catch (JSONException ex) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Json Exception.");
        }
    }

    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testDomStudentModelDataTree() {
        Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
        DomAnalyticalModelScoreTree tree = new DomAnalyticalModelScoreTree("nl", "root", 0);
        DomJsonModelScoreNode c1 = new DomJsonModelScoreNode("nl", "1", 1.0);
        c1.getChildren().add(new DomJsonModelScoreNode("nl", "1.1", 1.0));
        c1.getChildren().add(new DomJsonModelScoreNode("nl", "1.2", 1.0));
        DomJsonModelScoreNode c2 = new DomJsonModelScoreNode("nl", "2", 1.0);
        c2.getChildren().add(new DomJsonModelScoreNode("nl", "2.1", 1.0));
        c2.getChildren().add(new DomJsonModelScoreNode("nl", "2.2", 1.0));
        tree.getChildren().add(c1);
        tree.getChildren().add(c2);
        String jsonTree = g.serialize(tree);
        System.out.println(jsonTree);
        DomAnalyticalModelScoreTree rTree = g.deserialize(jsonTree, DomAnalyticalModelScoreTree.class);
        //rTree.reCalculate();     
        String out = g.serialize(rTree);
        System.out.println(out);
        try {
            JSONAssert.assertEquals(out, jsonTree, JSONCompareMode.NON_EXTENSIBLE);
        } catch (JSONException ex) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Json Exception.");
        }
    }

    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testDomStudentModelContextSerialization() {
        DomStudentModelStructure model = new DomStudentModelStructure();
        Map<String, String> titleMap = new HashMap<>();
        Map<String, String> descrMap = new HashMap<>();
        titleMap.put("nl", "A mödel");
        titleMap.put("en", "A mødel");
        descrMap.put("nl", "A déscription");
        descrMap.put("en", "A dèscription");
        model.setInfo(new DomStudentModelContextInfo(titleMap, descrMap));
        List<DomStudentModelCategory> catList = new ArrayList<>(3);
        model.setCategories(catList);
        for (int c = 0; c < 3; c++) {
            catList.add(new DomStudentModelCategory());
            List<DomStudentModelObj> objList = new ArrayList<>(2);
            catList.get(c).setObjectives(objList);
            Map<String, String> catTitle = new HashMap<>();
            Map<String, String> catDescr = new HashMap<>();
            catTitle.put("nl", "A cat " + c);
            catTitle.put("en", "A cat " + c);
            catDescr.put("nl", "A description");
            catDescr.put("en", "A description");
            catList.get(c).setInfo(new DomStudentModelContextInfo(catTitle, catDescr));
            for (int o = 0; o < 2; o++) {
                {
                    Map<String, String> objTitle = new HashMap<>();
                    Map<String, String> objDescr = new HashMap<>();
                    objTitle.put("nl", "A öbj " + o);
                    objTitle.put("en", "A öbj " + o);
                    objDescr.put("nl", "A dëscription");
                    objDescr.put("en", "A dëscription");
                    objList.add(new DomStudentModelObj());
                    objList.get(o).setInfo(new DomStudentModelContextInfo(objTitle, objDescr));
                }
            }
        }

        Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
        String jsonModel = g.serialize(model);
        System.out.println(jsonModel);

        DomStudentModelStructure rModel = g.deserialize(jsonModel, DomStudentModelStructure.class);
        String out = g.serialize(rModel);
        System.out.println(out);
        try {
            JSONAssert.assertEquals(out, jsonModel, JSONCompareMode.NON_EXTENSIBLE);
        } catch (JSONException ex) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Json Exception.");
        }

        // create
        PersistentStudentModelContext modelA = new PersistentStudentModelContext();
        try {
            System.out.println("create model");

            modelA.setSchoolID(1L);
            modelA.setModelStructure(model);
            modelA = StudentModelContextManager.create(modelA);
        } catch (Exception e) {
            fail("AnalyticalModelManager.create() failed.");
        }

        //get
        try {
            System.out.println("reading modelA ");
            PersistentStudentModelContext modelD = StudentModelContextManager.findEntity(modelA.getModelID());
            assertEquals(modelA.getModelID(), modelD.getModelID());
            String expected = UEscape.convertUEsc(modelA.getModelStructure().getInfo().getTitle().toString());
			String was = UEscape.convertUEsc(modelD.getModelStructure().getInfo().getTitle().toString());
			assertEquals(expected, was);
//            JSONAssert.assertEquals(modelA.getModelStructure().toJSONString(), modelD.getModelStructure().toJSONString(), JSONCompareMode.NON_EXTENSIBLE);
            //should succeed            
        } catch (Exception e) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
            fail("Fetched model does not match model A.");
        }
    }
//
//    /**
//     * Light testing CRUD and more of class SchoolManager.
//     */
//    @Test
//    public void testCRUD() {
//        // create
//
//        try {
//            System.out.println("create model");
//            modelA = StudentModelContextManager.create(modelA);
//            modelB = StudentModelContextManager.create(modelB);
//        } catch (Exception e) {
//            fail("AnalyticalModelManager.create() failed.");
//        }
//
//        PersistentStudentModelContext modelC = null;
//        // recreate
//        try {
//            System.out.println("create model again");
//            modelC = StudentModelContextManager.create(modelA);
//            //should succeed            
//        } catch (PersistenceException e) {
//            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
//            fail("AnalyticalModelManager.create() did not fail creating a copy of a model.");
//        }
//
//        // test delete
//        try {
//            System.out.println("delete second copy of modelA ");
//            StudentModelContextManager.destroy(modelC.getModelID().longValue());
//            //should succeed            
//        } catch (Exception e) {
//            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
//            fail("AnalyticalModelManager.delete() failed.");
//        }
//
//        try {
//            System.out.println("delete second copy of modelA  again");
//            StudentModelContextManager.destroy(modelC.getModelID().longValue());
//            fail("AnalyticalModelManager.create() did not fail removing deleted opy.");
//            //should succeed            
//        } catch (Exception e) {
//            //success
//        }
//
//        // test read single
//        try {
//            System.out.println("reading modelA ");
//            PersistentStudentModelContext modelD = StudentModelContextManager.findEntity(modelB.getModelID());
//            assertEquals(modelB.getModelID(), modelD.getModelID());
//            JSONAssert.assertEquals(modelB.getModelStructure().toJSONString(), modelD.getModelStructure().toJSONString(), JSONCompareMode.NON_EXTENSIBLE);
//            //should succeed            
//        } catch (Exception e) {
//            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
//            fail("Fetched model does not match model A.");
//        }
//
//        try {
//            System.out.println("reading modelA ");
//            PersistentStudentModelContext modelD = StudentModelContextManager.findEntity(modelB.getModelID());
//            assertEquals(modelB.getModelID(), modelD.getModelID());
//            JSONAssert.assertEquals(modelB.getModelStructure().toJSONString(), modelD.getModelStructure().toJSONString(), JSONCompareMode.NON_EXTENSIBLE);
//            //should succeed            
//        } catch (Exception e) {
//            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
//            fail("Fetched model does not match model A.");
//        }
//
//        // test update single and check timestamps and version
//        try {
//            System.out.println("updating modelB ");
//            int lock1 = modelB.getOptlock();
//            long time1 = modelB.getLastChangeTimeStamp();
//            modelB.getModelStructure().put("de", "German version");
//            modelB = StudentModelContextManager.edit(modelB);
//            long time2 = modelB.getLastChangeTimeStamp();
//            int lock2 = modelB.getOptlock();
//            //  check if modelStructure is updated.
//            assertEquals(modelB.getModelStructure().get("de"), "German version");
//            //  check if timeStamp is updated.
//            if (time1 >= time2) {
//                fail("Timestamps unchanged after update.");
//            }
//            //check if lock is updated
//            assertEquals(lock1 + 1, lock2);
//            //should succeed            
//        } catch (Exception e) {
//            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
//            fail("Fetched model does not match model A.");
//        }
//    }

    @Test
    public void testGetReducedList() throws Exception {
    	
    	PersistentSchool school = new PersistentSchool(3L);
    	PersistentDwoProfile profile = new PersistentDwoProfile(1L);
    	List<PersistentStudentModelContext> result = StudentModelContextManager.findReducedEntities(school, profile);
    	assertNotNull (result);
    	assertEquals(2, result.size());
    	
    	school = new PersistentSchool(1234L);
    	result = StudentModelContextManager.findReducedEntities(school, profile);
    	assertNotNull (result);
    	assertEquals(0, result.size());
   	
    }
    
    @Test public void testAddProfile() throws Exception {
    	PersistentSchool school = new PersistentSchool(3L);
    	PersistentDwoProfile profile = new PersistentDwoProfile(3L);
    	modelA.setDwoProfileID(1L);
    	modelA.setSchoolID(MethodManager.NUL);   	
    	DomStudentModelStructure s = new DomStudentModelStructure();
		modelA.setModelStructure(s);
		StudentModelContextManager.create(modelA);
		StudentModelContextManager.addProfile(modelA, profile);
		
		List<PersistentStudentModelContext> list = StudentModelContextManager.findReducedEntities(school, profile);
    	
		assertEquals(3, list.size());
    	profile.setDwoProfileID(4L);
    	list = StudentModelContextManager.findReducedEntities(school, profile);
    	assertEquals(2, list.size());
    	profile.setDwoProfileID(1L);
    	school.setSchoolID(1L);
       	list = StudentModelContextManager.findReducedEntities(school, profile);
    	assertEquals(1, list.size());
    	
  	
    }
}
