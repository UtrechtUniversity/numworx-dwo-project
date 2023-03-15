package nl.uu.fi.dwo.lms.jclient.rest.managers;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SystemManagerIT {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	      Dwo2ExceptionTranslator.setTranslator(new DWO2ExceptionTranslatorInterface() {

			@Override
			public String encodeJSON(Dwo2ExceptionCode code, String message) {
				// TODO Auto-generated method stub
				return message;
			}

			@Override
			public String decodeMessageInJSON(String json) {
				// TODO Auto-generated method stub
				return json;
			}

			@Override
			public Dwo2ExceptionCode decodeCodeInJSON(String json) {
				// TODO Auto-generated method stub
				return Dwo2ExceptionCode.Client_InternalError;
			}

			@Override
			public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
				return code.toString();
			}});
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	SystemManager manager;
	
	@Before
	public void setUp() throws Exception {
		RestAuthenticator authenticator = new RestAuthenticator();
		authenticator.setServerUrlPath(new URL("http://localhost:8080/dwo/rest"));
		StoredRestManager rest = new StoredRestManager(authenticator);
		manager = new SystemManager(rest);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSystemManager() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSchoolDomSchoolId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSchoolString() throws Dwo2Exception {
		String p = "project";
		DomSchoolFull s = manager.getSchool(p);
		assertNotNull(s.getPasswords());
		assertEquals("premium", AboType.premium, s.getAboType());
	}

	@Test
	public void testGetSchoolClasses() {
		fail("Not yet implemented");
	}

	@Test
	public void testRequestSamlToken() throws Dwo2Exception {
		String u = "wimvvv@kennisnet.org";
		String o = "ENTREE";
		DomSamlUser user = new DomSamlUser();
		user.setSamlOrgId(o);
		user.setSamlUserId(u);
		DomSamlUser result = manager.requestSamlToken(user);
		assertNotNull("request token", result.getAuthToken());
	}

	@Test
	public void testGetSuggestion() throws Dwo2Exception {
		String u = "project_wim";
		String result = manager.getSuggestion(u);
		System.out.println(result);
		assertNotEquals("exists!",u,result);
	}

	@Test public void testSubmitSchool() throws Dwo2Exception {
		DomSchoolFull school = new DomSchoolFull();
		school.setAboType(AboType.demo);
		school.setExpire(new Date());
		List<DomMapEntry<RoleType, String>> passwords = new ArrayList<>();
		passwords.add(new DomMapEntry<>(RoleType.STUDENT, "student"));
		passwords.add(new DomMapEntry<>(RoleType.TEACHER, "teacher"));
		passwords.add(new DomMapEntry<>(RoleType.SCHOOLADMIN, "admin"));
		school.setSchoolRights("_");
		school.setPasswords(passwords);
		school.setSchoolLogin("test");
		school.setSchoolName("TestSchool");
		
		Boolean result = manager.submitSchool(school);
		assertTrue(result.booleanValue());
		System.err.println("please remove test school");
	}
	
	
}
