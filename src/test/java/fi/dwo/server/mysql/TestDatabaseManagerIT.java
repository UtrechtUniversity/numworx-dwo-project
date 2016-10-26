/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.mysql;

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

    /**
     * Test of ClearDatabase method, of class DatabaseManager. Running a focused
     * junit test on this method can clear the junittestdatabase.
     */
    @Test
    public void testClearDatabase() {
        System.out.println("ClearDatabase");
        DatabaseManager instance = new DatabaseManager();
        instance.ClearDatabase();
    }

//    @Test
//    public void testProfiler(){
//     for(int i=0;i<1000;i++){
//     testIntializeDatabase();
//     }
//    }
    /**
     * Test of IntializeTestDatabase method, of class DatabaseManager. Running a
     * focussed junit test on this will fill the junittestdatabase for manipulation.
     */
    @Test
    public void testIntializeDatabase() {
        System.out.println("IntializeDatabase");
        DatabaseManager instance = new DatabaseManager();
        instance.ClearDatabase();
        instance.IntializeTestDatabase();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
