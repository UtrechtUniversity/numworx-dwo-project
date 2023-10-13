package nl.numworx.oauth2client.server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EntreeSRedirectTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidUsernameString() {
		String in = "Wim o'Velthoven";
		String out = "WimoVelthoven";
		assertEquals(out, EntreeSRedirect.validUsername(in));
	}

}
