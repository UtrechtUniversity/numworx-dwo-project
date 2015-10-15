/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestNewUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PublicUserManagerIT {
    private static final Logger LOG = Logger.getLogger(PublicUserManagerIT.class.getName());

    static DatabaseManager instance = null;
    
    public PublicUserManagerIT() {
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
     * Test of submitNewUser method, of class PublicUserManager.
     */
    @Test
    public void testSubmitNewUser() {
        System.out.println("submitNewUser");
        RestNewUser newUserReg = new RestNewUser();
        newUserReg.setUsername("testuser01");
        newUserReg.setGivenName("a");
        newUserReg.setInsertion("b");
        newUserReg.setFamilyName("c");
        newUserReg.setEmail("a@b.c");
        newUserReg.setPassword("pwd");
        newUserReg.setRole(RoleType.TEACHER);
        newUserReg.setSchoolLogin("school01");
        newUserReg.setSchoolCode("teacher");
        
        PublicUserManager instance = new PublicUserManager();
        Boolean result = instance.submitNewUser(newUserReg);
        assertEquals("function gave false as result.", result, true);
        PersistentUser user = UserManager.findByUserName(newUserReg.getUsername());
        assertEquals(newUserReg.getGivenName(), user.getFirstname());
        assertEquals(newUserReg.getInsertion(), user.getMiddlename());
        assertEquals(newUserReg.getFamilyName(), user.getLastname());
        assertEquals(newUserReg.getEmail(), user.getEmail());
        assertEquals(newUserReg.getPassword(), user.getPasswd());
        
        
        try {
            //check for hasRole
            PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(user, SchoolManager.findBySchoolLogin(newUserReg.getSchoolLogin()), RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find created user's hasRole");
        }
    }
    
//TOOD add SamlUser
//    /**
//     * Test of getSamlUser method, of class PublicUserManager.
//     */
//    @Test
//    public void testGetSamlUser() {
//        System.out.println("getSamlUser");
//        String samlUserId = "";
//        String samlOrgId = "";
//        String authToken = "";
//        PublicUserManager instance = new PublicUserManager();
//        PersistentUser expResult = null;
//        PersistentUser result = instance.getSamlUser(samlUserId, samlOrgId, authToken);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
}
