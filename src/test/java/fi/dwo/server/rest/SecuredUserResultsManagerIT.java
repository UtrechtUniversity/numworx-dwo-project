package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredUserResultsManagerIT {

	private static DatabaseManager dbInstance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbInstance = new DatabaseManager();
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        dbInstance = null;
        DwoEmfFactory.setDefaultEntityManagerFactory();
	}

	@Before
	public void setUp() throws Exception {
        dbInstance.IntializeTestDatabase();
	}

	@After
	public void tearDown() throws Exception {
        dbInstance.ClearDatabase();
	}

	@Test
	public void testGetCourseResults() throws Exception {
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        DomDwoProfile domProfile;
        
        PersistentDwoProfile pProfile = new PersistentDwoProfile();
        pProfile.setDwoProfileID(1L);
        pProfile.setDwoProfileName("testprofile01");
        pProfile.setDwoProfileRights("_");
        pProfile.setDwoProfileDescription("Test dwoProfileDescription");
        pProfile.setDwoProfileText("Test dwoProfileText01");
        domProfile = pProfile.buildDomDwoProfileFull();
        
        DomContext restContext = new DomContext();
        DomHasRole domHasRole = null;
        PersistentUser pUser = UserManager.findByUserName("user02");
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

        try {
            PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.STUDENT);
            domHasRole = pHasRole.buildDomHasRole();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find student hasRole");
        }
        restContext.setDomHasRole(domHasRole);
        
        RestCourse rest = new RestCourse();
        rest.setDomDwoProfile(domProfile);
        rest.setRestContext(restContext);
        
        PersistentCourse course = CourseManager.findEntity(5L); 
        rest.setDomCourse(course.buildDomCourse());
        
        SecuredUserResultsManager manager = new SecuredUserResultsManager();
        DomResultsPerStudentCourse result = manager.getCourseResults(sc, rest);
        assertEquals(1, result.getStudentScoContexts().size());
	}

}
