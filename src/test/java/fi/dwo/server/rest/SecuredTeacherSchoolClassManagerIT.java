/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass4Teacher;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
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
public class SecuredTeacherSchoolClassManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherSchoolClassManagerIT.class.getName());

    static DatabaseManager dbInstance = null;

    public SecuredTeacherSchoolClassManagerIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbInstance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        dbInstance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
    }

    @Before
    public void setUp() {
        dbInstance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        dbInstance.ClearDatabase();
    }

    /**
     * Test of getTeachersSchoolClasses method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersSchoolClasses() {
        System.out.println("getTeachersSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestSchoolClass> result = instance.getTeachersSchoolClasses(sc);
        assertEquals(1, result.size());
    }

    /**
     * Test of getTeachersInSchool method, of class
     * SecuredTeacherSchoolClassManager.
     */
    //   @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals(2, result.size());
    }

    /**
     * Test of SubmitSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testSubmitSchoolClass() {
        System.out.println("SubmitSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass4Teacher restSchoolClass = new RestSchoolClass4Teacher();
//        restSchoolClass.setId(id);
        restSchoolClass.setIconizer(0);
        restSchoolClass.setRegistrationKey("Shaihulud");
        restSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.SubmitSchoolClass(sc, restSchoolClass);
        assertEquals("Update action threw false", true, result);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity("The worm wil eat you.", SchoolManager.findBySchoolLogin("school01"));
        assertEquals(false, schoolClass.getIconizer());
        assertEquals("The worm wil eat you.", schoolClass.getClass1());
        assertEquals("Shaihulud", schoolClass.getRegistrationKey());
    }

    /**
     * Test of GetTeachersInSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersInSchoolClass() {
        System.out.println("GetTeachersInSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass4Teacher restSchoolClass = new RestSchoolClass4Teacher();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2L, PersistenceClassType.PersistentSchoolClass);
        restSchoolClass.setId(id);
        restSchoolClass.setIconizer(0);
        restSchoolClass.setRegistrationKey("Shaihulud");
        restSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestTeacher> result = instance.GetTeachersInSchoolClass(sc, restSchoolClass);
        assertEquals(2, result.size());
    }

    /**
     * Test of GetStudentsInSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetStudentsInSchoolClass() {
        System.out.println("GetStudentsInSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass restSchoolClass = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<RestStudent> expResult = null;
        List<RestStudent> result = instance.GetStudentsInSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSchoolClasses method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<PersistentSchoolClass> expResult;
        List<RestSchoolClass> result = instance.getSchoolClasses(sc);
        //fetch classes
        expResult = SchoolClassManager.findEntities(SchoolManager.findEntity(3L));
        assertEquals(expResult.size(), result.size());
    }

    /**
     * Test of removeSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testRemoveSchoolClass() {
        System.out.println("removeSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2L, PersistenceClassType.PersistentSchoolClass);
        restSchoolClass.setId(id);
        restSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.removeSchoolClass(sc, restSchoolClass);
        assertEquals("remove returned false",expResult, result);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(restSchoolClass.getId()));
        if(schoolClass!=null){
            fail("SchoolClass still exists after removal.");
        }
        id = MySQLPersistenceId.createPersistenceId(3L, PersistenceClassType.PersistentSchoolClass);
        restSchoolClass.setId(id);
        restSchoolClass.setSchoolClassName("The worm wil eat you.");
        expResult = true;
        try{
        result = instance.removeSchoolClass(sc, restSchoolClass);
        assertNotEquals("remove returned false",expResult, result);
        }catch(Dwo2RestException e){
            //succes
        }
        schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(restSchoolClass.getId()));
        if(schoolClass==null){
            fail("Managed to delete a SchoolClass from another school.");
        }
    }

    /**
     * Test of SubmitTeacherToSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testSubmitTeacherToSchoolClass() {
        System.out.println("SubmitTeacherToSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
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
     * Test of SubmitStudentToSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testSubmitStudentToSchoolClass() {
        System.out.println("SubmitStudentToSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
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
     * Test of removeTeacherFromSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testRemoveTeacherFromSchoolClass() {
        System.out.println("removeTeacherFromSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
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
     * Test of removeStudentFromSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testRemoveStudentFromSchoolClass() {
        System.out.println("removeStudentFromSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
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
     * Test of UpdateSchoolClass method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testUpdateSchoolClass() {
        System.out.println("UpdateSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass4Teacher restSchoolClass = new RestSchoolClass4Teacher();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass);
        restSchoolClass.setId(id);
        restSchoolClass.setIconizer(0);
        restSchoolClass.setRegistrationKey("Shaihulud");
        restSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean result = instance.UpdateSchoolClass(sc, restSchoolClass);
        assertEquals("Update action threw false", true, result);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(restSchoolClass.getId()));
        assertEquals(false, schoolClass.getIconizer());
        assertEquals("The worm wil eat you.", schoolClass.getClass1());
        assertEquals("Shaihulud", schoolClass.getRegistrationKey());
    }

    /**
     * Test of updateSingleSchoolStudent method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testUpdateSingleSchoolStudent() {
        System.out.println("updateSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSingleSchoolStudent nssStudent = null;
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.updateSingleSchoolStudent(sc, nssStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
