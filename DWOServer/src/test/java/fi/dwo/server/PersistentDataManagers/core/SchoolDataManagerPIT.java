/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * SchoolManager persistence integration tests (PIT).  <p/>
 * 
 * Light testing. Every method assumes other SchoolManager methods work proper.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolDataManagerPIT {

    PersistentSchoolData schoolA = new PersistentSchoolData();
    PersistentSchoolData schoolB = new PersistentSchoolData();

    static DatabaseManager instance = null;
    
    public SchoolDataManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }
    
    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    @Before
    public void setUp() {
        instance.IntializeTestDatabase();
        schoolA.setSchoolID(3L);
        schoolB.setSchoolID(5L);
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }
   
    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testCRUD()  {
        // create
        try{
        System.out.println("create school");
        SchoolDataManager.create(schoolA);
        SchoolDataManager.create(schoolB);
        }catch(Exception e){
            fail("SchoolManager.create() failed.");
        }
        
        // recreate
        try{
        System.out.println("create school again");
            //should fail
        SchoolDataManager.create(schoolA);
            fail("SchoolDataManager.create() did not fail creating a copy of a school.");
        }catch(Exception e){
            //succeeded
        }
        
        //read 
        PersistentSchoolData school=null;
        try{
        System.out.println("read school");
        school = SchoolDataManager.findEntity(schoolA.getSchoolID());
        }catch(Exception e){
            fail("SchoolDataManager.read() failed.");
        }
        
        //update proper 
        try{
        System.out.println("update school");
        school = SchoolDataManager.findEntity(schoolA.getSchoolID());
        school.setSchoolData("{\"name\": \"schoolA\"}");
        schoolA = SchoolDataManager.edit(school);
        school = SchoolDataManager.findEntity(schoolA.getSchoolID());
        if(school.getSchoolData().compareTo(schoolA.getSchoolData())!=0){
            fail("SchoolDataManager.create() failed.");
        }
        }catch(Exception e){
            fail("SchoolDataManager.create() failed.");
        }
        //update should fail
        try{
        school = SchoolDataManager.findEntity(schoolA.getSchoolID());
        school.setSchoolID(schoolB.getSchoolID());
        SchoolDataManager.edit(school);
        fail("SchoolDataManager.create() failed.");
        }catch(Exception e){
            //works
        }
        
        //delete 
        System.out.println("delete school");
        school = SchoolDataManager.findEntity(schoolA.getSchoolID());
        SchoolDataManager.destroy(school.getSchoolID());
        school = SchoolDataManager.findEntity(schoolB.getSchoolID());
        SchoolDataManager.destroy(school.getSchoolID());
        try{
            school = SchoolDataManager.findEntity(schoolA.getSchoolID());
            if(school!=null) fail("SchoolDataManager.destroy() schoolA failed.");
        }catch(Exception e){
            // works
        }
        try{
        school = SchoolDataManager.findEntity(schoolB.getSchoolID());
        if(school!=null) fail("SchoolDataManager.destroy() schoolB failed.");
        }catch(Exception e){
            // works
        }
    }    
    
    /**
     * Test of destroy method, of class SchoolDataManager.
     */
    @Test
    public void testCreate() {
        //create
        try {
            SchoolDataManager.create(schoolA);
            SchoolDataManager.create(schoolB);
            PersistentSchoolData schoolOne = SchoolDataManager.findEntity(schoolA.getSchoolID());
            PersistentSchoolData schoolTwo = SchoolDataManager.findEntity(schoolB.getSchoolID());
//            if ((!schoolA.similar(schoolOne)) || (!schoolB.similar(schoolTwo))) {
//                fail("School created is different.");
//            }
        }
        catch (Exception e) {
            fail("Exception during create.");
        }
        //recreate
        try {
            SchoolDataManager.create(schoolA);
            SchoolDataManager.create(schoolB);
            fail("Creating double copy should not work.");
        }
        catch (Exception e) {
            //works!
        }

        //cleanup
        try {
            SchoolDataManager.destroy(SchoolDataManager.findEntity(schoolA.getSchoolID()).getSchoolID());
            SchoolDataManager.destroy(SchoolDataManager.findEntity(schoolB.getSchoolID()).getSchoolID());
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of destroy method, of class SchoolDataManager.
     */
    @Test
    public void testEdit() {
        SchoolDataManager.create(schoolA);
        SchoolDataManager.create(schoolB);
        // edit
        try {
            System.out.println("update school");
            PersistentSchoolData school = SchoolDataManager.findEntity(schoolA.getSchoolID());
            school.setSchoolData(schoolB.getSchoolData());
            SchoolDataManager.edit(school);
            school = SchoolDataManager.findEntity(schoolA.getSchoolID());
            if (school.getSchoolData().compareTo(schoolB.getSchoolData()) != 0) {
                fail("SchoolDataManager.edit() failed.");
            }
            school.setSchoolData(schoolA.getSchoolData());
//            if (!school.similar(schoolA)) {
//                fail("SchoolDataManager.edit() failed.");
//            }
        }
        catch (Exception e) {
            fail("SchoolDataManager.edit() failed.");
        }

        //update should fail
        try {
            System.out.println("update school");
            PersistentSchoolData school = SchoolDataManager.findEntity(schoolA.getSchoolID());
            school.setSchoolID(schoolB.getSchoolID());
            SchoolDataManager.edit(school);
            school = SchoolDataManager.findEntity(schoolA.getSchoolID());
            if (school == null) {
                fail("SchoolDataManager.edit() failed. School disappeared.");
            }
        }
        catch (Exception e) {
            // works!
        }

        //cleanup
        try {
            SchoolDataManager.destroy(SchoolDataManager.findEntity(schoolA.getSchoolID()).getSchoolID());
            SchoolDataManager.destroy(SchoolDataManager.findEntity(schoolB.getSchoolID()).getSchoolID());
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of destroy method, of class SchoolDataManager.
     */
    @Test
    public void testDestroy() {
        SchoolDataManager.create(schoolA);
        System.out.println("destroy");
        Integer id = null;
        try {
            PersistentSchoolData school = SchoolDataManager.findEntity(schoolA.getSchoolID());
            SchoolDataManager.destroy(school.getSchoolID());
            try {
                school = SchoolDataManager.findEntity(school.getSchoolID());
                if (school != null) {
                    fail("School not destroyed.");
                }
            }
            catch (Exception e) {
                //works
            }
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of findEntities method, of class SchoolDataManager.
     */
    //    @Test
    public void testFindEntities_0args() {
        System.out.println("findEntities");
        List<PersistentSchoolData> expResult = null;
        List<PersistentSchoolData> result = SchoolDataManager.findEntities();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntities method, of class SchoolDataManager.
     */
//    @Test
    public void testFindEntities_int_int() {
        System.out.println("findEntities");
        int maxResults = 0;
        int firstResult = 0;
        List<PersistentSchoolData> expResult = null;
        List<PersistentSchoolData> result = SchoolDataManager.findEntities(maxResults, firstResult);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntity method, of class SchoolDataManager.
     */
    public void testFindEntity() {
        System.out.println("findEntity");
        Long id = null;
        PersistentSchoolData expResult = null;
        PersistentSchoolData result = SchoolDataManager.findEntity(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEntityCount method, of class SchoolDataManager.
     */
    public void testGetEntityCount() {
        System.out.println("getEntityCount");
        int expResult = 0;
        int result = SchoolDataManager.getEntityCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findBySchoolName method, of class SchoolDataManager.
     */
    @Test
    public void testFindBySchoolLogin() {
        try {
            SchoolDataManager.create(schoolA);
            PersistentSchoolData result = SchoolDataManager.findEntity(schoolA.getSchoolID());
//            if (!result.similar(schoolA)) {
//                fail("Found different school as created.");
//            }
            SchoolDataManager.destroy(result.getSchoolID());
        }
        catch (Exception e) {
            fail("Exception during find.");
        }
    }

}
