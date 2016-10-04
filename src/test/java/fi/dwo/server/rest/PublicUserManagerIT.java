/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomLoginCheck;
import fi.dwo.rest.dom.entities.DomNewUser;
import fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.rest.entities.RestAuthToken;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestNewUser;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
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
public class PublicUserManagerIT {
    private static final Logger LOG = Logger.getLogger(PublicUserManagerIT.class.getName());

    static DatabaseManager instance = null;
    
    public PublicUserManagerIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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
        domNewUser.setEmail("a@b.cd");
        domNewUser.setPassword("pwd");
        domNewUser.setRole(RoleType.TEACHER);
        domNewUser.setSchoolLogin("school01");
        domNewUser.setSchoolCode("teacher");
        
        PublicUserManager instance = new PublicUserManager();

        try{
            Boolean result = instance.submitNewUser(restNewUser);
            assertEquals("function gave false as result.", result, true);
        }catch(Dwo2RestException e){
            assertEquals(e.getDwo2Code(),Dwo2ExceptionCode.Rest_Registration_School_authentication_failed);
        }
        
        PersistentUser user = UserManager.findByUserName(domNewUser.getUsername());
        assertEquals(domNewUser.getGivenName(), user.getGivenName());
        assertEquals(domNewUser.getInsertion(), user.getInsertion());
        assertEquals(domNewUser.getFamilyName(), user.getLastname());
        assertEquals(domNewUser.getEmail(), user.getEmail());
        assertEquals(domNewUser.getPassword(), user.getPassword());
        
        
        try {
            //check for hasRole
            PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(user, SchoolManager.findBySchoolLogin(domNewUser.getSchoolLogin()), RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, "", ex);
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
    

    /**
     * Test of getSamlUser method, of class PublicUserManager.
     */
    @Test
    public void testGetLoginCheck() {
        System.out.println("getLoginCheck");
        PublicUserManager instance = new PublicUserManager();

        SecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);
        PersistentUser user = UserManager.findByUserName("user01");
        RestLoginCheck restLoginCheck = new RestLoginCheck();
        restLoginCheck.setRestContext(new DomContext());
        restLoginCheck.setDomLoginCheck(new DomLoginCheck());
        restLoginCheck.getDomLoginCheck().setUsername("user01");
        restLoginCheck.getDomLoginCheck().setPassword(DomLoginCheck.crypt("user01"));
        Boolean result = instance.getLoginCheck(restLoginCheck);
        assertEquals(true, result);
        restLoginCheck.getDomLoginCheck().setPassword("bla");
        result = instance.getLoginCheck(restLoginCheck);
        assertEquals(false, result);
    }        
    
    /**
     * Test getAuthToken format '1'
     */
    @Test
    public void testGetAuthTokenUser() throws Exception {
// Basic test
    		PublicUserManager instance = new PublicUserManager();
    		PersistentUser user = UserManager.findByUserName("user01");
// Build correct token
    		String authToken = 
    			"1\f" + System.currentTimeMillis() + "\f" + user.getUsername() + "\f" + user.getPassword();
    		authToken = Base64.getUrlEncoder().encodeToString(authToken.getBytes(StandardCharsets.UTF_8));
    		RestAuthToken rest = new RestAuthToken();
    		rest.setAuthToken(authToken);
    		
    		DomUserFullwLoginContext result = instance.getUserFromAuthToken(rest);
    		
    		assertEquals( "user01", result.getDomUserFull().getUserName());
// Too Old test
    		authToken = 
			"1\f" + 0L + "\f" + user.getUsername() + "\f" + user.getPassword();
		authToken = Base64.getUrlEncoder().encodeToString(authToken.getBytes(StandardCharsets.UTF_8));
		rest = new RestAuthToken();
		rest.setAuthToken(authToken);
		
		try {
			result = instance.getUserFromAuthToken(rest);
			fail("Too old " + result);
		} catch (Dwo2RestException e) {
			System.out.println(e.getMessage());
		}
// TODO wrong format/version/user not found tests...
		
    }
    
}
