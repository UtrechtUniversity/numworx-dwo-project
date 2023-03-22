package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import fi.dwo.server.testutil.TestUriInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredTeacherScoContextManagerIT {

	private static final Logger LOG = Logger.getLogger(SecuredTeacherScoContextManagerIT.class.getName());

    static DatabaseManager dbInstance = null;
    
    SecuredTeacherScoContextManager manager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbInstance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        dbInstance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
	}

	@Before
	public void setUp() throws Exception {
        dbInstance.IntializeTestDatabase();
        manager = new SecuredTeacherScoContextManager();
	}

	@After
	public void tearDown() throws Exception {
        dbInstance.ClearDatabase();
	}

	@Test
	public void testUpdate() throws Exception {
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        PersistentUser user = UserManager.findByUserName("user07");
        List<PersistentHasRole> list = HasRoleManager.findEntities(user);
        DomHasRole dhr = list.get(0).buildDomHasRole();
        
        RestScoContextFull rest = new RestScoContextFull();
		DomDwoProfile domDwoProfile = new DomDwoProfile();
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		domDwoProfile.setId(id);
		rest.setDomDwoProfile(domDwoProfile);
		
		DomScoContextFull domScoContext = new DomScoContextFull();
		PersistenceId sco = PersistentScoContext.buildPersistenceId(Long.valueOf(1));
		domScoContext.setId(sco);
		
		
		rest.setDomScoContext(domScoContext);
		DomContext restContext = new DomContext();
		restContext.setDomHasRole(dhr);
		rest.setRestContext(restContext);
		TestUriInfo info = new TestUriInfo();
		DomScoContextFull result = manager.update(sc, rest);
		assertEquals("Optellen en aftrekken", result.getScoName());
		domScoContext.setDescription("more description");
		domScoContext.setImageData(new byte[123]);
		result = manager.update(sc, rest);
		assertNull(result.getImageData());
	}

	@Test
	public void testAdd() {
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        PersistentUser user = UserManager.findByUserName("user07");
        List<PersistentHasRole> list = HasRoleManager.findEntities(user);
        DomHasRole dhr = list.get(0).buildDomHasRole();
		RestScoContextFull rest = new RestScoContextFull();
		DomDwoProfile domDwoProfile = new DomDwoProfile();
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		domDwoProfile.setId(id);
		rest.setDomDwoProfile(domDwoProfile);
		PersistenceId Cid = PersistentCourse.buildPersistenceId(Long.valueOf(1));
		PersistenceId Aid = PersistentApplet.buildPersistenceId(Long.valueOf(17));
		
		DomScoContextFull domScoContext = new DomScoContextFull();
		domScoContext.setCourseId(Cid);
		domScoContext.setScoName("test02");
		domScoContext.setAppletId(Aid);
		rest.setDomScoContext(domScoContext);
		DomContext restContext = new DomContext();
		restContext.setDomHasRole(dhr);
		rest.setRestContext(restContext);
		TestUriInfo info = new TestUriInfo();
		DomScoContextFull result = manager.add(sc, rest);
		assertNotNull(result.getId());
		assertEquals("test02", result.getScoName());
		domScoContext.setScoName("test04");
		domScoContext.setDescription("more description");
		domScoContext.setImageData(new byte[123]);
		result = manager.add(sc, rest);
		assertNull(result.getImageData());
		assertNotNull(result.getId());
		domScoContext.setScoName("test07");
		domScoContext.setUrnId(result.getId());
		result = manager.add(sc,  rest);	
	}

	@Test public void testDuplicate() {
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        PersistentUser user = UserManager.findByUserName("user07");
        List<PersistentHasRole> list = HasRoleManager.findEntities(user);
        DomHasRole dhr = list.get(0).buildDomHasRole();

        RestScoContextFull rest = new RestScoContextFull();
		DomDwoProfile domDwoProfile = new DomDwoProfile();
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		domDwoProfile.setId(id);
		rest.setDomDwoProfile(domDwoProfile);
		PersistenceId Cid = PersistentCourse.buildPersistenceId(Long.valueOf(1));
		PersistenceId Aid = PersistentApplet.buildPersistenceId(Long.valueOf(17));
		
		DomScoContextFull domScoContext = new DomScoContextFull();
		domScoContext.setCourseId(Cid);
		domScoContext.setScoName("test02");
		domScoContext.setAppletId(Aid);
		rest.setDomScoContext(domScoContext);
		DomContext restContext = new DomContext();
		restContext.setDomHasRole(dhr);
		rest.setRestContext(restContext);
		TestUriInfo info = new TestUriInfo();
		DomScoContextFull result = manager.add(sc, rest);
		assertNotNull(result.getId());
		assertEquals("test02", result.getScoName());
		domScoContext.setScoName("test02");
		domScoContext.setDescription("more description");
		domScoContext.setImageData(new byte[123]);
		try { 
			result = manager.add(sc, rest);
			fail("should fail");
		} catch(Dwo2RestException e) {
			assertEquals(Dwo2ExceptionCode.Rest_ScoNameExists, e.getDwo2Code());
		}
		assertNotNull(result.getId());
		domScoContext.setScoName("test07");
		domScoContext.setUrnId(result.getId());
		result = manager.add(sc,  rest);
		
		domScoContext.setId(result.getId());
		domScoContext.setScoName("test02");
		try { 
			result = manager.update(sc, rest);
			fail("should fail");
		} catch (Dwo2RestException e) {
			assertEquals(Dwo2ExceptionCode.Rest_ScoNameExists, e.getDwo2Code());
		} catch (Dwo2Exception e) {
          assertEquals(Dwo2ExceptionCode.Rest_ScoNameExists, e.getDwo2Code());
		}
	}
	
	@Test public void testCountStudents() throws Exception {
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        PersistentUser user = UserManager.findByUserName("user07");
        List<PersistentHasRole> list = HasRoleManager.findEntities(user);
        DomHasRole dhr = list.get(0).buildDomHasRole();
        
        RestScoContext rest = new RestScoContext();
		DomDwoProfile domDwoProfile = new DomDwoProfile();
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		domDwoProfile.setId(id);
		rest.setDomDwoProfile(domDwoProfile);
		
		DomScoContextFull domScoContext = new DomScoContextFull();
		PersistenceId sco = PersistentScoContext.buildPersistenceId(Long.valueOf(1));
		domScoContext.setId(sco);
		
		rest.setDomScoContext(domScoContext);
		DomContext restContext = new DomContext();
		restContext.setDomHasRole(dhr);
		rest.setRestContext(restContext);
		Integer result = manager.countStudents(sc, rest);
		assertEquals(1, result.intValue());	
	}
}
