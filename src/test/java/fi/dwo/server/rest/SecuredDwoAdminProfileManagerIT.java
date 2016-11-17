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
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestDwoProfileFull;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;

public class SecuredDwoAdminProfileManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminProfileManagerIT.class.getName());

    static DatabaseManager instance = null;
    SecuredDwoAdminProfileManager manager;

	
	
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

//        PersistentSchool school = new PersistentSchool();
//        school.setSchoolName("testSchool");
//        school.setSchoolLogin("testSchool");
//        SchoolManager.create(school);
        
//        PersistentRole role = new PersistentRole();
//        role.setDescription("The DWO Admin");
//        role.setGroupID((long) RoleType.ADMIN.ordinal());
//        role.setGroupname(RoleType.ADMIN.name());
//        RoleManager.create(role);
        
//        PersistentSchoolGroup group = new PersistentSchoolGroup();
//        group.setSchoolID(school.getSchoolID().intValue());
//        group.setPasswd("no password");
//        group.setRole(role);
//        group.setGroupID(role.getGroupID().intValue());
        
//        SchoolGroupManager.create(group);
        
//        PersistentUser user = new PersistentUser();
//        user.setUsername("dwoadmin");
//        user.setEmail("a@b.cd"); user.setGivenName("dwo"); user.setInsertion(""); user.setPassword("no password");
//        user.setLastname("admin"); user.setSingleSchoolAccount(Boolean.FALSE);
//        user.setSchoolGroupId(group.getSchoolGroupID());  
//        user.setRegisterDate(new Date(0));
//        UserManager.create(user);
//        
//        PersistentHasRole hasRole = new PersistentHasRole();
//        PersistentHasRolePK persistentHasRolePK = new PersistentHasRolePK(user.getId(), group.getSchoolGroupID());
//		hasRole.setPersistentHasRolePK(persistentHasRolePK);
//		hasRole.setRegisterDate(new Date(0));
//		HasRoleManager.create(hasRole);
//        
//        PersistentDwoProfile profile = new PersistentDwoProfile();
//        profile.setDwoProfileDescription("default");
//        profile.setDwoProfileName("default");
//        profile.setDwoProfileRights("_");
//        profile.setDwoProfileText("default");
//        DwoProfileManager.create(profile);        
        manager = new SecuredDwoAdminProfileManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testGetProfiles() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		List<DomDwoProfileFull> list = manager.getProfiles(sc);
		assertEquals("getProfiles listsize", 2, list.size());
	}

	@Test
	public void testSubmitProfile() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestDwoProfileFull restDwoProfile;
		DomDwoProfileFull  profile;
		profile = new DomDwoProfileFull();
		profile.setDwoProfileDescription("description");
		profile.setDwoProfileName("name");
		profile.setDwoProfileRights("rights");
		profile.setDwoProfileText("text");
		restDwoProfile = new RestDwoProfileFull();
		restDwoProfile.setRestContext(new DomContext());
		restDwoProfile.setDomDwoProfile(profile);
                int size = manager.getProfiles(sc).size();
		Boolean result = manager.submitProfile(sc, restDwoProfile);
		assertTrue("submit profile", result.booleanValue());
		List<DomDwoProfileFull> list = manager.getProfiles(sc);
		assertEquals("getProfiles listsize", size+1, list.size());
		DomDwoProfileFull other = list.get(size);
		assertEquals("get from database", profile.getDwoProfileDescription(), other.getDwoProfileDescription());
		assertEquals("get from database", profile.getDwoProfileName(), other.getDwoProfileName());
		assertEquals("get from database", profile.getDwoProfileRights(), other.getDwoProfileRights());
		assertEquals("get from database", profile.getDwoProfileText(), other.getDwoProfileText());
		assertNotNull("get from database", other.getId().getIdString());
	}

	@Test
	public void testUpdateProfile() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestDwoProfileFull restDwoProfile;
		DomDwoProfileFull  profile;
		profile = new DomDwoProfileFull();
		profile.setId( manager.getProfiles(sc).get(0).getId());
		profile.setDwoProfileDescription("other description");
		profile.setDwoProfileName("other name");
		profile.setDwoProfileRights("other rights");
		profile.setDwoProfileText("other text");
                int size = manager.getProfiles(sc).size();
		restDwoProfile = new RestDwoProfileFull();
		restDwoProfile.setRestContext(new DomContext());
		restDwoProfile.setDomDwoProfile(profile);
		Boolean result = manager.updateProfile(sc, restDwoProfile);
		assertTrue("submit profile", result.booleanValue());
		List<DomDwoProfileFull> list = manager.getProfiles(sc);
		assertEquals("getProfiles listsize", size, list.size());
		DomDwoProfileFull other = list.get(0);
		assertEquals("get from database", profile.getDwoProfileDescription(), other.getDwoProfileDescription());
		assertEquals("get from database", profile.getDwoProfileName(), other.getDwoProfileName());
		assertEquals("get from database", profile.getDwoProfileRights(), other.getDwoProfileRights());
		assertEquals("get from database", profile.getDwoProfileText(), other.getDwoProfileText());
		assertEquals("get from database", profile.getId().getIdString(), other.getId().getIdString());
	}

}
