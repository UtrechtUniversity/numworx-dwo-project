package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.SecurityContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestCourseFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredTeacherCourseManagerIT {

  private static DatabaseManager instance = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
      Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
      DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
      instance = new DatabaseManager();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
      DwoEmfFactory.setDefaultEntityManagerFactory();
      instance = null;
  }

  SecuredTeacherCourseManager manager;
  @Before
  public void setUp() throws Exception {
      instance.IntializeTestDatabase();
      manager = new SecuredTeacherCourseManager();
  }

  @Test
  public void testRemove() throws Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
    PersistentCourse  course = CourseManager.findEntity(1L);
    PersistentCourse  course1 = course;
    List<?> list = ClassCourseManager.findEntities(course);
    assertFalse(list.isEmpty());
    PersistentDwoProfile profile = DwoProfileManager.findEntity(course.getDwoProfileID());
    DomHasRole domHasRole = pHasRole.buildDomHasRole();
    DomCourse  domCourse = course.buildDomCourse();
    RestCourse rest = new RestCourse();
    rest.setDomCourse(domCourse);
    DomDwoProfile domDwoProfile = profile.buildDomDwoProfile();
    rest.setDomDwoProfile(domDwoProfile);
    DomContext restContext = new DomContext();
    restContext.setDomHasRole(domHasRole);
    rest.setRestContext(restContext);
    Boolean result = manager.remove(sc, rest);
    assertTrue("remove", result.booleanValue());
    
    course = CourseManager.findEntity(1L);
    assertNull("removed", course);
    list = ClassCourseManager.findEntities(course1);
    assertTrue(list.isEmpty()); }

  /**
   * Should fail! no updates in nullschool for normal teacher!
   * @throws Exception
   */
  @Test
  public void testUpdate() throws Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
    PersistentCourse  course = CourseManager.findEntity(1L);
    
    RestCourseFull rest = new RestCourseFull();
    DomCourseFull domCourse = course.buildDomCourseFull();   
    final String newName = "XXXXXXXXXX";
    domCourse.setName(newName);
    rest.setDomCourse(domCourse);
    DomContext restContext = new DomContext();
    DomHasRole domHasRole = pHasRole.buildDomHasRole();
    restContext.setDomHasRole(domHasRole);
    rest.setRestContext(restContext);
    DomCourseFull result = manager.update(sc, rest);
    assertEquals("update", newName, result.getName());
    
   }

  @Test @Ignore
  public void testUpdateDuplicateInNullSchool() throws Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
    PersistentCourse  course = CourseManager.findEntity(1L);
    
    RestCourseFull rest = new RestCourseFull();
    DomCourseFull domCourse = course.buildDomCourseFull();   
    rest.setDomCourse(domCourse);
    DomContext restContext = new DomContext();
    DomHasRole domHasRole = pHasRole.buildDomHasRole();
    restContext.setDomHasRole(domHasRole);
    rest.setRestContext(restContext);
    
    final String usedName = "course02";
    domCourse.setName(usedName);
    rest.setDomCourse(domCourse);
    try {
      DomCourseFull result = manager.update(sc, rest);
      fail("update returns " + result);
    } catch (Dwo2Exception e) {
      assertEquals("usedName update", Dwo2ExceptionCode.Rest_CourseNameExists, e.getDwo2Code());
    } catch (Dwo2RestException e) {
      assertEquals("usedName update", Dwo2ExceptionCode.Rest_CourseNameExists, e.getDwo2Code());
    }
  }

  @Test
  public void testAdd() throws Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);

    RestCourseFull rest = new RestCourseFull();
    DomCourseFull domCourse = new DomCourseFull();
    domCourse.setDescription("description");
    domCourse.setDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L));
    domCourse.setExport(Boolean.FALSE);
    domCourse.setName("school add");
    domCourse.setNotVisible(false);
    domCourse.setParentID(PersistentCourse.buildPersistenceId(0L));
    domCourse.setSchoolId(pSchool.buildPersistenceId());
    domCourse.setSequenceNr(0L);
    domCourse.setWithChildren(Boolean.FALSE);
    rest.setDomCourse(domCourse);
    DomContext restContext = new DomContext(); restContext.setDomHasRole(pHasRole.buildDomHasRole());
    rest.setRestContext(restContext);
    DomCourseFull result = manager.add(sc, rest);
    assertNotNull("add", result.getId());
    assertEquals("add", domCourse.getName(), result.getName());
    
    try { 
      result = manager.add(sc, rest);
      fail("add should fail " + result);
    } catch(Dwo2Exception e) {
      assertEquals("add duplicate", Dwo2ExceptionCode.Rest_CourseNameExists, e.getDwo2Code());
    } catch(Dwo2RestException e) {
      assertEquals("add duplicate", Dwo2ExceptionCode.Rest_CourseNameExists, e.getDwo2Code());
    }
  }

  
  @Test
  public void testTooLong() throws Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);

    RestCourseFull rest = new RestCourseFull();
    DomCourseFull domCourse = new DomCourseFull();
    domCourse.setDescription("description");
    domCourse.setDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L));
    domCourse.setExport(Boolean.FALSE);
    domCourse.setName("school add 1234567890123456789012345678901234567890");
    domCourse.setNotVisible(false);
    domCourse.setParentID(PersistentCourse.buildPersistenceId(0L));
    domCourse.setSchoolId(pSchool.buildPersistenceId());
    domCourse.setSequenceNr(0L);
    domCourse.setWithChildren(Boolean.FALSE);
    rest.setDomCourse(domCourse);
    DomContext restContext = new DomContext(); restContext.setDomHasRole(pHasRole.buildDomHasRole());
    rest.setRestContext(restContext);
    DomCourseFull result;    
    try { 
      result = manager.add(sc, rest);
      fail("add should fail " + result);
    } catch(Dwo2Exception e) {
      assertEquals("add too long", Dwo2ExceptionCode.Rest_NameTooLong, e.getDwo2Code());
    } catch(Dwo2RestException e) {
      assertEquals("add too long", Dwo2ExceptionCode.Rest_NameTooLong, e.getDwo2Code());
    }
  }

  @Test
  public void testUpdateTooLong() throws Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
    PersistentCourse  course = CourseManager.findEntity(1L);
    
    RestCourseFull rest = new RestCourseFull();
    DomCourseFull domCourse = course.buildDomCourseFull();   
    final String newName = "XXXXXXXXXX 1234567890123456789012345678901234567890";
    domCourse.setName(newName);
    rest.setDomCourse(domCourse);
    DomContext restContext = new DomContext();
    DomHasRole domHasRole = pHasRole.buildDomHasRole();
    restContext.setDomHasRole(domHasRole);
    rest.setRestContext(restContext);
    try {
		DomCourseFull result = manager.update(sc, rest);
		fail("update to long should fail");
	} catch (Dwo2Exception e) {
		assertEquals("update1", Dwo2ExceptionCode.Rest_NameTooLong, e.getDwo2Code());
	} catch (Dwo2RestException e) {
		assertEquals("update2", Dwo2ExceptionCode.Rest_NameTooLong, e.getDwo2Code());		
	}
    
   }

  
}
