/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.SecurityContext;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SecuredUserAccountManagerIT {

    static DatabaseManager instance = null;

    public SecuredUserAccountManagerIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        instance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
    }

    @Before
    public void setUp() {
        instance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

    /**
     * Test of getCurrentUser method, of class SecuredUserAccountManager. Should
     * retrieve the current user if it exists.
     */
    @Test
    public void testGetCurrentUser() {
        System.out.println("getCurrentUser");
        SecuredUserAccountManager instance = new SecuredUserAccountManager();

        SecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);
        PersistentUser expResult = UserManager.findByUserName("user01");
        PersistentUser result = instance.getCurrentUser(sc);
        assertEquals(expResult, result);

        // fail if non-existing user
        sc = new TestSecurityContext("userFake", RoleType.STUDENT);
        try {
            result = instance.getCurrentUser(sc);
            fail("Did not fail fake username with result." + result.getUsername());
        }
        catch (Dwo2RestException e) {
            // succeeded
        }
    }

    /**
     * Test of updateCurrentUser method, of class SecuredUserAccountManager.
     */
    @Test
    public void testUpdateCurrentUser() {
        System.out.println("updateCurrentUser");
        SecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);
        SecuredUserAccountManager instance = new SecuredUserAccountManager();
        PersistentUser user = UserManager.findByUserName("user01");
        user.setFirstname("a");
        user.setMiddlename("b");
        user.setLastname("c");
        user.setEmail("d");
        user.setPasswd("e");

        PersistentUser result = instance.updateCurrentUser(sc, user);
        assertEquals(user, result);

        user = UserManager.findByUserName("user01");
        user.setUsername("bonk");
        try {
            result = instance.updateCurrentUser(sc, user);
            fail("Did not fail fake username with result." + result.getUsername());
        }
        catch (Dwo2RestException e) {
            // succeeded
        }

        user = UserManager.findByUserName("user01");
        user.setSchoolGroupID(-1);
        try {
            result = instance.updateCurrentUser(sc, user);
            if (result.getSchoolGroupID() == -1) {
                fail("Did not fail schoolgroup change.");
            }
        }
        catch (Dwo2RestException e) {
            // succeeded
        }

        user = UserManager.findByUserName("user01");
        user.setLastLogin(new Date());
        try {
            result = instance.updateCurrentUser(sc, user);
            if (user.getLastLogin()!=null && result.getLastLogin()!=null && (new SimpleDateFormat("MM-dd-yyyy").format(user.getLastLogin())).equals(new SimpleDateFormat("MM-dd-yyyy").format(result.getLastLogin()))) {
                fail("Did not fail last login change.");
            }
        }
        catch (Dwo2RestException e) {
            // succeeded
        }

    }

    /**
     * Test of removeCurrentUser method, of class SecuredUserAccountManager.
     */
    @Test
    public void testRemoveCurrentUser() {
        System.out.println("removeCurrentUser");
        SecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);
        SecuredUserAccountManager instance = new SecuredUserAccountManager();
        Boolean result = instance.removeCurrentUser(sc);
        assertEquals(result, new Boolean(true));

        try {
            PersistentUser user = UserManager.findByUserName("user01");
            if(user!=null) fail("User was not removed by function.");
        }
        catch (Dwo2RestException e) {
            // succeeded
        }

    }
}
