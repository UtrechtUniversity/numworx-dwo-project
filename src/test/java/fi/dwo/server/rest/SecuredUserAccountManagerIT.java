/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.server.testutil.TestSecurityContext;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestFullUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SecuredUserAccountManagerIT {
    private static final Logger LOG = Logger.getLogger(SecuredUserAccountManagerIT.class.getName());

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
     * retrieve the current user if it exists and not if the user does not exists.
     */
    @Test
    public void testGetCurrentUser() {
        System.out.println("getCurrentUser");
        SecuredUserAccountManager instance = new SecuredUserAccountManager();

        SecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);
        PersistentUser expResult = UserManager.findByUserName("user01");
        DomFullUser result = instance.getCurrentUser(sc);
        assertEquals(expResult.getUserID().longValue(), MySQLPersistenceId.getId(result.getId()));

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
     * Test of updateCurrentUser method, of class SecuredUserAccountManager. Should
     * only update given name, insertion and family name, email and password.
     */
    @Test
    public void testUpdateCurrentUser() {
        System.out.println("updateCurrentUser");
        SecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);
        SecuredUserAccountManager instance = new SecuredUserAccountManager();
        RestFullUser user = new RestFullUser();
        user.setDomFullUser(new DomFullUser(UserManager.findByUserName("user01")));
        user.getDomFullUser().setGivenName("a");
        user.getDomFullUser().setInsertion("b");
        user.getDomFullUser().setFamilyName("c");
        user.getDomFullUser().setEmail("d");
        user.getDomFullUser().setPassword("e");

        DomFullUser result = instance.updateCurrentUser(sc, user);
        assertEquals(user, result);

        user.setDomFullUser(new DomFullUser(UserManager.findByUserName("user01")));
        user.getDomFullUser().setUsername("bonk");
        try {
            result = instance.updateCurrentUser(sc, user);
            fail("Did not fail fake username with result." + result.getUsername());
        }
        catch (Dwo2RestException e) {
            // succeeded
        }

        user.setDomFullUser(new DomFullUser(UserManager.findByUserName("user01")));

    }

    /**
     * Test of removeCurrentUser method, of class SecuredUserAccountManager. Tests
     * if user in tblUser was removed.
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
