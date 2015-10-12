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
//     * Test of ClearDatabase method, of class TestDatabaseManager.
//     */
//    @Test
//    public void testClearDatabase() {
//        System.out.println("ClearDatabase");
//        TestDatabaseManager instance = new TestDatabaseManager(resourceString);
//        instance.ClearDatabase();
//        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }

    /**
     * Test of IntializeDatabase method, of class TestDatabaseManager.
     */
    @Test
    public void testIntializeDatabase() {
        System.out.println("IntializeDatabase");
        TestDatabaseManager instance = new TestDatabaseManager(resourceString);
        instance.IntializeDatabase();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
