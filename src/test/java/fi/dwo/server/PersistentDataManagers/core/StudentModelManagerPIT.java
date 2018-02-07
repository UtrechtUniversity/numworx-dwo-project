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
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
        JSONObject json = new JSONObject();
        json.put("nl", "Model A");
        modelA.setModelStructure(json);
        modelA.setSchoolID(2L);
        json = new JSONObject();
        json.put("nl", "Model B");
        modelB.setModelStructure(json);
        modelB.setSchoolID(2L);
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
    public void testCRUD() {
        // create

        try {
            System.out.println("create model");
            modelA = StudentModelManager.create(modelA);
            modelB = StudentModelManager.create(modelB);
        } catch (Exception e) {
            fail("AnalyticalModelManager.create() failed.");
        }

        PersistentStudentModelContext modelC = null;
        // recreate
        try {
            System.out.println("create model again");
            modelC = StudentModelManager.create(modelA);
            //should succeed            
        } catch (Exception e) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
            fail("AnalyticalModelManager.create() did not fail creating a copy of a model.");
        }

        // test delete
        try {
            System.out.println("delete second copy of modelA ");
            StudentModelManager.destroy(modelC.getModelID().longValue());
            //should succeed            
        } catch (Exception e) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
            fail("AnalyticalModelManager.delete() failed.");
        }

        try {
            System.out.println("delete second copy of modelA  again");
            StudentModelManager.destroy(modelC.getModelID().longValue());
            fail("AnalyticalModelManager.create() did not fail removing deleted opy.");
            //should succeed            
        } catch (Exception e) {
            //success
        }

        // test read single
        try {
            System.out.println("reading modelA ");
            PersistentStudentModelContext modelD = StudentModelManager.findEntity(modelB.getModelID());
            assertEquals(modelB.getModelID(), modelD.getModelID());
            JSONAssert.assertEquals(modelB.getModelStructure().toJSONString(), modelD.getModelStructure().toJSONString(), JSONCompareMode.NON_EXTENSIBLE);
            //should succeed            
        } catch (Exception e) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
            fail("Fetched model does not match model A.");
        }

        // test read schoolList
        PersistentSchool s = new PersistentSchool();
        s.setSchoolID(2L);
        List<PersistentStudentModelContext> modelList = StudentModelManager.findEntities(s);
        if (modelList.size() != 2) {
            fail("Did not find 2 models");
        }

        // test update single and check timestamps and version
        try {
            System.out.println("updating modelB ");
            int lock1 = modelB.getOptlock();
            long time1 = modelB.getLastChangeTimeStamp();
            modelB.getModelStructure().put("de", "German version");
            modelB = StudentModelManager.edit(modelB);
            long time2 = modelB.getLastChangeTimeStamp();
            int lock2 = modelB.getOptlock();
            //  check if modelStructure is updated.
            assertEquals(modelB.getModelStructure().get("de"), "German version");
            //  check if timeStamp is updated.
            if(time1>=time2) fail("Timestamps unchanged after update.");
            //check if lock is updated
            assertEquals(lock1 + 1, lock2);
            //should succeed            
        } catch (Exception e) {
            Logger.getLogger(StudentModelManagerPIT.class.getName()).log(Level.SEVERE, null, e);
            fail("Fetched model does not match model A.");
        }

    }

}
