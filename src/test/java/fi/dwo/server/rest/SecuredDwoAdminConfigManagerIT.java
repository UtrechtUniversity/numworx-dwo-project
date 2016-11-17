package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestAppletConfig;
import nl.uu.fi.dwo.rest.entities.RestDwoProfileFull;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.AppletConfigManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.RoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;

public class SecuredDwoAdminConfigManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminConfigManagerIT.class.getName());

    static DatabaseManager instance = null;
    SecuredDwoAdminConfigManager manager;

	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        DwoEmfFactory.setDefaultEntityManagerFactory();
	}

	@Before
	public void setUp() throws Exception {
        instance.IntializeTestDatabase();

        PersistentAppletConfig profile = new PersistentAppletConfig();
        profile.setAppletID(17);
        profile.setLanguage("nl");
        profile.setName("Template");
        profile.setLaunchdata("");
        AppletConfigManager.create(profile);
        
        manager = new SecuredDwoAdminConfigManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testGetConfigurations() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		List<DomAppletConfig> list = manager.getConfigurations(sc);
		assertEquals("getProfiles listsize", 1, list.size());
	}

	@Test
	public void testSubmitAppletConfig() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestAppletConfig restDwoProfile;
		DomAppletConfig  profile;
		profile = new DomAppletConfig();
		profile.setAppletID(17);
		profile.setName("name");
		profile.setLanguage("la");
		profile.setLaunchdata("launchdata");
		restDwoProfile = new RestAppletConfig();
		restDwoProfile.setRestContext(new DomContext());
		restDwoProfile.setDomAppletConfig(profile);
		Boolean result = manager.submitAppletConfig(sc, restDwoProfile);
		assertTrue("submit profile", result.booleanValue());
		List<DomAppletConfig> list = manager.getConfigurations(sc);
		assertEquals("getProfiles listsize", 2, list.size());
		DomAppletConfig other = list.get(1);
		assertEquals("get from database", profile.getAppletID(), other.getAppletID());
		assertEquals("get from database", profile.getLanguage(), other.getLanguage());
		assertEquals("get from database", profile.getLaunchdata(), other.getLaunchdata());
		assertEquals("get from database", profile.getName(), other.getName());
		assertNotNull("get from database", other.getId().getIdString());
	}

	@Test
	public void testUpdateAppletConfig() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestAppletConfig restConfig;
		DomAppletConfig  config;
		config = new DomAppletConfig();
		config.setId( manager.getConfigurations(sc).get(0).getId());
		config.setAppletID(1);
		config.setName("other name");
		config.setLanguage("ot");
		config.setLaunchdata("other data");
		restConfig = new RestAppletConfig();
		restConfig.setRestContext(new DomContext());
		restConfig.setDomAppletConfig(config);
		Boolean result = manager.updateConfig(sc, restConfig);
		assertTrue("update profile", result.booleanValue());
		List<DomAppletConfig> list = manager.getConfigurations(sc);
		assertEquals("getProfiles listsize", 1, list.size());
		DomAppletConfig other = list.get(0);
		assertEquals("get from database", config.getAppletID(), other.getAppletID());
		assertEquals("get from database", config.getLaunchdata(), other.getLaunchdata());
		assertEquals("get from database", config.getLanguage(), other.getLanguage());
		assertEquals("get from database", config.getName(), other.getName());
		assertEquals("get from database", config.getId().getIdString(), other.getId().getIdString());
	}

	@Test
	public void testRemoveAppletConfig() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestAppletConfig restConfig;
		DomAppletConfig  config;
		config = new DomAppletConfig();
		config.setId( manager.getConfigurations(sc).get(0).getId());
		config.setAppletID(1);
		config.setName("other name");
		config.setLanguage("ot");
		config.setLaunchdata("other data");
		restConfig = new RestAppletConfig();
		restConfig.setRestContext(new DomContext());
		restConfig.setDomAppletConfig(config);
		Boolean result = manager.removeConfig(sc, restConfig);
		assertTrue("remove config", result.booleanValue());
		List<DomAppletConfig> list = manager.getConfigurations(sc);
		assertEquals("getProfiles listsize", 0, list.size());
		result = manager.removeConfig(sc, restConfig);
		assertFalse("remove twice", result.booleanValue());		
	}
	
}
