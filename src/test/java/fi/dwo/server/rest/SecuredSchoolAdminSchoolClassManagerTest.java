/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
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
public class SecuredSchoolAdminSchoolClassManagerTest {
    
    public SecuredSchoolAdminSchoolClassManagerTest() {
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
     * Test of getSchoolClasses method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<RestSchoolClass> expResult = null;
        List<RestSchoolClass> result = instance.getSchoolClasses(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTeachersInSchool method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of GetTeachersInSchoolClass method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testGetTeachersInSchoolClass() {
        System.out.println("GetTeachersInSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.GetTeachersInSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitTeacherToSchoolClass method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testSubmitTeacherToSchoolClass() {
        System.out.println("SubmitTeacherToSchoolClass");
        SecurityContext sc = null;
        RestTeacher restTeacher = null;
        RestSchoolClass restSchoolClass = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitTeacherToSchoolClass(sc, restTeacher, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeTeacherFromSchoolClass method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testRemoveTeacherFromSchoolClass() {
        System.out.println("removeTeacherFromSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        RestTeacher restTeacher = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeTeacherFromSchoolClass(sc, restSchoolClass, restTeacher);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitSingleSchoolStudent method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = null;
        RestSingleSchoolStudent nssStudent = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitSingleSchoolStudent(sc, nssStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
