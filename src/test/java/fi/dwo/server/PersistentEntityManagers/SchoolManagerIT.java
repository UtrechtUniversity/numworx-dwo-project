/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolManagerIT {
    
    public SchoolManagerIT() {
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
     * Test of create method, of class SchoolManager.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        PersistentSchool schoolIn = new PersistentSchool();
        schoolIn.setSchoolName("De School");
        schoolIn.setImage(null);
        schoolIn.setExpire(new Date());
        schoolIn.setExport(false);
        schoolIn.setSchoolRights("_");
        schoolIn.setSchoollogin("JunitTestSchool");
        PersistentSchool schoolOut = SchoolManager.findBySchoolName(schoolIn.getSchoolName());
        if(schoolOut==null){
            fail("Schoollogin does not exist.");
        };
    }
//
//    /**
//     * Test of edit method, of class SchoolManager.
//     */
//    @Test
//    public void testEdit() throws Exception {
//        System.out.println("edit");
//        PersistentSchool school = null;
//        SchoolManager.edit(school);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of destroy method, of class SchoolManager.
//     */
//    @Test
//    public void testDestroy() {
//        System.out.println("destroy");
//        Integer id = null;
//        SchoolManager.destroy(id);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findEntities method, of class SchoolManager.
//     */
//    @Test
//    public void testFindEntities_0args() {
//        System.out.println("findEntities");
//        List<PersistentSchool> expResult = null;
//        List<PersistentSchool> result = SchoolManager.findEntities();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findEntities method, of class SchoolManager.
//     */
//    @Test
//    public void testFindEntities_int_int() {
//        System.out.println("findEntities");
//        int maxResults = 0;
//        int firstResult = 0;
//        List<PersistentSchool> expResult = null;
//        List<PersistentSchool> result = SchoolManager.findEntities(maxResults, firstResult);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findEntity method, of class SchoolManager.
//     */
//    @Test
//    public void testFindEntity() {
//        System.out.println("findEntity");
//        Integer id = null;
//        PersistentSchool expResult = null;
//        PersistentSchool result = SchoolManager.findEntity(id);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEntityCount method, of class SchoolManager.
//     */
//    @Test
//    public void testGetEntityCount() {
//        System.out.println("getEntityCount");
//        int expResult = 0;
//        int result = SchoolManager.getEntityCount();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findBySchoolName method, of class SchoolManager.
//     */
//    @Test
//    public void testFindBySchoolName() {
//        System.out.println("findBySchoolName");
//        String schoolName = "";
//        PersistentSchool expResult = null;
//        PersistentSchool result = SchoolManager.findBySchoolName(schoolName);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
}
