/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomNewUser;
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
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
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
     * 
     * Tests if user is added and the appropriate role is created. it does not
     * test for proper null-school and null-class enlistment.
     */
    //TODO test for proper null-school and null-class enlistment.
    @Test
    public void testSubmitNewUser() {
        System.out.println("submitNewUser");
        RestNewUser restNewUser = new RestNewUser();
        DomNewUser domNewUser= new DomNewUser();
        restNewUser.setDomNewUser(domNewUser);
        domNewUser.setUsername("testuser01");
        domNewUser.setGivenName("a");
        domNewUser.setInsertion("b");
        domNewUser.setFamilyName("c");
        domNewUser.setEmail("a@b.c");
        domNewUser.setPassword("pwd");
        domNewUser.setRole(RoleType.TEACHER);
        domNewUser.setSchoolLogin("school01");
        domNewUser.setSchoolCode("teacher");
        
        PublicUserManager instance = new PublicUserManager();
        Boolean result = instance.submitNewUser(restNewUser);
        assertEquals("function gave false as result.", result, true);
        PersistentUser user = UserManager.findByUserName(domNewUser.getUsername());
        assertEquals(domNewUser.getGivenName(), user.getFirstname());
        assertEquals(domNewUser.getInsertion(), user.getMiddlename());
        assertEquals(domNewUser.getFamilyName(), user.getLastname());
        assertEquals(domNewUser.getEmail(), user.getEmail());
        assertEquals(domNewUser.getPassword(), user.getPasswd());
        
        
        try {
            //check for hasRole
            PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(user, SchoolManager.findBySchoolLogin(domNewUser.getSchoolLogin()), RoleType.TEACHER);
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
