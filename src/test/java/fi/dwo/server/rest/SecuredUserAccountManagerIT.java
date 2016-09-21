/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.server.testutil.TestSecurityContext;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.rest.entities.RestUserFull;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
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
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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
        DomUserFull result = instance.getCurrentUser(sc);
        assertEquals(expResult.getId().longValue(), MySQLPersistenceId.getId(result.getId()));

        // fail if non-existing user
        sc = new TestSecurityContext("userFake", RoleType.STUDENT);
        try {
            result = instance.getCurrentUser(sc);
            fail("Did not fail fake username with result." + result.getUserName());
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
        RestUserFull user = new RestUserFull();
        user.setDomUserFull(UserManager.findByUserName("user01").buildDomUserFull());
        user.getDomUserFull().setGivenName("a");
        user.getDomUserFull().setInsertion("b");
        user.getDomUserFull().setFamilyName("c");
        user.getDomUserFull().setEmail("x@xy.zz");
        user.getDomUserFull().setPassword("e");

        DomUserFull result = instance.updateCurrentUser(sc, user);
        assertEquals(user.getDomUserFull().getUserName(), result.getUserName());
        assertEquals(user.getDomUserFull().getGivenName(), result.getGivenName());
        assertEquals(user.getDomUserFull().getInsertion(), result.getInsertion());
        assertEquals(user.getDomUserFull().getFamilyName(), result.getFamilyName());
        assertEquals(user.getDomUserFull().getPassword(), result.getPassword());
        assertEquals(user.getDomUserFull().getEmail(), result.getEmail());

        user.setDomUserFull(UserManager.findByUserName("user01").buildDomUserFull());
        user.getDomUserFull().setUserName("bonk");
        try {
            result = instance.updateCurrentUser(sc, user);
            fail("Did not fail fake username with result." + result.getUserName());
        }
        catch (Dwo2RestException e) {
            // succeeded
        }

        user.setDomUserFull(UserManager.findByUserName("user01").buildDomUserFull());

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
