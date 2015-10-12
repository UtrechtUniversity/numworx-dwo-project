/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.mysql.TestDatabaseManager;
import fi.dwo.server.testutil.Parameters;
import javax.ws.rs.core.SecurityContext;
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
public class SecuredUserAccountManagerIT {

    static TestDatabaseManager instance = null;

    public SecuredUserAccountManagerIT() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new TestDatabaseManager(Parameters.getResourceString());
        instance.IntializeDatabase();
    }

    @After
    public void tearDown() {
        instance = new TestDatabaseManager(Parameters.getResourceString());
        instance.ClearDatabase();
    }

    /**
     * Test of getCurrentUser method, of class SecuredUserAccountManager.
     */
    @Test
    public void testGetCurrentUser() {
        System.out.println("getCurrentUser");
        SecurityContext sc = null;
        SecuredUserAccountManager instance = new SecuredUserAccountManager();
        PersistentUser expResult = null;
        PersistentUser result = instance.getCurrentUser(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateCurrentUser method, of class SecuredUserAccountManager.
     */
    @Test
    public void testUpdateCurrentUser() {
        System.out.println("updateCurrentUser");
        SecurityContext sc = null;
        PersistentUser user = null;
        SecuredUserAccountManager instance = new SecuredUserAccountManager();
        PersistentUser expResult = null;
        PersistentUser result = instance.updateCurrentUser(sc, user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeCurrentUser method, of class SecuredUserAccountManager.
     */
    @Test
    public void testRemoveCurrentUser() {
        System.out.println("removeCurrentUser");
        SecurityContext sc = null;
        SecuredUserAccountManager instance = new SecuredUserAccountManager();
        Boolean expResult = null;
        Boolean result = instance.removeCurrentUser(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
