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
 * SchoolManager persistence integration tests (PIT).  <p/>
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
        json.put("nl","Model A");
        modelA.setModelStructure(json);
        modelA.setSchoolID(2L);
        json = new JSONObject();
        json.put("nl","Model B");
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
    public void testDomAnalyticalModelTemplateTree()  {
        Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
        DomAnalyticalModelTemplateTree tree = new DomAnalyticalModelTemplateTree("nl","root");
        DomJsonModelTemplateNode c1 = new DomJsonModelTemplateNode("nl","1");
        c1.getChildren().add(new DomJsonModelTemplateNode("nl","1.1"));
        c1.getChildren().add(new DomJsonModelTemplateNode("nl","1.2"));
        DomJsonModelTemplateNode c2 = new DomJsonModelTemplateNode("nl","2");
        c2.getChildren().add(new DomJsonModelTemplateNode("nl","2.1"));
        c2.getChildren().add(new DomJsonModelTemplateNode("nl","2.2"));
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
//        if(out.compareTo(jsonTree)!=0){
//            fail("Marshalling demarshalling is not identical.");
//        }
    }

     /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testDomAnalyticalModelScoreTree()  {
        Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
        DomAnalyticalModelScoreTree tree = new DomAnalyticalModelScoreTree("nl","root",0);
        DomJsonModelScoreNode c1 = new DomJsonModelScoreNode("nl","1",1.0);
        c1.getChildren().add(new DomJsonModelScoreNode("nl","1.1",1.0));
        c1.getChildren().add(new DomJsonModelScoreNode("nl","1.2",1.0));
        DomJsonModelScoreNode c2 = new DomJsonModelScoreNode("nl","2",1.0);
        c2.getChildren().add(new DomJsonModelScoreNode("nl","2.1",1.0));
        c2.getChildren().add(new DomJsonModelScoreNode("nl","2.2",1.0));
        tree.getChildren().add(c1);
        tree.getChildren().add(c2);
        String jsonTree = g.serialize(tree);
        System.out.println(jsonTree);        
        DomAnalyticalModelScoreTree rTree = g.deserialize(jsonTree, DomAnalyticalModelScoreTree.class);
        //rTree.reCalculate();     
        String out = g.serialize(rTree);
        System.out.println(out);
        if(out.compareTo(jsonTree)!=0){
            fail("Marshalling demarshalling is not identical.");
        }
    }

    
    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testCRUD()  {
        // create
        try{
        System.out.println("create model");
        StudentModelManager.create(modelA);
        StudentModelManager.create(modelB);
        }catch(Exception e){
            fail("AnalyticalModelManager.create() failed.");
        }
        
        // recreate
        try{
        System.out.println("create model again");
        StudentModelManager.create(modelA);
            //should succeed            
        }catch(Exception e){
            fail("AnalyticalModelManager.create() did not fail creating a copy of a model.");
        }
        
        // test read
        PersistentSchool s = new PersistentSchool();
        s.setSchoolID(2L);
        List<PersistentStudentModelContext> modelList = StudentModelManager.findEntities(s);
        if(modelList.size()!=2){
            fail("Did not find 2 models");
        }
//        
//        //read 
//        StudentModelManager model=null;
//        try{
//        System.out.println("read school");
//        }catch(Exception e){
//            fail("SchoolManager.read() failed.");
//        }
//        
//        //update proper 
//        try{
//        System.out.println("update school");
//        model = SchoolManager.findBySchoolLogin(modelA.getSchoolLogin());
//        model.setSchoolName(modelB.getSchoolName());
//        SchoolManager.edit(model);
//        model = SchoolManager.findBySchoolLogin(modelA.getSchoolLogin());
//        if(model.getSchoolName().compareTo(modelB.getSchoolName())!=0){
//            fail("SchoolManager.create() failed.");
//        }
//        }catch(Exception e){
//            fail("SchoolManager.create() failed.");
//        }
//        //update should fail
//        try{
//        model = SchoolManager.findBySchoolLogin(modelA.getSchoolLogin());
//        model.setSchoolLogin(modelB.getSchoolLogin());
//        SchoolManager.edit(model);
//        fail("SchoolManager.create() failed.");
//        }catch(Exception e){
//            //works
//        }
//        
//        //delete 
//        System.out.println("delete school");
//        model = SchoolManager.findBySchoolLogin(modelA.getSchoolLogin());
//        SchoolManager.destroy(model.getSchoolID());
//        model = SchoolManager.findBySchoolLogin(modelB.getSchoolLogin());
//        SchoolManager.destroy(model.getSchoolID());
//        try{
//            model = SchoolManager.findBySchoolLogin(modelA.getSchoolLogin());
//            if(model!=null) fail("SchoolManager.destroy() schoolA failed.");
//        }catch(Exception e){
//            // works
//        }
//        try{
//        model = SchoolManager.findBySchoolLogin(modelB.getSchoolLogin());
//        if(model!=null) fail("SchoolManager.destroy() schoolB failed.");
//        }catch(Exception e){
//            // works
//        }
    }    
   

}
