/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.server.testutil.TestSecurityContext;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestNewSchoolLogin;
import fi.dwo.commons.rest.entities.RestSchoolRoleAndClass;
import fi.dwo.commons.rest.entities.RestSchoolsRolesAndClasses;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
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
    
    static DatabaseManager instance = null;
    
    public SecuredUserAccountLoginsManagerIT() {
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
     * Test of getSchoolLogins method, of class SecuredUserAccountLoginsManager.
     */
    @Test
    public void testGetSchoolLogins() {
        System.out.println("getSchoolLogins");
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();

        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        RestSchoolsRolesAndClasses result = instance.getSchoolLogins(sc);
        if(result.getSchoolsRolesAndClassesList().size()!=5){
        fail("The number of schoollogins is wrong.");
        }
        //test default user
        if((int) MySQLPersistenceId.getId(result.getActiveSchoolRoleAndClass().getSchoolGroupId())!=5
                ||  (int) MySQLPersistenceId.getId(result.getActiveSchoolRoleAndClass().getUserId())!=10
                || result.getActiveSchoolRoleAndClass().getSchoolClassId()!= null
                ){
        fail("The retrieved selected user, group and class is wrong for the selected login.");
        }
    }

    /**
     * Test of switchToSchoolLogin method, of class SecuredUserAccountLoginsManager.
     */
    @Test
    public void testSwitchToSchoolLogin() {
        System.out.println("switchToSchoolLogin");
        SecurityContext sc = new TestSecurityContext("user03", RoleType.STUDENT);
        PersistentUser user = UserManager.findByUserName("user03");
        int oldSchoolGroup = user.getSchoolGroupID();

        RestSchoolRoleAndClass sarc = new RestSchoolRoleAndClass();
        sarc.setUserId(MySQLPersistenceId.createPersistenceId(user.getUserID(),PersistenceClassType.PersistentUser));
        sarc.setSchoolId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchool));
        sarc.setRoleId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentRole));
        sarc.setSchoolClassId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        sarc.setSchoolGroupId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolGroup));
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();
        RestSchoolRoleAndClass result = instance.switchToSchoolLogin(sc, sarc);

        int sgId = (int) (new MySQLPersistenceId( result.getSchoolGroupId()).getId());
        int scId = (int) (new MySQLPersistenceId( result.getSchoolClassId()).getId());
        if(new MySQLPersistenceId(result.getSchoolGroupId()).getId() == oldSchoolGroup 
                || sgId!=2 || scId!=2){            
            fail("SchoolClass or SchoolGroup did not change.");
        }
    }

    /**
     * Test of submitASchoolLogin method, of class SecuredUserAccountLoginsManager.
     */
    @Test
    public void testSubmitASchoolLogin() {
        System.out.println("submitASchoolLogin");
        SecurityContext sc = null;
        RestNewSchoolLogin existingUserReg = null;
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();
        Response expResult = null;
        Response result = instance.submitASchoolLogin(sc, existingUserReg);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeASchoolLogin method, of class SecuredUserAccountLoginsManager.
     */
    @Test
    public void testRemoveASchoolLogin() {
        System.out.println("removeASchoolLogin");
        SecurityContext sc = null;
        RestSchoolRoleAndClass sarc = null;
        SecuredUserAccountLoginsManager instance = new SecuredUserAccountLoginsManager();
        Boolean expResult = null;
        Boolean result = instance.removeASchoolLogin(sc, sarc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
