/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.mysql;

import fi.dwo.server.testutil.Parameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author G.A.J. van der Plas
 */
public class TestDatabaseManagerIT {
    private String resourceString = Parameters.getResourceString();
    
    public TestDatabaseManagerIT() {
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

//    /**
//     * Test of ClearDatabase method, of class DatabaseManager.
//     */
//    @Test
//    public void testClearDatabase() {
//        System.out.println("ClearDatabase");
//        DatabaseManager instance = new DatabaseManager(resourceString);
//        instance.ClearDatabase();
//        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testProfiler(){
//     for(int i=0;i<1000;i++){
//     testIntializeDatabase();
//     }
//
//    }
    /**
     * Test of IntializeTestDatabase method, of class DatabaseManager.
     */
    @Test
    public void testIntializeDatabase() {
        System.out.println("IntializeDatabase");
        DatabaseManager instance = new DatabaseManager(resourceString);
        instance.IntializeTestDatabase();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
