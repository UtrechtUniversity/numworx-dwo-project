package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;

public class SecuredDwoAdminProfileManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminProfileManagerIT.class.getName());

    private static DatabaseManager instance = null;
    SecuredDwoAdminProfileManager manager;

	private DomContext context;

	
	
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
        PersistentUser pUser = UserManager.findByUserName("dwoadmin");
        PersistentSchool pSchool = SchoolManager.findEntity(0L);
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.ADMIN);
        context = new DomContext();
        context.setDomHasRole(pHasRole.buildDomHasRole());
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testGetProfiles() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
        RestContext rest = new RestContext();
        rest.setRestContext(context);
		List<DomDwoProfileFull> list = manager.getProfiles(sc, rest);
		assertEquals("getProfiles listsize", 3, list.size());
	}

	@Test
	public void testSubmitProfile() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestDwoProfileFull restDwoProfile;
		DomDwoProfileFull  profile;
		profile = new DomDwoProfileFull();
		profile.setDwoProfileDescription("description");
		profile.setDwoProfileName("name");
		profile.setDwoProfileRights("rights");
		profile.setDwoProfileText("text");
        RestContext rest = new RestContext();
        rest.setRestContext(context);
		restDwoProfile = new RestDwoProfileFull();
		restDwoProfile.setRestContext(context);
		restDwoProfile.setDomDwoProfile(profile);
                int size = manager.getProfiles(sc, rest).size();
		Boolean result = manager.submitProfile(sc, restDwoProfile);
		assertTrue("submit profile", result.booleanValue());
		List<DomDwoProfileFull> list = manager.getProfiles(sc, rest);
		assertEquals("getProfiles listsize", size+1, list.size());
		DomDwoProfileFull other = list.get(size);
		assertEquals("get from database", profile.getDwoProfileDescription(), other.getDwoProfileDescription());
		assertEquals("get from database", profile.getDwoProfileName(), other.getDwoProfileName());
		assertEquals("get from database", profile.getDwoProfileRights(), other.getDwoProfileRights());
		assertEquals("get from database", profile.getDwoProfileText(), other.getDwoProfileText());
		assertNotNull("get from database", other.getId().getIdString());
	}

	@Test
	public void testUpdateProfile() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestDwoProfileFull restDwoProfile;
		DomDwoProfileFull  profile;
		profile = new DomDwoProfileFull();
        RestContext rest = new RestContext();
        rest.setRestContext(context);
		profile.setId( manager.getProfiles(sc,rest).get(0).getId());
		profile.setDwoProfileDescription("other description");
		profile.setDwoProfileName("other name");
		profile.setDwoProfileRights("other rights");
		profile.setDwoProfileText("other text");
		profile.setOptLock(0L);
        int size = manager.getProfiles(sc, rest).size();
		restDwoProfile = new RestDwoProfileFull();
		restDwoProfile.setRestContext(context);
		restDwoProfile.setDomDwoProfile(profile);
		Boolean result = manager.updateProfile(sc, restDwoProfile);
		assertTrue("submit profile", result.booleanValue());
		List<DomDwoProfileFull> list = manager.getProfiles(sc, rest);
		assertEquals("getProfiles listsize", size, list.size());
		DomDwoProfileFull other = list.get(0);
		assertEquals("get from database", profile.getDwoProfileDescription(), other.getDwoProfileDescription());
		assertEquals("get from database", profile.getDwoProfileName(), other.getDwoProfileName());
		assertEquals("get from database", profile.getDwoProfileRights(), other.getDwoProfileRights());
		assertEquals("get from database", profile.getDwoProfileText(), other.getDwoProfileText());
		assertEquals("get from database", profile.getId().getIdString(), other.getId().getIdString());
		assertEquals("get from database", 1L, other.getOptLock().longValue());
	}
	@Test
	public void testUpdateProfileFail() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
		RestDwoProfileFull restDwoProfile;
		DomDwoProfileFull  profile;
		profile = new DomDwoProfileFull();
        RestContext rest = new RestContext();
        rest.setRestContext(context);
		profile.setId( manager.getProfiles(sc,rest).get(0).getId());
		profile.setDwoProfileDescription("other description");
		profile.setDwoProfileName("other name");
		profile.setDwoProfileRights("other rights");
		profile.setDwoProfileText("other text");
		profile.setOptLock(1L);
        int size = manager.getProfiles(sc, rest).size();
		restDwoProfile = new RestDwoProfileFull();
		restDwoProfile.setRestContext(context);
		restDwoProfile.setDomDwoProfile(profile);
		try {
			Boolean result = manager.updateProfile(sc, restDwoProfile);
			fail("should fail " + result);
		} catch(Dwo2RestException e) {
			assertEquals(e.toString(), Dwo2ExceptionCode.Rest_ObjectModified, e.getDwo2Code());
		}
	}

}
