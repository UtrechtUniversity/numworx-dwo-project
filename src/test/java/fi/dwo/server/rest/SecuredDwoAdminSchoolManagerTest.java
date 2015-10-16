/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.rest.entities.RestSchool4Admin;
import java.util.List;
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
public class SecuredDwoAdminSchoolManagerTest {
    
    public SecuredDwoAdminSchoolManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of submitSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testSubmitSchool() {
        System.out.println("submitSchool");
        SecurityContext sc = null;
        PersistentSchool school = null;
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        PersistentSchool result = instance.submitSchool(sc, school);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testGetSchool() {
        System.out.println("getSchool");
        SecurityContext sc = null;
        PersistenceId pid = null;
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        PersistentSchool result = instance.getSchool(sc, pid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSchools method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testGetSchools() {
        System.out.println("getSchools");
        SecurityContext sc = null;
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        List<RestSchool4Admin> expResult = null;
        List<RestSchool4Admin> result = instance.getSchools(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testUpdateSchool() {
        System.out.println("updateSchool");
        SecurityContext sc = null;
        PersistentSchool school = null;
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        PersistentSchool result = instance.updateSchool(sc, school);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testRemoveSchool() {
        System.out.println("removeSchool");
        SecurityContext sc = null;
        RestSchool4Admin restSchool = null;
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        Boolean expResult = null;
        Boolean result = instance.removeSchool(sc, restSchool);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
