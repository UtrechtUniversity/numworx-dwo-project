package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
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
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestUriInfo;

public class PublicScoContextManagerIT {
	
    private static final Logger LOG = Logger.getLogger(PublicScoContextManagerIT.class.getName());

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

	PublicScoContextManager manager;
	@Before
	public void setUp() throws Exception {
        instance.IntializeTestDatabase();
		manager = new PublicScoContextManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testSco() throws Exception {
		RestScoContext rest = new RestScoContext();
		DomDwoProfile domDwoProfile = new DomDwoProfile();
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		domDwoProfile.setId(id);
		rest.setDomDwoProfile(domDwoProfile);
		
		DomScoContext domScoContext = new DomScoContext();
		PersistenceId sco = PersistentScoContext.buildPersistenceId(Long.valueOf(1));
		domScoContext.setId(sco);
		rest.setDomScoContext(domScoContext);
		DomContext restContext = new DomContext();
		rest.setRestContext(restContext);
		rest.setSchoolClassID(null);
		TestUriInfo info = new TestUriInfo();
		DomScoContext result = manager.get(rest, info);
		
		assertEquals(rest.getDomScoContext().getId(), result.getId());
		assertEquals(ScoType.OEFENEN_STRAFPUNTEN, result.getScoType());
		
	}
	
	@Test public void testScos() throws Exception {
		RestCourse rest = new RestCourse();
		rest.setRestContext(new DomContext());
		DomDwoProfile domDwoProfile = new DomDwoProfile();
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		domDwoProfile.setId(id);
		rest.setDomDwoProfile(domDwoProfile);
		DomCourse domCourse = new DomCourse();
		PersistenceId course = PersistentCourse.buildPersistenceId(Long.valueOf(5));
		domCourse.setId(course);
		rest.setDomCourse(domCourse);
		rest.setSchoolClassID(null);
		UriInfo info = new TestUriInfo();
		List<DomScoContext> result = manager.getScos(rest, info);
		assertEquals(1, result.size());
		assertEquals(ScoType.OEFENEN_STRAFPUNTEN, result.get(0).getScoType());
	}
	
}
