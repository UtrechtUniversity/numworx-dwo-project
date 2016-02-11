/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomSchoolAdmin;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestSchoolAdmin;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
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
public class SecuredSchoolAdminSchoolManagerIT {
    
    private static final Logger LOG = Logger.getLogger(SecuredSchoolAdminSchoolManagerIT.class.getName());
    
    static DatabaseManager dbInstance = null;
    
    public SecuredSchoolAdminSchoolManagerIT() {
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
     * Test of getTeachersInSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if the proper number of teachers
     * is returned for a known school.
     */
    @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<DomTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals(2, result.size());
    }

    /**
     * Test of getStudentsInSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if the proper number of students is 
     * returned for a known school.
     */
    @Test
    public void testGetStudentsInSchool() {
        System.out.println("getStudentsInSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<DomStudent> result = instance.getStudentsInSchool(sc);
        assertEquals(3, result.size());
    }

    /**
     * Test of getSchoolAdminInSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if the proper number of school admins
     * is returned for a known school.
     */
    @Test
    public void testGetSchoolAdminInSchool() {
        System.out.println("getSchoolAdminInSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<DomTeacher> result = instance.getSchoolAdminInSchool(sc);
        assertEquals(1L, result.size());
    }

    /**
     * Test of removeSingleSchoolStudentFromSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if a known single school student
     * is removed from its school and whether all its data is removed.
     */
    @Test
    public void testRemoveSingleSchoolStudentFromSchool() {
        System.out.println("removeSingleSchoolStudentFromSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01

//        RestSchool restSchool = new RestSchool(SchoolManager.findBySchoolLogin("school01"));
//        restSchool.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchool));
//        restSchool.setSchoolName("SchoolClass02");
        PersistentUser user = (PersistentUser) UserManager.findByUserName("user02");
        RestStudent restStudent = new RestStudent(user);
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        try {
            Boolean result = instance.removeSingleSchoolStudentFromSchool(sc, restStudent);
            assertEquals("Student was removed but is not a SingleSchoolStudent.", true, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
        user = (PersistentUser) UserManager.findByUserName("user02");
        if (user == null) {
            fail("Student was removed but is not a SingleSchoolStudent.");
        }

        //fetch user
        user = (PersistentUser) UserManager.findByUserName("user04");
        if (user == null) {
            fail("Test student is missing from the test database.");
        }

        //fetch hasrole
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            fail("Student did not have a hasRole in the test database. He should.");
        }
        
        restStudent = new RestStudent(user);
        Boolean result = instance.removeSingleSchoolStudentFromSchool(sc, restStudent);
        assertEquals("Student was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user04");
        if (userResult != null) {
            fail("Student was not removed.");
        }
        try {
            HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
            fail("HasRole was not removed.");
        }
        catch (Dwo2Exception ex) {
            //success
        }
        
        List<PersistentStudentOfClass> soc = StudentOfClassManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, soc.size());

        // test for studentsco data
        List<PersistentStudentScoContext> scoc = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, scoc.size());
        
    }

    /**
     * Test of SubmitSingleSchoolStudent method, of class
     * SecuredSchoolAdminSchoolManager. Tests if a single school student can be
     * added to a known school, has a proper hasRole and is a single school student.
     */
    @Test
    public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        RestSingleSchoolStudent rssStudent = new RestSingleSchoolStudent();
        DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
        rssStudent.setDomSingleSchoolStudent(dssStudent);
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        System.out.println("submitNewUser");
        dssStudent.setUserName("testuser01");
        dssStudent.setGivenName("a");
        dssStudent.setInsertion("b");
        dssStudent.setFamilyName("c");
        dssStudent.setEmail("a@b.c");
        dssStudent.setPassword("pwd");
        
        try {
            Boolean result = instance.SubmitSingleSchoolStudent(sc, rssStudent);
            assertEquals(true, result);
        }
        catch(Dwo2RestException ex) {
            fail("Student submit failed.");
        }
        
        PersistentUser user = UserManager.findByUserName(dssStudent.getUserName());
        assertEquals(dssStudent.getGivenName(), user.getGivenName());
        assertEquals(dssStudent.getInsertion(), user.getInsertion());
        assertEquals(dssStudent.getFamilyName(), user.getLastname());
        assertEquals(dssStudent.getEmail(), user.getEmail());
        assertEquals(dssStudent.getPassword(), user.getPassword());
        assertEquals(true, user.isSingleSchoolAccount());
        
        try {
            //check for hasRole
            PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(user, SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find created user's hasRole");
        }
    }
    
    /**
     * Test of updateSingleSchoolStudent method, of class
     * SecuredTeacherSchoolClassManager. Tests if only one of its single school
     * students can be updated.
     */
    @Test
    public void testUpdateSingleSchoolStudent() {
        System.out.println("updateSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        RestSingleSchoolStudent nssStudent = new RestSingleSchoolStudent();
        DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
        nssStudent.setDomSingleSchoolStudent(dssStudent);
        
        dssStudent.setId(MySQLPersistenceId.createPersistenceId(11L, PersistenceClassType.PersistentUser));
        dssStudent.setUserName("user04"); //changing is not allowed.
        dssStudent.setGivenName("User");
        dssStudent.setFamilyName("Lastname 04");
        dssStudent.setPassword("bla");
        dssStudent.setEmail("blamail");
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
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
     * Test of getSchoolClasses method, of class
     * SecuredSchoolAdminSchoolClassManager.
     *
     * Tests for the number of SchoolClasses in a known school.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<PersistentSchoolClass> expResult;
        List<DomSchoolClass> result = instance.getSchoolClasses(sc);
        //fetch classes
        expResult = SchoolClassManager.findEntities(SchoolManager.findEntity(3L));
        assertEquals(expResult.size(), result.size());
    }

    /**
     * Test of removeTeacherFromSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if a teacher is removed from a school
     * and whether all the school related data is removed.
     */
    @Test
    public void testRemoveTeacherFromSchool() {
        System.out.println("removeTeacherFromSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        PersistentUser user = (PersistentUser) UserManager.findByUserName("user04");
        RestTeacher restTeacher = new RestTeacher();
        DomTeacher domTeacher = new DomTeacher(user);
        restTeacher.setDomTeacher(domTeacher);
        try {
            Boolean expResult = null;
            Boolean result = instance.removeTeacherFromSchool(sc, restTeacher);
            assertEquals(true, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
        user = (PersistentUser) UserManager.findByUserName("user03");
        if (user == null) {
            fail("User was removed but is not a Teacher.");
        }

        //fetch user
        user = (PersistentUser) UserManager.findByUserName("user03");
        if (user == null) {
            fail("Test Teacher is missing from the test database.");
        }

        //fetch hasrole
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            fail("Teacher did not have a hasRole in the test database. He should.");
        }
        
        domTeacher = new DomTeacher(user);
        restTeacher.setDomTeacher(domTeacher);
        Boolean result = instance.removeTeacherFromSchool(sc, restTeacher);
        assertEquals("Teacher was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user03");
        if (userResult == null) {
            fail("User was removed, while it should remain.");
        }
        try {
            HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.TEACHER);
            fail("HasRole was not removed.");
        }
        catch (Dwo2Exception ex) {
            //success
        }
        
        List<PersistentTeacherOfClass> soc = TeacherOfClassManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, soc.size());

        // test for studentsco data
        List<PersistentStudentScoContext> scoc = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, scoc.size());        
    }

    /**
     * Test of removeStudentFromSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests whether a student can be removed
     * from a known school and all the school-related data is removed.
     */
    @Test
    public void testRemoveStudentFromSchool() {
        System.out.println("removeStudentFromSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        PersistentUser user = (PersistentUser) UserManager.findByUserName("user04");
        RestStudent restStudent = new RestStudent(user);
        try {
            Boolean result = instance.removeStudentFromSchool(sc, restStudent);
            assertEquals("SingleSchoolStudent was removed but should fail.", false, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
        user = (PersistentUser) UserManager.findByUserName("user04");
        if (user == null) {
            fail("Student was removed but is a SingleSchoolStudent.");
        }

        //fetch user
        user = (PersistentUser) UserManager.findByUserName("user02");
        if (user == null) {
            fail("Test student is missing from the test database.");
        }

        //fetch hasrole
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            fail("Student did not have a hasRole in the test database. He should.");
        }
        
        restStudent = new RestStudent(user);
        Boolean result = instance.removeStudentFromSchool(sc, restStudent);
        assertEquals("Student was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user02");
        if (userResult == null) {
            fail("User was removed,while it should remain.");
        }
        try {
            HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
            fail("HasRole was not removed.");
        }
        catch (Dwo2Exception ex) {
            //success
        }
        
        List<PersistentStudentOfClass> soc = StudentOfClassManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, soc.size());

        // test for studentsco data
        List<PersistentStudentScoContext> scoc = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, scoc.size());
        
    }

    /**
     * Test of removeSchoolAdminFromSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests whether a schooladmin can be removed
     * from a known school all the school-related data is removed. it also tests for
     * removing StudentScoContext and StudentScoData. This is for the transition 
     * from 1.0 to 2.0 as a schooladmin should not be able to create it in Dwo 2.0.
     */
    @Test
    public void testRemoveSchoolAdminFromSchool() {
        System.out.println("removeSchoolAdminFromSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        PersistentUser user = (PersistentUser) UserManager.findByUserName("user04");
        RestSchoolAdmin restSchoolAdmin = new RestSchoolAdmin();
        DomSchoolAdmin domSchoolAdmin = new DomSchoolAdmin(user);
        restSchoolAdmin.setDomSchoolAdmin(domSchoolAdmin);
        try {
            Boolean result = instance.removeSchoolAdminFromSchool(sc, restSchoolAdmin);
            assertEquals(true, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
        user = (PersistentUser) UserManager.findByUserName("user04");
        if (user == null) {
            fail("User was removed but is not a SchoolAdmin.");
        }

        //fetch user
        user = (PersistentUser) UserManager.findByUserName("user06");
        if (user == null) {
            fail("Test SchoolAdmin is missing from the test database.");
        }

        //fetch hasrole
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.SCHOOLADMIN);
        }
        catch (Dwo2Exception ex) {
            fail("SchoolAdmin did not have a hasRole in the test database. He should.");
        }
        
        domSchoolAdmin = new DomSchoolAdmin(user);
        restSchoolAdmin.setDomSchoolAdmin(domSchoolAdmin);
        Boolean result = instance.removeSchoolAdminFromSchool(sc, restSchoolAdmin);
        assertEquals("SchoolAdmin was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user06");
        if (userResult == null) {
            fail("User was removed, while it should remain.");
        }
        try {
            HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.SCHOOLADMIN);
            fail("HasRole was not removed.");
        }
        catch (Dwo2Exception ex) {
            //success
        }

        // test for studentsco data
        List<PersistentStudentScoContext> scoc = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, scoc.size());        
    }
}
