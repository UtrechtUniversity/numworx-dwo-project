package nl.uu.fi.dwo.lms.jclient.rest.managers;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SystemManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SystemManagerIT {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

}
