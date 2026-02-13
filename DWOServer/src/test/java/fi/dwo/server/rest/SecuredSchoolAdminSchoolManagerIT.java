/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolOrganisation;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.entities.RestUserFullv2;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.rest.util.Origin;
import fi.dwo.server.testutil.TestSecurityContext;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
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
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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
    @Test
    public void testGetTeachersAndHasRoleInSchool() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        RestContext rest = new RestContext();
        rest.setRestContext(new DomContext());
		List<DomTeacherAndHasRole> result = instance.getTeachersAndHasRoleInSchool(sc, rest);
        assertEquals(2, result.size());
    }

    /**
     * Test of getStudentsInSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if the proper number of students
     * is returned for a known school.
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
     * Test of getSchoolAdminsInSchool method, of class
     * SecuredSchoolAdminSchoolManager. Tests if the proper number of school
     * admins is returned for a known school.
     */
    @Test
    public void testGetSchoolAdminInSchool() {
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        List<DomSchoolAdmin> result = instance.getSchoolAdminsInSchool(sc);
        assertEquals(1L, result.size());
    }
    @Test
    public void testGetSchoolAdminAndHasRoleInSchool() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        RestContext rest = new RestContext();
        rest.setRestContext(new DomContext());
		List<DomSchoolAdminAndHasRole> result = instance.getSchoolAdminsAndHasRoleInSchool(sc, rest);
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
        RestStudent restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(user.buildDomStudent(null));
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        try {
            Boolean result = instance.removeSingleSchoolStudentFromSchool(sc, restStudent);
            assertEquals("Student was removed but is not a SingleSchoolStudent.", true, result);
        } catch (Dwo2RestException e) {
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
            hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            fail("Student did not have a hasRole in the test database. He should.");
        }

        restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(user.buildDomStudent(null));
        Boolean result = instance.removeSingleSchoolStudentFromSchool(sc, restStudent);
        assertEquals("Student was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user04");
        if (userResult != null) {
            fail("Student was not removed.");
        }
        try {
            HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
            fail("HasRole was not removed.");
        } catch (Dwo2Exception ex) {
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
     * added to a known school, has a proper hasRole and is a single school
     * student.
     */
    @Test
    public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        RestNewSingleSchoolStudent rssStudent = new RestNewSingleSchoolStudent();
        DomNewSingleSchoolStudent nssStudent = new DomNewSingleSchoolStudent();
        DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
        rssStudent.setRestContext(new DomContext());
        rssStudent.setDomNewSingleSchoolStudent(nssStudent);
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        dssStudent.setUserName("testuser01");
        dssStudent.setGivenName("a");
        dssStudent.setInsertion("b");
        dssStudent.setFamilyName("c");
        dssStudent.setEmail("a@b.cd");
        dssStudent.setPassword("pwd");
        nssStudent.setDomSingleSchoolStudent(dssStudent);
        nssStudent.setDomSchoolClass(null);

        System.out.println("submitNewUser without a schoolclass");
        try {
            Boolean result = instance.submitSingleSchoolStudent(sc, rssStudent);
            assertEquals(true, result);
        } catch (Dwo2RestException ex) {
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
            PersistentHasRole hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, "", ex);
            fail("Could not find created user's hasRole");
        }

        System.out.println("submitNewUser with a schoolclass");
        dssStudent.setUserName("testuser02");
        DomSchoolClass schoolClass = SchoolClassManager.findEntity(1L).buildDomSchoolClass();//SchoolClass01        
        nssStudent.setDomSchoolClass(schoolClass);

        try {
            Boolean result = instance.submitSingleSchoolStudent(sc, rssStudent);
            assertEquals(true, result);
        } catch (Dwo2RestException ex) {
            fail("Student submit failed.");
        }

        user = UserManager.findByUserName(dssStudent.getUserName());
        assertEquals(dssStudent.getGivenName(), user.getGivenName());
        assertEquals(dssStudent.getInsertion(), user.getInsertion());
        assertEquals(dssStudent.getFamilyName(), user.getLastname());
        assertEquals(dssStudent.getEmail(), user.getEmail());
        assertEquals(dssStudent.getPassword(), user.getPassword());
        assertEquals(true, user.isSingleSchoolAccount());

        PersistentHasRole hr = null;
        try {
            //check for hasRole
            hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, "", ex);
            fail("Could not find created user's hasRole");
        }
        try {
            //check for schoolClass
            PersistentSchoolClass pSchoolClass = SchoolClassManager.findEntity(1L);
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(
                    new PersistentStudentOfClassPK(hr.getPersistentHasRolePK().getUserID(),
                            hr.getClassID(), hr.getPersistentHasRolePK().getSchoolGroupID()));
            assertNotEquals(pSchoolClass, null);
        } catch (Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, "", ex);
            fail("Could not find created student of class.");
        }
    }
 
    @Test public void testSubmitTeacher() throws Dwo2Exception { 
      SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
      SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
      
      RestUserFull teacher = new RestUserFull();
      DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
      dssStudent.setUserName("testuser01");
      dssStudent.setGivenName("a");
      dssStudent.setInsertion("b");
      dssStudent.setFamilyName("c");
      dssStudent.setEmail("a@b.cd");
      dssStudent.setPassword("pwd");
      teacher.setDomUserFull(dssStudent);
      teacher.setRestContext(new DomContext());
      
      Boolean result = instance.submitTeacher(sc, teacher);
      assertTrue(result);

      PersistentUser user = UserManager.findByUserName(dssStudent.getUserName());
      assertEquals(dssStudent.getGivenName(), user.getGivenName());
      assertEquals(dssStudent.getInsertion(), user.getInsertion());
      assertEquals(dssStudent.getFamilyName(), user.getLastname());
      assertEquals(dssStudent.getEmail(), user.getEmail());
      assertEquals(dssStudent.getPassword(), user.getPassword());
      assertFalse(user.isSingleSchoolAccount());
      PersistentHasRole hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, SchoolManager.findBySchoolLogin("school01"), RoleType.TEACHER);
      assertNotNull(hr);
      
    }
 
    @Test public void testSubmitTeacherv2() throws Dwo2Exception, AddressException, IOException, MessagingException { 
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
    	PersistentUser user = UserManager.findByUserName("user06");
    	PersistentSchool school = SchoolManager.findBySchoolLogin("school01");
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        
        RestUserFullv2 teacher = new RestUserFullv2();
        DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
        dssStudent.setUserName("testuser01");
        dssStudent.setGivenName("a");
        dssStudent.setInsertion("b");
        dssStudent.setFamilyName("c");
        dssStudent.setEmail("a@b.cd");
        dssStudent.setPassword("pwd05");
        teacher.setDomUserFull(dssStudent);
        teacher.setDwoProfile(new DomDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L), 1L));
        teacher.setRestContext(new DomContext());
        ServletContext context = new MockServletContext();
        context.setInitParameter("fi.dwo.server.rest.smtp.server", "localhost");
        context.setInitParameter("fi.dwo.server.rest.smtp.port", "2525");
        context.setInitParameter("fi.dwo.server.rest.smtp.tls", "false");
        context.setInitParameter("fi.dwo.server.rest.smtp.ssl", "false");
        context.setInitParameter("fi.dwo.server.rest.smtp.auth", "false");
        context.setInitParameter("fi.dwo.server.rest.smtp.email", "noreply@example.com");
        Origin.ORIGINS[0] = "http://localhost:8080";
        DomHasRole hr = HasRoleUtilManager.getHasRole(user.getId(), RoleType.SCHOOLADMIN, school).buildDomHasRole();
        teacher.getRestContext().setDomHasRole(hr);

		Boolean result = instance.submitTeacher2(sc, teacher, context);
        assertTrue(result);

        user = UserManager.findByUserName(dssStudent.getUserName());
        assertEquals(dssStudent.getGivenName(), user.getGivenName());
        assertEquals(dssStudent.getInsertion(), user.getInsertion());
        assertEquals(dssStudent.getFamilyName(), user.getLastname());
        assertEquals(dssStudent.getEmail(), user.getEmail());
        assertEquals(dssStudent.getPassword(), user.getPassword());
        assertFalse(user.isSingleSchoolAccount());
        PersistentHasRole phr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, SchoolManager.findBySchoolLogin("school01"), RoleType.TEACHER);
        assertNotNull(phr);
        
      }

    
    
    
    
    
    
    @Test public void testSubmitTeacher_expire() { 
      PersistentSchool school = SchoolManager.findBySchoolLogin("school01");    
      school.setExpire(new Date(0));
      SchoolManager.edit(school);
      SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
      SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
      
      RestUserFull teacher = new RestUserFull();
      DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
      dssStudent.setUserName("testuser01");
      dssStudent.setGivenName("a");
      dssStudent.setInsertion("b");
      dssStudent.setFamilyName("c");
      dssStudent.setEmail("a@b.cd");
      dssStudent.setPassword("pwd");
      teacher.setDomUserFull(dssStudent);
      teacher.setRestContext(new DomContext());
    try {  
      Boolean result = instance.submitTeacher(sc, teacher);
      assertFalse(result);
    } catch (Dwo2RestException ex) {
      assertEquals(Dwo2ExceptionCode.Rest_Registration_School_license_expired, ex.getDwo2Code());
  }

      PersistentUser user = UserManager.findByUserName(dssStudent.getUserName());
      assertNull(user);
    }
    
    
    
    @Test
    public void testSubmitSingleSchoolStudent_expire() {
        System.out.println("SubmitSingleSchoolStudent");
        PersistentSchool school = SchoolManager.findBySchoolLogin("school01");    
        school.setExpire(new Date(0));
        SchoolManager.edit(school);
       SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        RestNewSingleSchoolStudent rssStudent = new RestNewSingleSchoolStudent();
        DomNewSingleSchoolStudent nssStudent = new DomNewSingleSchoolStudent();
        DomSingleSchoolStudent dssStudent = new DomSingleSchoolStudent();
        rssStudent.setRestContext(new DomContext());
        rssStudent.setDomNewSingleSchoolStudent(nssStudent);
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        dssStudent.setUserName("testuser01");
        dssStudent.setGivenName("a");
        dssStudent.setInsertion("b");
        dssStudent.setFamilyName("c");
        dssStudent.setEmail("a@b.cd");
        dssStudent.setPassword("pwd");
        nssStudent.setDomSingleSchoolStudent(dssStudent);
        nssStudent.setDomSchoolClass(null);

        System.out.println("submitNewUser without a schoolclass");
        try {
            Boolean result = instance.submitSingleSchoolStudent(sc, rssStudent);
            assertFalse(result);
        } catch (Dwo2RestException ex) {
            assertEquals(Dwo2ExceptionCode.Rest_Registration_School_license_expired, ex.getDwo2Code());
        }

        PersistentUser user = UserManager.findByUserName(dssStudent.getUserName());
        assertNull(user);

        System.out.println("submitNewUser with a schoolclass");
        dssStudent.setUserName("testuser02");
        DomSchoolClass schoolClass = SchoolClassManager.findEntity(1L).buildDomSchoolClass();//SchoolClass01        
        nssStudent.setDomSchoolClass(schoolClass);

        try {
            Boolean result = instance.submitSingleSchoolStudent(sc, rssStudent);
            assertFalse(result);
        } catch (Dwo2RestException ex) {
          assertEquals(Dwo2ExceptionCode.Rest_Registration_School_license_expired, ex.getDwo2Code());
      }

        user = UserManager.findByUserName(dssStudent.getUserName());
        assertNull(user);
        
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

        dssStudent.setId(PersistentUser.buildPersistenceId(11L));
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
        dssStudent.setId(PersistentUser.buildPersistenceId(10L)); // By id, not by name!
        dssStudent.setGivenName("User");
        dssStudent.setFamilyName("Lastname 02");
        dssStudent.setPassword("bla");
        dssStudent.setEmail("blamail");
        expResult = false;
        try {
            result = instance.updateSingleSchoolStudent(sc, nssStudent);
            assertEquals(expResult, result);
        } catch (Dwo2RestException e) {
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
     * SecuredSchoolAdminSchoolManager. Tests if a teacher is removed from a
     * school and whether all the school related data is removed.
     */
    @Test
    public void testRemoveTeacherFromSchool() {
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        PersistentUser user = (PersistentUser) UserManager.findByUserName("user04");
        RestTeacher restTeacher = new RestTeacher();
        DomTeacher domTeacher = user.buildDomTeacher(null);
        restTeacher.setDomTeacher(domTeacher);
        try {
            Boolean expResult = null;
            Boolean result = instance.removeTeacherFromSchool(sc, restTeacher);
            assertEquals(true, result);
        } catch (Dwo2RestException e) {
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
            hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.TEACHER);
        } catch (Dwo2Exception ex) {
            fail("Teacher did not have a hasRole in the test database. He should.");
        }

        restTeacher.setDomTeacher(user.buildDomTeacher(null));
        Boolean result = instance.removeTeacherFromSchool(sc, restTeacher);
        assertEquals("Teacher was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user03");
        if (userResult == null) {
            fail("User was removed, while it should remain.");
        }
        try {
            HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.TEACHER);
            fail("HasRole was not removed.");
        } catch (Dwo2Exception ex) {
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
        RestStudent restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(user.buildDomStudent(null));
        try {
            Boolean result = instance.removeStudentFromSchool(sc, restStudent);
            assertEquals("SingleSchoolStudent was removed but should fail.", false, result);
        } catch (Dwo2RestException e) {
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
            hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            fail("Student did not have a hasRole in the test database. He should.");
        }

        restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(user.buildDomStudent(null));
        Boolean result = instance.removeStudentFromSchool(sc, restStudent);
        assertEquals("Student was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user02");
        if (userResult == null) {
            fail("User was removed,while it should remain.");
        }
        try {
            HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.STUDENT);
            fail("HasRole was not removed.");
        } catch (Dwo2Exception ex) {
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
     * SecuredSchoolAdminSchoolManager. Tests whether a schooladmin can be
     * removed from a known school all the school-related data is removed. it
     * also tests for removing StudentScoContext and StudentScoData. This is for
     * the transition from 1.0 to 2.0 as a schooladmin should not be able to
     * create it in Dwo 2.0.
     */
    @Test
    public void testRemoveSchoolAdminFromSchool() {
        System.out.println("removeSchoolAdminFromSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        PersistentUser user = (PersistentUser) UserManager.findByUserName("user04");
        RestSchoolAdmin restSchoolAdmin = new RestSchoolAdmin();
        restSchoolAdmin.setDomSchoolAdmin(user.buildDomSchoolAdmin(null));
        try {
            Boolean result = instance.removeSchoolAdminFromSchool(sc, restSchoolAdmin);
            assertEquals(true, result);
        } catch (Dwo2RestException e) {
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
            hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.SCHOOLADMIN);
        } catch (Dwo2Exception ex) {
            fail("SchoolAdmin did not have a hasRole in the test database. He should.");
        }

        restSchoolAdmin.setDomSchoolAdmin(user.buildDomSchoolAdmin(null));
        Boolean result = instance.removeSchoolAdminFromSchool(sc, restSchoolAdmin);
        assertEquals("SchoolAdmin was not removed.", true, result);
        PersistentUser userResult = (PersistentUser) UserManager.findByUserName("user06");
        if (userResult == null) {
            fail("User was removed, while it should remain.");
        }
        try {
            HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(user, (PersistentSchool) SchoolManager.findBySchoolLogin("school01"), RoleType.SCHOOLADMIN);
            fail("HasRole was not removed.");
        } catch (Dwo2Exception ex) {
            //success
        }

        // test for studentsco data
        List<PersistentStudentScoContext> scoc = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        assertEquals(0L, scoc.size());
    }
    
    
    @Test
    public void testGetStudentsInOrganisation() throws Exception {
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolManager instance = new SecuredSchoolAdminSchoolManager();
        RestSchoolOrganisation rest = new RestSchoolOrganisation();
        rest.setRestContext(new DomContext());
        DomHasRole dhr = HasRoleUtilManager.getCurrentHasRole("user06", RoleType.SCHOOLADMIN).buildDomHasRole();
		rest.getRestContext().setDomHasRole(dhr);
        rest.setDomSchoolOrganisation(new DomSchoolOrganisation());
        DomSchoolOrganisation result = instance.getStudentsInSchool(sc, rest);
        assertEquals(3, result.getUsers().size());
        assertEquals(6, result.getUsersOfClasses().size());
        
        DomSchoolOrganisation org = rest.getDomSchoolOrganisation();
        org.setSchoolClasses(result.getSchoolClasses());
        org.setUsers(null);
        org.setUsersOfClasses(null);
        org.setLimit(1L);
        org.setSkip(1L);
        result = instance.getStudentsInSchool(sc, rest);
        assertEquals(1, result.getUsers().size());
        assertEquals(2, result.getUsersOfClasses().size());

    }
}
