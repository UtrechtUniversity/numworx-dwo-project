/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomNewSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomRemoveStudentFromSchoolClass;
import fi.dwo.commons.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass4Teacher;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomSubmitStudentToSchoolClass;
import fi.dwo.commons.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestNewSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestRemoveStudentFromSchoolClass;
import fi.dwo.commons.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass4Teacher;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestSubmitStudentToSchoolClass;
import fi.dwo.commons.rest.entities.RestSubmitTeacherToSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
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
    public void testGetFullSchoolClass() {
        System.out.println("getFullSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass);
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);
        domSchoolClass.setId(id);
        DomSchoolClass4Teacher result = instance.getFullSchoolClass(sc, restSchoolClass);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(domSchoolClass.getId()));
        assertEquals(schoolClass.getIconizer(), result.getIconizer());
        assertEquals(schoolClass.getClass1(), result.getSchoolClassName());
        assertEquals(schoolClass.getRegistrationKey(), result.getRegistrationKey());
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
        List<DomSchoolClass> result = instance.getTeachersSchoolClasses(sc);
        assertEquals(1, result.size());
    }

    /**
     * Test of getTeachersInSchool method, of class
     * SecuredTeacherSchoolClassManager. Tests if the proper number of teachers
     * is returned for a known school.
     */
    //   @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<DomTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals(2, result.size());
    }

    /**
     * Test of SubmitSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests if a proper new SchoolClass can
     * be submitted in a known school
     */
    @Test
    public void testSubmitSchoolClass() {
        System.out.println("SubmitSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass4Teacher restSchoolClass = new RestSchoolClass4Teacher();
        DomSchoolClass4Teacher domSchoolClass = new DomSchoolClass4Teacher();
        restSchoolClass.setDomSchoolClass4Teacher(domSchoolClass);
//        restSchoolClass.setId(id);
        domSchoolClass.setIconizer(false);
        domSchoolClass.setRegistrationKey("Shaihulud");
        domSchoolClass.setSchoolClassName("The worm wil eat you.");
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
     * SecuredTeacherSchoolClassManager. Tests whether the correct number of
     * teachers is returned for a known school class.
     */
    @Test
    public void testGetTeachersInSchoolClass() {
        System.out.println("GetTeachersInSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        DomSchoolClass domSchoolClass = new DomSchoolClass();
         restSchoolClass.setDomSchoolClass(domSchoolClass);
       PersistenceId id = MySQLPersistenceId.createPersistenceId(2L, PersistenceClassType.PersistentSchoolClass);
        domSchoolClass.setId(id);
        domSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<DomTeacher> result = instance.GetTeachersInSchoolClass(sc, restSchoolClass);
        assertEquals(2, result.size());
    }

    /**
     * Test of GetStudentsInSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests whether the proper number of
     * students is returned for a known school class.
     */
    @Test
    public void testGetStudentsInSchoolClass() {
        System.out.println("GetStudentsInSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2L, PersistenceClassType.PersistentSchoolClass);
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);
        domSchoolClass.setId(id);
        domSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        List<DomStudent> result = instance.GetStudentsInSchoolClass(sc, restSchoolClass);
        assertEquals(3L, result.size());
    }

//    /**
//     * Test of getSchoolClasses method, of class
//     * SecuredTeacherSchoolClassManager.
//     */
//    @Test
//    public void testGetSchoolClasses() {
//        System.out.println("getSchoolClasses");
//        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
//        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
//        List<PersistentSchoolClass> expResult;
//        List<RestSchoolClass> result = instance.getSchoolClasses(sc);
//        //fetch classes
//        expResult = SchoolClassManager.findEntities(SchoolManager.findEntity(3L));
//        assertEquals(expResult.size(), result.size());
//    }
    /**
     * Test of removeSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests whether a proper school class can
     * be removed by a teacher. Tests whether a school class can't be removed
     * from another school.
     */
    @Test
    public void testRemoveSchoolClass() {
        System.out.println("removeSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2L, PersistenceClassType.PersistentSchoolClass);
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);
        domSchoolClass.setId(id);
        domSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.removeSchoolClass(sc, restSchoolClass);
        assertEquals("remove returned false", expResult, result);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass != null) {
            fail("SchoolClass still exists after removal.");
        }
        id = MySQLPersistenceId.createPersistenceId(3L, PersistenceClassType.PersistentSchoolClass);
        domSchoolClass.setId(id);
        domSchoolClass.setSchoolClassName("The worm wil eat you.");
        expResult = true;
        try {
            result = instance.removeSchoolClass(sc, restSchoolClass);
            assertNotEquals("remove returned false", expResult, result);
        }
        catch (Dwo2RestException e) {
            //succes
        }
        schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass == null) {
            fail("Managed to delete a SchoolClass from another school.");
        }
    }

    /**
     * Test of SubmitTeacherToSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests if a proper teacher is properly
     * be submitted to a school class.
     */
    @Test
    public void testSubmitTeacherToSchoolClass() {
        System.out.println("SubmitTeacherToSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass01");
        DomTeacher domTeacher = new DomTeacher();
        domTeacher.setId(MySQLPersistenceId.createPersistenceId(10L, PersistenceClassType.PersistentUser));
        domTeacher.setUserName("user03");
        domTeacher.setGivenName("User");
        domTeacher.setFamilyName("Lastname 03");
        RestSubmitTeacherToSchoolClass restSubmitTeacherToSchoolClass = new RestSubmitTeacherToSchoolClass();
        restSubmitTeacherToSchoolClass.setDomSubmitTeacherToSchoolClass(new DomSubmitTeacherToSchoolClass());
        restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().setSchoolClass(domSchoolClass);
        restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().setTeacher(domTeacher);
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.SubmitTeacherToSchoolClass(sc, restSubmitTeacherToSchoolClass);
        assertEquals("Method returned false.", expResult, result);
        PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
        key.setClassID(1L);
        key.setSchoolGroupID(3L);
        key.setUserID(10L);
        PersistentTeacherOfClass teacher = TeacherOfClassManager.findEntity(key);
        assertNotEquals("Teacher not added to schoolclass", teacher, null);
    }

    /**
     * Test of SubmitStudentToSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests if a student is properly
     * submitted to known school class.
     */
    @Test
    public void testSubmitStudentToSchoolClass() {
        System.out.println("SubmitStudentToSchoolClass");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.TEACHER);//school01
        PersistentUser user = UserManager.findByUserName("user02");
        user.setSchoolGroupId(6L);
        UserManager.edit(user);
        DomStudent domStudent = new DomStudent();
        domStudent.setId(MySQLPersistenceId.createPersistenceId(12L, PersistenceClassType.PersistentUser));
        domStudent.setUserName("user05");
        domStudent.setGivenName("User");
        domStudent.setFamilyName("Lastname 05");
        DomSchoolClass domFromSchoolClass = new DomSchoolClass();
        domFromSchoolClass.setId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchoolClass));
        domFromSchoolClass.setSchoolClassName("SchoolClass03");
        DomSchoolClass domToSchoolClass = new DomSchoolClass();
        domToSchoolClass.setId(MySQLPersistenceId.createPersistenceId(4, PersistenceClassType.PersistentSchoolClass));
        domToSchoolClass.setSchoolClassName("SchoolClass04");
        RestSubmitStudentToSchoolClass restSubmitStudentToSchoolClass = new RestSubmitStudentToSchoolClass();
        restSubmitStudentToSchoolClass.setDomSubmitStudentToSchoolClass(new DomSubmitStudentToSchoolClass());
        restSubmitStudentToSchoolClass.getDomSubmitStudentToSchoolClass().setSchoolFromClass(domFromSchoolClass);
        restSubmitStudentToSchoolClass.getDomSubmitStudentToSchoolClass().setSchoolToClass(domToSchoolClass);
        restSubmitStudentToSchoolClass.getDomSubmitStudentToSchoolClass().setStudent(domStudent);
        
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.SubmitStudentToSchoolClass(sc, restSubmitStudentToSchoolClass);
        assertEquals("Method returned false.", expResult, result);
        PersistentStudentOfClassPK key = new PersistentStudentOfClassPK();
        key.setClassID(4L);
        key.setSchoolGroupID(5L);
        key.setUserID(12L);
        PersistentStudentOfClass soc = StudentOfClassManager.findEntity(key);
        assertNotEquals("Student not added to schoolclass", soc, null);
    }

    /**
     * Test of removeTeacherFromSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests if a proper teacher is removed
     * from a known class. Tests if a teacher can't be removed from a class from
     * a different school.
     */
    @Test
    public void testRemoveTeacherFromSchoolClass() {
        System.out.println("removeTeacherFromSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        DomRemoveTeacherFromSchoolClass domRemoveTeacherFromSchoolClass = new DomRemoveTeacherFromSchoolClass();
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass02");
        DomTeacher domTeacher = new DomTeacher();
        domTeacher.setId(MySQLPersistenceId.createPersistenceId(10L, PersistenceClassType.PersistentUser));
        domTeacher.setUserName("user03");
        domTeacher.setGivenName("User");
        domTeacher.setFamilyName("Lastname 03");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        RestRemoveTeacherFromSchoolClass restRemoveTeacherFromSchoolClass = new RestRemoveTeacherFromSchoolClass();
        restRemoveTeacherFromSchoolClass.setDomRemoveTeacherFromSchoolClass(domRemoveTeacherFromSchoolClass);
        restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().setSchoolClass(domSchoolClass);
        restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().setTeacher(domTeacher);

        Boolean result = instance.removeTeacherFromSchoolClass(sc, restRemoveTeacherFromSchoolClass);
        assertEquals(expResult, result);
        PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
        key.setUserID(10L);
        key.setClassID(02L);
        key.setSchoolGroupID(03L);
        PersistentTeacherOfClass newTeacher = TeacherOfClassManager.findEntity(key);
        assertEquals("Teacher was not deleted.", newTeacher, null);

        //fail next
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass03");
        domTeacher.setId(MySQLPersistenceId.createPersistenceId(10L, PersistenceClassType.PersistentUser));
        domTeacher.setUserName("user03");
        domTeacher.setGivenName("User");
        domTeacher.setFamilyName("Lastname 03");
        restRemoveTeacherFromSchoolClass = new RestRemoveTeacherFromSchoolClass();
        restRemoveTeacherFromSchoolClass.setDomRemoveTeacherFromSchoolClass(domRemoveTeacherFromSchoolClass);
        restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().setSchoolClass(domSchoolClass);
        restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().setTeacher(domTeacher);
        try {
            result = instance.removeTeacherFromSchoolClass(sc, restRemoveTeacherFromSchoolClass);
            assertEquals("Teacher removed while this should not occur.", false, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
    }

    /**
     * Test of removeStudentFromSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests if a proper teacher is removed
     * from a known class. Tests if a teacher can't be removed from a class from
     * a different school.
     */
    @Test
    public void testRemoveStudentFromSchoolClass() {
        System.out.println("removeStudentFromSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass02");
        DomStudent domStudent = new DomStudent();
        domStudent.setId(MySQLPersistenceId.createPersistenceId(9L, PersistenceClassType.PersistentUser));
        domStudent.setUserName("user02");
        domStudent.setGivenName("User");
        domStudent.setFamilyName("Lastname 02");
        RestRemoveStudentFromSchoolClass restRemoveStudentFromSchoolClass = new RestRemoveStudentFromSchoolClass();
        restRemoveStudentFromSchoolClass.setDomRemoveStudentFromSchoolClass(new DomRemoveStudentFromSchoolClass());
        restRemoveStudentFromSchoolClass.getDomRemoveStudentFromSchoolClass().setSchoolClass(domSchoolClass);
        restRemoveStudentFromSchoolClass.getDomRemoveStudentFromSchoolClass().setStudent(domStudent);

        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.removeStudentFromSchoolClass(sc, restRemoveStudentFromSchoolClass);
        assertEquals(expResult, result);

        PersistentStudentOfClassPK key = new PersistentStudentOfClassPK();
        key.setUserID(9L);
        key.setClassID(02L);
        key.setSchoolGroupID(02L);
        PersistentStudentOfClass newStudent = StudentOfClassManager.findEntity(key);
        assertEquals("Student was not deleted.", newStudent, null);
    }

    /**
     * Test of UpdateSchoolClass method, of class
     * SecuredTeacherSchoolClassManager. Tests if the properties of a school
     * class can be updated by one of its teachers.
     */
    @Test
    public void testUpdateSchoolClass() {
        System.out.println("UpdateSchoolClass");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSchoolClass4Teacher restSchoolClass = new RestSchoolClass4Teacher();
        PersistenceId id = MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass);
        DomSchoolClass4Teacher domSchoolClass = new DomSchoolClass4Teacher();
        restSchoolClass.setDomSchoolClass4Teacher(domSchoolClass);
        domSchoolClass.setId(id);
        domSchoolClass.setIconizer(false);
        domSchoolClass.setRegistrationKey("Shaihulud");
        domSchoolClass.setSchoolClassName("The worm wil eat you.");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean result = instance.UpdateSchoolClass(sc, restSchoolClass);
        assertEquals("Update action threw false", true, result);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(domSchoolClass.getId()));
        assertEquals(false, schoolClass.getIconizer());
        assertEquals("The worm wil eat you.", schoolClass.getClass1());
        assertEquals("Shaihulud", schoolClass.getRegistrationKey());
    }

    /**
     * Test of updateSingleSchoolStudent method, of class
     * SecuredTeacherSchoolClassManager. Tests if only one of its single school
     * students can be updated.
     */
    @Test
    public void testUpdateSingleSchoolStudent() {
        System.out.println("updateSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        RestSingleSchoolStudent nssStudent = new RestSingleSchoolStudent();
        DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
        nssStudent.setDomSingleSchoolStudent(dssStudent);
        
        dssStudent.setId(MySQLPersistenceId.createPersistenceId(11L, PersistenceClassType.PersistentUser));
        dssStudent.setUserName("user04"); //changing is not allowed.
        dssStudent.setGivenName("User");
        dssStudent.setFamilyName("Lastname 04");
        dssStudent.setPassword("bla");
        dssStudent.setEmail("blamail");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.updateSingleSchoolStudent(sc, nssStudent);
        assertEquals(expResult, result);
        PersistentUser user = UserManager.findEntity(11L);
        assertEquals(user.getEmail(), dssStudent.getEmail());
        assertEquals(user.getGivenName(), dssStudent.getGivenName());
        assertEquals(user.getLastname(), dssStudent.getFamilyName());
        assertEquals(user.getInsertion(), dssStudent.getInsertion());
        assertEquals(user.getPassword(), dssStudent.getPassword());
        assertEquals(user.isSingleSchoolAccount(), true);
        assertEquals(user.getUsername(), dssStudent.getUserName());

        //try if a non-single school student can be updated
        dssStudent.setUserName("user03");
        dssStudent.setGivenName("User");
        dssStudent.setFamilyName("Lastname 02");
        dssStudent.setPassword("bla");
        dssStudent.setEmail("blamail");
        expResult = false;
        try {
            result = instance.updateSingleSchoolStudent(sc, nssStudent);
            assertEquals(expResult, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
    }


    /**
     * Test of SubmitSingleSchoolStudent method, of class
     * SecuredSchoolAdminSchoolClassManager. Tests if a single student student
     * can be added. Tests only for a proper request. 
     */
    @Test
    public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01

        RestNewSingleSchoolStudent rss = new RestNewSingleSchoolStudent();
        DomNewSingleSchoolStudent nss = new DomNewSingleSchoolStudent();
        DomSingleSchoolStudent dss = new DomSingleSchoolStudent();
        rss.setDomNewSingleSchoolStudent(nss);
        nss.setDomSingleSchoolStudent(dss);
        dss.setUserName("singleschooluser");
        dss.setGivenName("a");
        dss.setInsertion("b");
        dss.setFamilyName("c");
        dss.setEmail("a@b.c");
        dss.setPassword("pwd");
        SecuredTeacherSchoolClassManager instance = new SecuredTeacherSchoolClassManager();
        Boolean result = instance.SubmitSingleSchoolStudent(sc, rss);
        assertEquals("Operation failed to be true.", true, result);

        //fetch user and hasrole and class if given?
        PersistentUser user = UserManager.findByUserName(dss.getUserName());
        assertEquals("Given name not as expected.", dss.getGivenName(), user.getGivenName());
        assertEquals("Insertion not as expected.", dss.getInsertion(), user.getInsertion());
        assertEquals("Familyname not as expected.", dss.getFamilyName(), user.getLastname());
        assertEquals("Email not as expected.", dss.getEmail(), user.getEmail());
        assertEquals("Password not as expected.", dss.getPassword(), user.getPassword());
        assertEquals("Did not creat a single schoolstudent.", user.isSingleSchoolAccount(), true);
        try {
            //check for hasRole
            PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findEntity(3L), RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find created user's hasRole");
        }
    }    
}
