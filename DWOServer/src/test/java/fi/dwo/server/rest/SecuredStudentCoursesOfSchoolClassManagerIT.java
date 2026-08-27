package fi.dwo.server.rest;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredStudentCoursesOfSchoolClassManagerIT {
    static DatabaseManager dbInstance = null;
    {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbInstance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        dbInstance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
	}

	SecuredStudentCoursesOfSchoolClassManager manager;

	@Before
	public void setUp() throws Exception {
        dbInstance.IntializeTestDatabase();
        manager = new SecuredStudentCoursesOfSchoolClassManager();
	}

	@After
	public void tearDown() throws Exception {
        dbInstance.ClearDatabase();
	}

	@Test @Ignore
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testIllegalGetCourse() throws Exception {
	       SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
			RestCourse rest = new RestCourse();
			DomCourse domCourse = new DomCourse();
			domCourse.setId(PersistentCourse.buildPersistenceId(12L));
			rest.setDomCourse(domCourse);
			DomDwoProfile profile = new DomDwoProfile();
			profile.setId(PersistentDwoProfile.buildPersistenceId(1L));
			rest.setDomDwoProfile(profile);
			DomSchoolClassId schoolclass = new DomSchoolClassId();
			schoolclass.setId(PersistentSchoolClass.buildPersistenceId(1L));
			rest.setSchoolClassID(schoolclass);
			DomContext context = new DomContext();
			rest.setRestContext(context);
	        DomHasRole domHasRole;
	        domHasRole = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT).buildDomHasRole();
	        rest.getRestContext().setDomHasRole(domHasRole);
			HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
			StringBuffer sb = new StringBuffer();
			sb.append("https://localhost:8080/dwo/rest/secure/student/enz");
			Mockito.when(mock.getRequestURL()).thenReturn(sb);
			DomCoursesOfSchoolClass result = manager.getCourse(sc, rest, mock);
		
		
	}
	
	
	@Test
	public void testGetCourse() throws Exception {
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
		RestCourse rest = new RestCourse();
		DomCourse domCourse = new DomCourse();
		domCourse.setId(PersistentCourse.buildPersistenceId(1L));
		rest.setDomCourse(domCourse);
		DomDwoProfile profile = new DomDwoProfile();
		profile.setId(PersistentDwoProfile.buildPersistenceId(1L));
		rest.setDomDwoProfile(profile);
		DomSchoolClassId schoolclass = new DomSchoolClassId();
		schoolclass.setId(PersistentSchoolClass.buildPersistenceId(1L));
		rest.setSchoolClassID(schoolclass);
		DomContext context = new DomContext();
		rest.setRestContext(context);
        DomHasRole domHasRole;
        domHasRole = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT).buildDomHasRole();
        rest.getRestContext().setDomHasRole(domHasRole);
		DomCoursesOfSchoolClass result = manager.getCourse(sc, rest, null);
	
	}

	@Test @Ignore
	public void testGetScoContext() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClassCourse() throws Exception {
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
		RestClassCourse rest = new RestClassCourse();
		DomClassCourse domCourse = new DomClassCourse();
		domCourse.setId(PersistentClassCourse.buildPersistenceId(1L));
		rest.setDomClassCourse(domCourse);
		DomDwoProfile profile = new DomDwoProfile();
		profile.setId(PersistentDwoProfile.buildPersistenceId(1L));
		rest.setDomDwoProfile(profile);
		DomContext context = new DomContext();
		rest.setRestContext(context);
        DomHasRole domHasRole;
        domHasRole = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT).buildDomHasRole();
        rest.getRestContext().setDomHasRole(domHasRole);
		DomCoursesOfSchoolClass result = manager.getClassCourse(sc, rest, null);
	}

}
