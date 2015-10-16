/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.rest.entities.RestSchool;
import fi.dwo.commons.rest.entities.RestSchoolAdmin;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestStudent;
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
public class SecuredSchoolAdminSchoolManagerIT {
    
    public SecuredSchoolAdminSchoolManagerIT() {
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
     * Test of getTeachersInSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = null;
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStudentsInSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testGetStudentsInSchool() {
        System.out.println("getStudentsInSchool");
        SecurityContext sc = null;
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<RestStudent> expResult = null;
        List<RestStudent> result = instance.getStudentsInSchool(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSchoolAdminInSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testGetSchoolAdminInSchool() {
        System.out.println("getSchoolAdminInSchool");
        SecurityContext sc = null;
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.getSchoolAdminInSchool(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeSingleSchoolStudentFromSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testRemoveSingleSchoolStudentFromSchool() {
        System.out.println("removeSingleSchoolStudentFromSchool");
        SecurityContext sc = null;
        RestSchool restSchool = null;
        RestStudent restStudent = null;
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        Boolean expResult = null;
        Boolean result = instance.removeSingleSchoolStudentFromSchool(sc, restSchool, restStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitSingleSchoolStudent method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = null;
        RestSingleSchoolStudent nssStudent = null;
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitSingleSchoolStudent(sc, nssStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSchoolClasses method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = null;
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<RestSchoolClass> expResult = null;
        List<RestSchoolClass> result = instance.getSchoolClasses(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeTeacherFromSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testRemoveTeacherFromSchool() {
        System.out.println("removeTeacherFromSchool");
        SecurityContext sc = null;
        RestTeacher restTeacher = null;
        Boolean expResult = null;
        Boolean result = SecuredSchoolAdminSchoolManager.removeTeacherFromSchool(sc, restTeacher);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeStudentFromSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testRemoveStudentFromSchool() {
        System.out.println("removeStudentFromSchool");
        SecurityContext sc = null;
        RestStudent restStudent = null;
        Boolean expResult = null;
        Boolean result = SecuredSchoolAdminSchoolManager.removeStudentFromSchool(sc, restStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeSchoolAdminFromSchool method, of class SecuredSchoolAdminSchoolManager.
     */
    @Test
    public void testRemoveSchoolAdminFromSchool() {
        System.out.println("removeSchoolAdminFromSchool");
        SecurityContext sc = null;
        RestSchoolAdmin restSchoolAdmin = null;
        Boolean expResult = null;
        Boolean result = SecuredSchoolAdminSchoolManager.removeSchoolAdminFromSchool(sc, restSchoolAdmin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
