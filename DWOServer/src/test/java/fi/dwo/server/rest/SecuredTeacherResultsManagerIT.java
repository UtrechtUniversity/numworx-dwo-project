/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.DomMappedResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredTeacherResultsManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName());

    static DatabaseManager dbInstance = null;

    public SecuredTeacherResultsManagerIT() {
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
     * Test of getTeachersSchoolClasses method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersResults() {
        System.out.println("testGetTeachersResults");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
        RestDwoProfile restProfile = new RestDwoProfile();
        DomDwoProfile domProfile;
        
        PersistentDwoProfile pProfile = new PersistentDwoProfile();
        pProfile.setDwoProfileID(1L);
        pProfile.setDwoProfileName("testprofile01");
        pProfile.setDwoProfileRights("_");
        pProfile.setDwoProfileDescription("Test dwoProfileDescription");
        pProfile.setDwoProfileText("Test dwoProfileText01");
        domProfile = pProfile.buildDomDwoProfileFull();
        
        DomContext restContext = new DomContext();
        restProfile.setRestContext(restContext);
        restProfile.setDomDwoProfile(domProfile);
        
        DomHasRole domHasRole = null;
        PersistentUser pUser = UserManager.findByUserName("user07");
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

        try {
            PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
            domHasRole = pHasRole.buildDomHasRole();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find teacher's hasRole");
        }
        restContext.setDomHasRole(domHasRole);
        
        DomResultsPerTeacher result = instance.getTeachersResults(sc,restProfile);
        DomMappedResultsPerTeacher mapResult = new DomMappedResultsPerTeacher(result);
        
        assertEquals(result.getTeacher().getId().getIdString(),"MYSQL;PersistentUser;00000000000000000014");
        //requires  students with id 9,10 and 11 in class with id
        assertEquals(3, mapResult.getStudentsOfClasses().size());
        assertEquals(true, mapResult.getStudentsOfClasses().containsKey(new PersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000009;00000000000000000002;00000000000000000002")));
        assertEquals(true, mapResult.getStudentsOfClasses().containsKey(new PersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000010;00000000000000000002;00000000000000000002")));
        assertEquals(true, mapResult.getStudentsOfClasses().containsKey(new PersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000011;00000000000000000002;00000000000000000002")));
        assertEquals(3, mapResult.getStudents().size());
        assertEquals(true, mapResult.getStudents().containsKey(new PersistenceId("MYSQL;PersistentUser;00000000000000000009")));
        assertEquals(true, mapResult.getStudents().containsKey(new PersistenceId("MYSQL;PersistentUser;00000000000000000010")));
        assertEquals(true, mapResult.getStudents().containsKey(new PersistenceId("MYSQL;PersistentUser;00000000000000000011")));
        //requires schoolclass with id 2
        assertEquals(1, mapResult.getSchoolClasses().size());
        assertEquals(true, mapResult.getSchoolClasses().containsKey(new PersistenceId("MYSQL;PersistentSchoolClass;00000000000000000002")));
        //requires courses with id 2,5,6
        assertEquals(2, mapResult.getCourses().size());
//        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000004")));
        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000005")));
        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000006")));
//        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000002")));
        //requires classCourses with id 4,5,6
        assertEquals(2, mapResult.getClassCourses().size());
//        assertEquals(true, mapResult.getClassCourses().containsKey(new PersistenceId("MYSQL;PersistentClassCourse;00000000000000000004")));
        assertEquals(true, mapResult.getClassCourses().containsKey(new PersistenceId("MYSQL;PersistentClassCourse;00000000000000000005")));
        assertEquals(true, mapResult.getClassCourses().containsKey(new PersistenceId("MYSQL;PersistentClassCourse;00000000000000000006")));
        //requires scoContexts with id 2,1
        assertEquals(2, mapResult.getScoContexts().size());
        assertEquals(true, mapResult.getScoContexts().containsKey(new PersistenceId("MYSQL;PersistentScoContext;00000000000000000001")));
        assertEquals(true, mapResult.getScoContexts().containsKey(new PersistenceId("MYSQL;PersistentScoContext;00000000000000000002")));
        //requires studentScoContexts with id 1,2
        assertEquals(2, mapResult.getStudentScoContexts().size());
        assertEquals(true, mapResult.getStudentScoContexts().containsKey(new PersistenceId("MYSQL;PersistentStudentScoContext;00000000000000000001")));
        assertEquals(true, mapResult.getStudentScoContexts().containsKey(new PersistenceId("MYSQL;PersistentStudentScoContext;00000000000000000002")));
    }
    
    /**
     * Test of getTeachersSchoolClasses method, of class
     * SecuredTeacherSchoolClassManager.
     * @throws Dwo2Exception 
     */
    @Test
    public void testSelectedTeachersResults() throws Dwo2Exception {
        System.out.println("testSelectedTeachersResults");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
        RestResultsPerTeacher restProfile = new RestResultsPerTeacher();
        DomDwoProfile domProfile;
        
        PersistentDwoProfile pProfile = new PersistentDwoProfile();
        pProfile.setDwoProfileID(1L);
        pProfile.setDwoProfileName("testprofile01");
        pProfile.setDwoProfileRights("_");
        pProfile.setDwoProfileDescription("Test dwoProfileDescription");
        pProfile.setDwoProfileText("Test dwoProfileText01");
        domProfile = pProfile.buildDomDwoProfileFull();
        
        DomContext restContext = new DomContext();
        restProfile.setRestContext(restContext);
        restProfile.setDomDwoProfile(domProfile);
        
        DomHasRole domHasRole = null;
        PersistentUser pUser = UserManager.findByUserName("user07");
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

        try {
            PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
            domHasRole = pHasRole.buildDomHasRole();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find teacher's hasRole");
        }
        restContext.setDomHasRole(domHasRole);
        
        DomResultsPerTeacher input = new DomResultsPerTeacher();
        input.setSchoolClasses(Collections.singletonList(new DomMapEntry<>(new PersistenceId("MYSQL;PersistentSchoolClass;00000000000000000002"),null)));
        input.setCourses(Collections.singletonList(new DomMapEntry<>(new PersistenceId("MYSQL;PersistentCourse;00000000000000000006"),null)));
        restProfile.setDomResultsPerTeacher(input);
        
        DomResultsPerTeacher result = instance.selectedTeachersResults(sc,restProfile);
        DomMappedResultsPerTeacher mapResult = new DomMappedResultsPerTeacher(result);
        
        assertEquals(result.getTeacher().getId().getIdString(),"MYSQL;PersistentUser;00000000000000000014");
        //requires  students with id 9,10 and 11 in class with id
        assertEquals(3, mapResult.getStudentsOfClasses().size());
        assertEquals(true, mapResult.getStudentsOfClasses().containsKey(new PersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000009;00000000000000000002;00000000000000000002")));
        assertEquals(true, mapResult.getStudentsOfClasses().containsKey(new PersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000010;00000000000000000002;00000000000000000002")));
        assertEquals(true, mapResult.getStudentsOfClasses().containsKey(new PersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000011;00000000000000000002;00000000000000000002")));
        assertEquals(3, mapResult.getStudents().size());
        assertEquals(true, mapResult.getStudents().containsKey(new PersistenceId("MYSQL;PersistentUser;00000000000000000009")));
        assertEquals(true, mapResult.getStudents().containsKey(new PersistenceId("MYSQL;PersistentUser;00000000000000000010")));
        assertEquals(true, mapResult.getStudents().containsKey(new PersistenceId("MYSQL;PersistentUser;00000000000000000011")));
        //requires schoolclass with id 2
        assertEquals(1, mapResult.getSchoolClasses().size());
        assertEquals(true, mapResult.getSchoolClasses().containsKey(new PersistenceId("MYSQL;PersistentSchoolClass;00000000000000000002")));
        //requires courses with id 2,5,6
        assertEquals(1, mapResult.getCourses().size());
//        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000004")));
 //       assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000005")));
        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000006")));
//        assertEquals(true, mapResult.getCourses().containsKey(new PersistenceId("MYSQL;PersistentCourse;00000000000000000002")));
        //requires classCourses with id 4,5,6
        assertEquals(1, mapResult.getClassCourses().size());
//        assertEquals(true, mapResult.getClassCourses().containsKey(new PersistenceId("MYSQL;PersistentClassCourse;00000000000000000004")));
//        assertEquals(true, mapResult.getClassCourses().containsKey(new PersistenceId("MYSQL;PersistentClassCourse;00000000000000000005")));
        assertEquals(true, mapResult.getClassCourses().containsKey(new PersistenceId("MYSQL;PersistentClassCourse;00000000000000000006")));
        //requires scoContexts with id 2,1
        assertEquals(1, mapResult.getScoContexts().size());
//        assertEquals(true, mapResult.getScoContexts().containsKey(new PersistenceId("MYSQL;PersistentScoContext;00000000000000000001")));
        assertEquals(true, mapResult.getScoContexts().containsKey(new PersistenceId("MYSQL;PersistentScoContext;00000000000000000002")));
        //requires studentScoContexts with id 1,2
        assertEquals(1, mapResult.getStudentScoContexts().size());
  //      assertEquals(true, mapResult.getStudentScoContexts().containsKey(new PersistenceId("MYSQL;PersistentStudentScoContext;00000000000000000001")));
        assertEquals(true, mapResult.getStudentScoContexts().containsKey(new PersistenceId("MYSQL;PersistentStudentScoContext;00000000000000000002")));
    }
 
    @Test public void testGetTeachersResultsV2() throws Exception {
        System.out.println("testSelectedTeachersResults V2");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
        RestResultsPerTeacherv2 rest = new RestResultsPerTeacherv2();
        DomDwoProfileId domProfile;
        
        PersistentDwoProfile pProfile = new PersistentDwoProfile();
        pProfile.setDwoProfileID(1L);
        pProfile.setDwoProfileName("testprofile01");
        pProfile.setDwoProfileRights("_");
        pProfile.setDwoProfileDescription("Test dwoProfileDescription");
        pProfile.setDwoProfileText("Test dwoProfileText01");
        domProfile = pProfile.buildDomDwoProfile();
        
        DomContext restContext = new DomContext();
        rest.setRestContext(restContext);
        rest.setDomDwoProfile(domProfile);
        
        DomHasRole domHasRole = null;
        PersistentUser pUser = UserManager.findByUserName("user07");
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

        try {
            PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
            domHasRole = pHasRole.buildDomHasRole();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find teacher's hasRole");
        }
        restContext.setDomHasRole(domHasRole);
        
        DomResultsPerTeacherv2 input = new DomResultsPerTeacherv2();
        rest.setDomResultsPerTeacher(input);
        
        DomResultsPerTeacherv2 result = instance.selectedTeachersResults(sc,rest);
        assertNotNull(result);
        assertEquals(1, result.getSchoolClasses().size());
        assertEquals(2, result.getCourses().size());
        assertEquals(2, result.getClassCourses().size());

// second half
        rest.setDomResultsPerTeacher(result);
        result = instance.selectedTeachersResults(sc, rest);
        
        assertNotNull(result.getStudents());
        assertNotNull(result.getStudentsOfClasses());
        
        assertNotNull(result.getScoContexts());
        assertNotNull(result.getStudentScoContexts());
        assertNotNull(result.getStudentScoPages()); // empty?
        
        
    }
    
    
    @Test
    public void testClearStudentResults() throws Exception {
      
      PersistentClassCourse scc = ClassCourseManager.findEntity(5L);
      scc.setViewState(ViewState.invisible);
      ClassCourseManager.edit(scc);
      
      SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
      SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
      
      RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
      DomContext restContext = new DomContext();
      DomHasRole domHasRole;
      PersistentUser pUser = UserManager.findByUserName("user07");
      PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
      PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
      domHasRole = pHasRole.buildDomHasRole();
      restContext.setDomHasRole(domHasRole);      
      rest.setRestContext(restContext);
      
      DomClearStudentDataForScoAndClass dom = new DomClearStudentDataForScoAndClass();
      DomDwoProfile domProfile = DwoProfileManager.findEntity(1L).buildDomDwoProfile();
      dom.setDomProfile(domProfile);
      DomSchoolClass domSchoolClass = SchoolClassManager.findEntity(2L).buildDomSchoolClass();
      dom.setDomSchoolClass(domSchoolClass);
      DomScoContext domScoContext = ScoContextManager.findEntity(1L).buildDomScoContext();
      dom.setDomScoContext(domScoContext);
      rest.setClearStudentDataForScoAndClass(dom);
      Boolean result = instance.clearStudentResults(sc, rest);
      
      assertEquals("schoolclasscourse not wiped", Boolean.TRUE, result);
    }
    
    @Test
    public void testCreateStudentResults() throws Exception {
            
      SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
      SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
      
      RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
      DomContext restContext = new DomContext();
      DomHasRole domHasRole;
      PersistentUser pUser = UserManager.findByUserName("user07");
      PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
      PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
      domHasRole = pHasRole.buildDomHasRole();
      restContext.setDomHasRole(domHasRole);      
      rest.setRestContext(restContext);
      
      DomClearStudentDataForScoAndClass dom = new DomClearStudentDataForScoAndClass();
      DomDwoProfile domProfile = DwoProfileManager.findEntity(1L).buildDomDwoProfile();
      dom.setDomProfile(domProfile);
      DomSchoolClass domSchoolClass = SchoolClassManager.findEntity(2L).buildDomSchoolClass();
      dom.setDomSchoolClass(domSchoolClass);
      DomScoContext domScoContext = ScoContextManager.findEntity(2L).buildDomScoContext();
      dom.setDomScoContext(domScoContext);
      
      dom.setDomStudentList(Collections.emptyList());
      rest.setClearStudentDataForScoAndClass(dom);
      DomResultsPerTeacher result = instance.createStudentResults(sc, rest);
      
      assertNotNull("schoolclasscourse not wiped",result);
  // test without classcourse    
      ClassCourseManager.destroy(6L);
      result = instance.createStudentResults(sc, rest);
      assertNotNull("schoolclasscourse wiped",result.getClassCourses());
   
    }

}
