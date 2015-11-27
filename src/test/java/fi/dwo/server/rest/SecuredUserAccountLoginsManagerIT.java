/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomNewSchoolLogin;
import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.server.testutil.TestSecurityContext;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestNewSchoolLogin;
import fi.dwo.commons.rest.entities.RestSchoolRoleAndClass;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredUserAccountLoginsManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountLoginsManagerIT.class.getName());

    static DatabaseManager dbinstance = null;

    public SecuredUserAccountLoginsManagerIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbinstance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        dbinstance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
    }

    @Before
    public void setUp() {
        dbinstance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        dbinstance.ClearDatabase();
    }

    /**
     * Test of getSchoolLogins method, of class SecuredUserAccountLoginsManager.
     */
    @Test
    public void testGetSchoolLogins() {
        System.out.println("getSchoolLogins");
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();

        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        DomSchoolsRolesAndClasses result = instance.getSchoolLogins(sc);
        if (result.getSchoolsRolesAndClassesList().size() != 5) {
            fail("The number of schoollogins is wrong.");
        }
        //test default user
        if (MySQLPersistenceId.getId(result.getActiveSchoolRoleAndClass().getSchoolGroupId()) != 5L
                || MySQLPersistenceId.getId(result.getActiveSchoolRoleAndClass().getUserId()) != 10L) {
            fail("The retrieved selected user, group and class is wrong for the selected login.");
        }
    }

    /**
     * Test of switchToSchoolLogin method, of class
     * SecuredUserAccountLoginsManager.
     */
    @Test
    public void testSwitchToSchoolLogin() {
        System.out.println("switchToSchoolLogin");
        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        PersistentUser user = UserManager.findByUserName("user03");
        Long oldSchoolGroup = user.getSchoolGroupID();

        RestSchoolRoleAndClass sarc = new RestSchoolRoleAndClass();
        DomSchoolRoleAndClass darc = new DomSchoolRoleAndClass();
        sarc.setDomSchoolRoleAndClass(darc);
        darc.setUserId(MySQLPersistenceId.createPersistenceId(user.getUserID(), PersistenceClassType.PersistentUser));
        darc.setSchoolId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchool));
        darc.setRoleId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentRole));
        darc.setSchoolClassId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchoolClass));
        darc.setSchoolGroupId(MySQLPersistenceId.createPersistenceId(5, PersistenceClassType.PersistentSchoolGroup));
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();
        DomSchoolRoleAndClass result = instance.switchToSchoolLogin(sc, sarc);

        long sgId = (long) MySQLPersistenceId.getId(result.getSchoolGroupId());
        long scId = (long) MySQLPersistenceId.getId(result.getSchoolClassId());

        if ((MySQLPersistenceId.getId(result.getSchoolGroupId())) != oldSchoolGroup
                || sgId != 5L || scId != 3) {
            fail("SchoolClass or SchoolGroup did not change.");
        }
    }

    /**
     * Test of submitASchoolLogin method, of class
     * SecuredUserAccountLoginsManager.
     */
    @Test
    public void testSubmitASchoolLogin() {
        System.out.println("submitASchoolLogin");
        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        PersistentUser user = UserManager.findByUserName("user03");
        RestNewSchoolLogin existingUserReg = new RestNewSchoolLogin();
        //should fail
        DomNewSchoolLogin domUserReg  = new DomNewSchoolLogin();
        existingUserReg.setDomNewSchoolLogin(domUserReg);
        domUserReg.setRole(RoleType.STUDENT);
        domUserReg.setSchoolLogin("school01");
        domUserReg.setSchoolCode("schooladmin");
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();
        try {
            Boolean result = instance.submitASchoolLogin(sc, existingUserReg);
            assertEquals(false, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
        PersistentHasRole hr = HasRoleManager.findEntity(new PersistentHasRolePK(10L, 4L));
        assertEquals(hr, null);

        //should succeed
        domUserReg.setRole(RoleType.SCHOOLADMIN);
        domUserReg.setSchoolLogin("school01");
        domUserReg.setSchoolCode("schooladmin");
        Boolean result = instance.submitASchoolLogin(sc, existingUserReg);
        assertEquals(true, result);
        hr = HasRoleManager.findEntity(new PersistentHasRolePK(10L, 4L));
        assertNotEquals(hr, null);
    }

    /**
     * Test of removeASchoolLogin method, of class
     * SecuredUserAccountLoginsManager.
     */
    @Test
    public void testRemoveASchoolLogin() {
        System.out.println("removeASchoolLogin");
        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        PersistentUser user = UserManager.findByUserName("user03");
        RestSchoolRoleAndClass sarc = new RestSchoolRoleAndClass();
        DomSchoolRoleAndClass darc = new DomSchoolRoleAndClass();
        sarc.setDomSchoolRoleAndClass(darc);
        darc.setUserId(MySQLPersistenceId.createPersistenceId(user.getUserID(), PersistenceClassType.PersistentUser));
        darc.setSchoolId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchool));
        darc.setRoleId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentRole));
        darc.setSchoolClassId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        darc.setSchoolGroupId(MySQLPersistenceId.createPersistenceId(5, PersistenceClassType.PersistentSchoolGroup));
        Boolean expResult = true;
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();
        Boolean result = instance.removeASchoolLogin(sc, sarc);
        assertEquals(expResult, result);
        PersistentHasRole hr = HasRoleManager.findEntity(new PersistentHasRolePK(10L, 5L));
        assertEquals("HasRole was not removed.", hr, null);
    }
}
