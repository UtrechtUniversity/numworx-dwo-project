/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.rest.entities.RestSchoolClass;
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
public class SecuredStudentSchoolClassManagerIT {
    
    public SecuredStudentSchoolClassManagerIT() {
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
     * Test of setActiveSchoolClass method, of class SecuredStudentSchoolClassManager.
     */
    @Test
    public void testSetActiveSchoolClass() {
        System.out.println("setActiveSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.setActiveSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeStudentFromSchoolClass method, of class SecuredStudentSchoolClassManager.
     */
    @Test
    public void testRemoveStudentFromSchoolClass() {
        System.out.println("removeStudentFromSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeStudentFromSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registerStudentForSchoolClass method, of class SecuredStudentSchoolClassManager.
     */
    @Test
    public void testRegisterStudentForSchoolClass() {
        System.out.println("registerStudentForSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.registerStudentForSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
