package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class PublicCourseManagerIT {
	
    private static final Logger LOG = Logger.getLogger(PublicCourseManagerIT.class.getName());

    static DatabaseManager instance = null;

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

	PublicCourseManager manager;
	@Before
	public void setUp() throws Exception {
        instance.IntializeTestDatabase();
		manager = new PublicCourseManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testPublicProfile1() {
		RestDwoProfile rest = new RestDwoProfile();
		rest.setDomDwoProfile(new DomDwoProfile());
		
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		rest.getDomDwoProfile().setId(id);
		List result = manager.getCourses(rest);
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	public void testLimitedProfile3() {
		RestDwoProfile rest = new RestDwoProfile();
		rest.setDomDwoProfile(new DomDwoProfile());
		
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(3));
		rest.getDomDwoProfile().setId(id);
		List result = manager.getCourses(rest);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetCourse01() {
		RestCourse rest = new RestCourse();
		DomCourse  course = new DomCourse();
		PersistenceId id = PersistentCourse.buildPersistenceId(Long.valueOf(1));
		PersistenceId profileID = PersistentDwoProfile.buildPersistenceId(1L);
		course.setId(id);
		rest.setDomCourse(course);
		DomDwoProfile profile = new DomDwoProfile();
		profile.setId(profileID);
		rest.setDomDwoProfile(profile);
		
		DomCourseStudent result = manager.getCourse(rest);
		
		assertEquals("course01", result.getName());
		
		
	}
	@Test
	public void testGetChildren01() {
		RestCourse rest = new RestCourse();
		DomCourse  course = new DomCourse();
		PersistenceId id = PersistentCourse.buildPersistenceId(Long.valueOf(1));
		PersistenceId profileID = PersistentDwoProfile.buildPersistenceId(1L);
		course.setId(id);
		rest.setDomCourse(course);
		DomDwoProfile profile = new DomDwoProfile();
		profile.setId(profileID);
		rest.setDomDwoProfile(profile);	
		List<DomCourseStudent> result = manager.getCourses(rest);
		assertFalse(result.isEmpty());
	
		
		
	}
	
}
