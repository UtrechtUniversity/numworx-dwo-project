/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass4Teacher;
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
public class SecuredTeacherSchoolClassManagerIT {
    
    public SecuredTeacherSchoolClassManagerIT() {
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
     * Test of getTeachersSchoolClasses method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersSchoolClasses() {
        System.out.println("getTeachersSchoolClasses");
        SecurityContext sc = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestSchoolClass> expResult = null;
        List<RestSchoolClass> result = instance.getTeachersSchoolClasses(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTeachersInSchool method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testSubmitSchoolClass() {
        System.out.println("SubmitSchoolClass");
        SecurityContext sc = null;
        PersistentSchoolClass schoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitSchoolClass(sc, schoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of GetTeachersInSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersInSchoolClass() {
        System.out.println("GetTeachersInSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.GetTeachersInSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of GetStudentsInSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetStudentsInSchoolClass() {
        System.out.println("GetStudentsInSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestStudent> expResult = null;
        List<RestStudent> result = instance.GetStudentsInSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSchoolClasses method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestSchoolClass> expResult = null;
        List<RestSchoolClass> result = instance.getSchoolClasses(sc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testRemoveSchoolClass() {
        System.out.println("removeSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitTeacherToSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testSubmitTeacherToSchoolClass() {
        System.out.println("SubmitTeacherToSchoolClass");
        SecurityContext sc = null;
        RestTeacher restTeacher = null;
        RestSchoolClass restSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitTeacherToSchoolClass(sc, restTeacher, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitStudentToSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testSubmitStudentToSchoolClass() {
        System.out.println("SubmitStudentToSchoolClass");
        SecurityContext sc = null;
        RestStudent restStudent = null;
        RestSchoolClass restFromSchoolClass = null;
        RestSchoolClass restToSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitStudentToSchoolClass(sc, restStudent, restFromSchoolClass, restToSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeTeacherFromSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testRemoveTeacherFromSchoolClass() {
        System.out.println("removeTeacherFromSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        RestTeacher restTeacher = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeTeacherFromSchoolClass(sc, restSchoolClass, restTeacher);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeStudentFromSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testRemoveStudentFromSchoolClass() {
        System.out.println("removeStudentFromSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        RestStudent restStudent = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeStudentFromSchoolClass(sc, restSchoolClass, restStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of UpdateSchoolClass method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testUpdateSchoolClass() {
        System.out.println("UpdateSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass4Teacher restSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.UpdateSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSingleSchoolStudent method, of class SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testUpdateSingleSchoolStudent() {
        System.out.println("updateSingleSchoolStudent");
        SecurityContext sc = null;
        RestSingleSchoolStudent nssStudent = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.updateSingleSchoolStudent(sc, nssStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
