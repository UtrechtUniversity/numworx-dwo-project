/**
 * Copyrighted Sep 18, 2015
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;
import java.util.List;
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
public class UserManagerPIT {
    PersistentUser userA = new PersistentUser();
    PersistentUser userB = new PersistentUser();

    public UserManagerPIT() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        userA.setGivenName("Hamlet");
        userA.setInsertion("of");
        userA.setLastname("Denmark");
        Date d = DwoDateUtilities.getCurrentDwoDate();
        userA.setRegisterDate(d);
        userA.setLastLogin(null);
        userA.setPassword("bladiebla");
        userA.setUsername("JunitTestUserA");
        userA.setEmail("hamlet@denmark.dk");

        userB.setGivenName("Yorick");
        userB.setInsertion("of");
        userB.setLastname("Denmark");
        d = DwoDateUtilities.getCurrentDwoDate();
        userB.setRegisterDate(d);
        userB.setLastLogin(null);
        userB.setPassword("bladiebla");
        userB.setUsername("JunitTestUserB");
        userB.setEmail("yorick@denmark.dk");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class UserManager.
     */
    @Test
    public void testCreate() {

        //create
        try {
            UserManager.create(userA);
            UserManager.create(userB);
            PersistentUser userOne = UserManager.findByUserName(userA.getUsername());
            PersistentUser userTwo = UserManager.findByUserName(userB.getUsername());
            if ((!userA.similar(userOne)) || (!userB.similar(userTwo))) {
                fail("User created is different.");
            }
        }
        catch (Exception e) {
            fail("Exception during create.");
        }
        //recreate
        try {
            UserManager.create(userA);
            UserManager.create(userB);
            fail("Creating double copy should not work.");
        }
        catch (Exception e) {
            //works!
        }

        //cleanup
        try {
            UserManager.destroy(UserManager.findByUserName(userA.getUsername()).getId());
            UserManager.destroy(UserManager.findByUserName(userB.getUsername()).getId());
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of edit method, of class UserManager.
     */
    @Test
    public void testEdit() throws Exception {
        UserManager.create(userA);
        UserManager.create(userB);
        // edit
        try {
            System.out.println("update user");
            PersistentUser user = UserManager.findByUserName(userA.getUsername());
            user.setGivenName(userB.getGivenName());
            UserManager.edit(user);
            user = UserManager.findByUserName(userA.getUsername());
            if (user.getGivenName().compareTo(userB.getGivenName()) != 0) {
                fail("UserManager.edit() failed.");
            }
            user.setGivenName(userA.getGivenName());
            if (!user.similar(userA)) {
                fail("UserManager.edit() failed.");
            }
        }
        catch (Exception e) {
            fail("UserManager.edit() failed.");
        }

        //update should fail
        try {
            System.out.println("update school");
            PersistentUser user = UserManager.findByUserName(userA.getUsername());
            user.setUsername(userB.getUsername());
            UserManager.edit(user);
            user = UserManager.findByUserName(userA.getUsername());
            if (user == null) {
                fail("UserManager.edit() failed. School disappeared.");
            }
        }
        catch (Exception e) {
            // works!
        }

        //cleanup
        try {
            UserManager.destroy(UserManager.findByUserName(userA.getUsername()).getId());
            UserManager.destroy(UserManager.findByUserName(userB.getUsername()).getId());
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of destroy method, of class UserManager.
     */
    @Test
    public void testDestroy() {
        UserManager.create(userA);
        System.out.println("destroy");
        Integer id = null;
        try {
            PersistentUser user = UserManager.findByUserName(userA.getUsername());
            UserManager.destroy(user.getId());
            try {
                user = UserManager.findByUserName(user.getUsername());
                if (user != null) {
                    fail("User not destroyed.");
                }
            }
            catch (Exception e) {
                //works
            }
        }
        catch (Exception e) {
            fail("Exception during destroy.");
        }
    }

    /**
     * Test of findEntities method, of class UserManager.
     */
//    @Test
    public void testFindEntities_0args() {
        System.out.println("findEntities");
        List<PersistentUser> expResult = null;
        List<PersistentUser> result = UserManager.findEntities();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntities method, of class UserManager.
     */
//    @Test
    public void testFindEntities_int_int() {
        System.out.println("findEntities");
        int maxResults = 0;
        int firstResult = 0;
        List<PersistentUser> expResult = null;
        List<PersistentUser> result = UserManager.findEntities(maxResults, firstResult);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntity method, of class UserManager.
     */
//    @Test
    public void testFindEntity() {
        System.out.println("findEntity");
        Long id = null;
        PersistentUser expResult = null;
        PersistentUser result = UserManager.findEntity(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEntityCount method, of class UserManager.
     */
//    @Test
    public void testGetEntityCount() {
        System.out.println("getEntityCount");
        int expResult = 0;
        int result = UserManager.getEntityCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByUserName method, of class UserManager.
     */
    @Test
    public void testFindByUserName() {
        try {
            UserManager.create(userA);
            PersistentUser result = UserManager.findByUserName(userA.getUsername());
            if (!result.similar(userA)) {
                fail("Found different user as created.");
            }
            UserManager.destroy(result.getId());
        }
        catch (Exception e) {
            fail("Exception during find.");
        }
    }

    /**
     * Test of login method, of class UserManager.
     */
//    @Test
    public void testLogin() {
//        try {
//            UserManager.create(userA);
//            PersistentUser result = UserManager.findByUserName(userA.getUsername());
//            if (!result.similar(userA)) {
//                fail("Found different user as created.");
//            }
//            UserManager.destroy(result.getId());
//        }
//        catch (Exception e) {
//            fail("Exception during find.");
//        }
    }
}
