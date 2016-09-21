/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.testutil.TestSecurityContext;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import fi.dwo.rest.entities.RestNewSchoolLogin;
import fi.dwo.rest.entities.RestSchoolRoleAndClassV2;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.RoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
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
public class SecuredUserAccountLoginsManagerV2IT {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountLoginsManagerV2IT.class.getName());

    static DatabaseManager dbinstance = null;

    public SecuredUserAccountLoginsManagerV2IT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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
        SecuredUserAccountLoginsManagerV2 instance = new SecuredUserAccountLoginsManagerV2();

        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        DomSchoolsRolesAndClassesV2 result = instance.getSchoolLogins(sc);
        if (result.getSchoolsRolesAndClassesList().size() != 5) {
            fail("The number of schoollogins is wrong.");
        }
        //test default user
        if (MySQLPersistenceId.getId(result.getActiveSchoolRoleAndClass().getHasRole().getSchoolGroupId())!=5  
                || MySQLPersistenceId.getId(result.getActiveSchoolRoleAndClass().getHasRole().getUserId()) != 10L) {
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
        Long oldSchoolGroup = user.getSchoolGroupId();

        RestSchoolRoleAndClassV2 sarc = new RestSchoolRoleAndClassV2();
        DomSchoolRoleAndClassV2 darc = new DomSchoolRoleAndClassV2();
        sarc.setDomSchoolRoleAndClass(darc);
        darc.setRole(RoleManager.findEntity(5L).createDomRole());
        darc.setSchool(SchoolManager.findEntity(3L).createDomSchool());
        darc.setSchoolClass(SchoolClassManager.findEntity(3L).createDomSchoolClass());
        darc.setHasRole(HasRoleManager.findEntity(new PersistentHasRolePK(user.getId(),7L)).buildDomHasRole());
//        darc.setUserId(MySQLPersistenceId.createPersistenceId(user.getId(), PersistenceClassType.PersistentUser));
//        darc.setSchoolId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchool));
//        darc.setRoleId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentRole));
//        darc.setSchoolClassId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchoolClass));
//        darc.setSchoolGroupId(MySQLPersistenceId.createPersistenceId(5, PersistenceClassType.PersistentSchoolGroup));
        SecuredUserAccountLoginsManagerV2 instance = new SecuredUserAccountLoginsManagerV2();
        DomSchoolRoleAndClassV2 result = instance.switchToSchoolLogin(sc, sarc);

        long sgId = (long) MySQLPersistenceId.getId(result.getHasRole().getSchoolGroupId());
        long scId = (long) MySQLPersistenceId.getId(result.getSchoolClass().getId());

        if (sgId == oldSchoolGroup
                || sgId != 7L ||scId!=3) {
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
        SecuredUserAccountLoginsManagerV2 instance = new SecuredUserAccountLoginsManagerV2();
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
        RestSchoolRoleAndClassV2 sarc = new RestSchoolRoleAndClassV2();
        DomSchoolRoleAndClassV2 darc = new DomSchoolRoleAndClassV2();
        sarc.setDomSchoolRoleAndClass(darc);
        darc.setRole(RoleManager.findEntity(1L).createDomRole());
        darc.setSchool(SchoolManager.findEntity(3L).createDomSchool());
        darc.setSchoolClass(SchoolClassManager.findEntity(2L).createDomSchoolClass());
        darc.setHasRole(HasRoleManager.findEntity(new PersistentHasRolePK(user.getId(),5L)).buildDomHasRole());

//        darc.setUserId(MySQLPersistenceId.createPersistenceId(user.getId(), PersistenceClassType.PersistentUser));
//        darc.setSchoolId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchool));
//        darc.setRoleId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentRole));
//        darc.setSchoolClassId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
//        darc.setSchoolGroupId(MySQLPersistenceId.createPersistenceId(5, PersistenceClassType.PersistentSchoolGroup));
        Boolean expResult = true;
        SecuredUserAccountLoginsManagerV2 instance = new SecuredUserAccountLoginsManagerV2();
        Boolean result = instance.removeASchoolLogin(sc, sarc);
        assertEquals(expResult, result);
        PersistentHasRole hr = HasRoleManager.findEntity(new PersistentHasRolePK(user.getId(), 5L));
        assertEquals("HasRole was not removed.", hr, null);
    }
}
