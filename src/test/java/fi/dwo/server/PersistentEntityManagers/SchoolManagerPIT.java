/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;
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
public class SchoolManagerPIT {

    PersistentSchool schoolA = new PersistentSchool();
    PersistentSchool schoolB = new PersistentSchool();

    static DatabaseManager instance = null;
    
    public SchoolManagerPIT() {
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
        schoolA.setSchoolName("De School A");
        schoolA.setImage(null);
        Date d = DwoDateUtilities.getCurrentDwoDate();
        schoolA.setExpire(d);
        schoolA.setExport(false);
        schoolA.setSchoolRights("_");
        schoolA.setSchoolLogin("JunitTestSchoolA");

        schoolB.setSchoolName("De School B");
        schoolB.setImage(null);
        d = DwoDateUtilities.getCurrentDwoDate();
        schoolB.setExpire(d);
        schoolB.setExport(false);
        schoolB.setSchoolRights("_");
        schoolB.setSchoolLogin("JunitTestSchoolB");
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
    public void testCRUD()  {
        // create
        try{
        System.out.println("create school");
        SchoolManager.create(schoolA);
        SchoolManager.create(schoolB);
        }catch(Exception e){
            fail("SchoolManager.create() failed.");
        }
        
        // recreate
        try{
        System.out.println("create school again");
            //should fail
        SchoolManager.create(schoolA);
            fail("SchoolManager.create() did not fail creating a copy of a school.");
        }catch(Exception e){
            //succeeded
        }
        
        //read 
        PersistentSchool school=null;
        try{
        System.out.println("read school");
        school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
        }catch(Exception e){
            fail("SchoolManager.read() failed.");
        }
        
        //update proper 
        try{
        System.out.println("update school");
        school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
        school.setSchoolName(schoolB.getSchoolName());
        SchoolManager.edit(school);
        school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
        if(school.getSchoolName().compareTo(schoolB.getSchoolName())!=0){
            fail("SchoolManager.create() failed.");
        }
        }catch(Exception e){
            fail("SchoolManager.create() failed.");
        }
        //update should fail
        try{
        school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
        school.setSchoolLogin(schoolB.getSchoolLogin());
        SchoolManager.edit(school);
        fail("SchoolManager.create() failed.");
        }catch(Exception e){
            //works
        }
        
        //delete 
        System.out.println("delete school");
        school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
        SchoolManager.destroy(school.getSchoolID());
        school = SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin());
        SchoolManager.destroy(school.getSchoolID());
        try{
            school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            if(school!=null) fail("SchoolManager.destroy() schoolA failed.");
        }catch(Exception e){
            // works
        }
        try{
        school = SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin());
        if(school!=null) fail("SchoolManager.destroy() schoolB failed.");
        }catch(Exception e){
            // works
        }
    }    
    
    /**
     * Test of destroy method, of class SchoolManager.
     */
    @Test
    public void testCreate() {
        //create
        try {
            SchoolManager.create(schoolA);
            SchoolManager.create(schoolB);
            PersistentSchool schoolOne = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            PersistentSchool schoolTwo = SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin());
            if ((!schoolA.similar(schoolOne)) || (!schoolB.similar(schoolTwo))) {
                fail("School created is different.");
            }
        }
        catch (Exception e) {
            fail("Exception during create.");
        }
        //recreate
        try {
            SchoolManager.create(schoolA);
            SchoolManager.create(schoolB);
            fail("Creating double copy should not work.");
        }
        catch (Exception e) {
            //works!
        }

        //cleanup
        try {
            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin()).getSchoolID());
            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin()).getSchoolID());
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of destroy method, of class SchoolManager.
     */
    @Test
    public void testEdit() {
        SchoolManager.create(schoolA);
        SchoolManager.create(schoolB);
        // edit
        try {
            System.out.println("update school");
            PersistentSchool school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            school.setSchoolName(schoolB.getSchoolName());
            SchoolManager.edit(school);
            school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            if (school.getSchoolName().compareTo(schoolB.getSchoolName()) != 0) {
                fail("SchoolManager.edit() failed.");
            }
            school.setSchoolName(schoolA.getSchoolName());
            if (!school.similar(schoolA)) {
                fail("SchoolManager.edit() failed.");
            }
        }
        catch (Exception e) {
            fail("SchoolManager.edit() failed.");
        }

        //update should fail
        try {
            System.out.println("update school");
            PersistentSchool school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            school.setSchoolLogin(schoolB.getSchoolLogin());
            SchoolManager.edit(school);
            school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            if (school == null) {
                fail("SchoolManager.edit() failed. School disappeared.");
            }
        }
        catch (Exception e) {
            // works!
        }

        //cleanup
        try {
            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin()).getSchoolID());
            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin()).getSchoolID());
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of destroy method, of class SchoolManager.
     */
    @Test
    public void testDestroy() {
        SchoolManager.create(schoolA);
        System.out.println("destroy");
        Integer id = null;
        try {
            PersistentSchool school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            SchoolManager.destroy(school.getSchoolID());
            try {
                school = SchoolManager.findBySchoolLogin(school.getSchoolLogin());
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
     * Test of findEntities method, of class SchoolManager.
     */
    //    @Test
    public void testFindEntities_0args() {
        System.out.println("findEntities");
        List<PersistentSchool> expResult = null;
        List<PersistentSchool> result = SchoolManager.findEntities();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntities method, of class SchoolManager.
     */
//    @Test
    public void testFindEntities_int_int() {
        System.out.println("findEntities");
        int maxResults = 0;
        int firstResult = 0;
        List<PersistentSchool> expResult = null;
        List<PersistentSchool> result = SchoolManager.findEntities(maxResults, firstResult);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntity method, of class SchoolManager.
     */
    public void testFindEntity() {
        System.out.println("findEntity");
        Long id = null;
        PersistentSchool expResult = null;
        PersistentSchool result = SchoolManager.findEntity(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEntityCount method, of class SchoolManager.
     */
    public void testGetEntityCount() {
        System.out.println("getEntityCount");
        int expResult = 0;
        int result = SchoolManager.getEntityCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findBySchoolName method, of class SchoolManager.
     */
    @Test
    public void testFindBySchoolLogin() {
        try {
            SchoolManager.create(schoolA);
            PersistentSchool result = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
            if (!result.similar(schoolA)) {
                fail("Found different school as created.");
            }
            SchoolManager.destroy(result.getSchoolID());
        }
        catch (Exception e) {
            fail("Exception during find.");
        }
    }

}
