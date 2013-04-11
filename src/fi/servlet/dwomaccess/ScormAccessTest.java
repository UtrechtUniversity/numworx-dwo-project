package fi.servlet.dwomaccess;

import java.net.URL;
import java.util.Hashtable;

import fi.dwo.client.persistence.DbAccessCreator;

import junit.framework.TestCase;

public class ScormAccessTest extends TestCase {

	private ScormAccessIF access;
	private int sco = 19240;
	private static final int user = 70016;

	protected void setUp() throws Exception {
		access = new ScormAccess();
	}

	public void testCommit() throws Exception {
		Hashtable input = new Hashtable();
		input.put("cmi.score.max", "100");
		boolean result = access.Commit(user, sco, input);
		assertTrue(result);
		assertEquals("100", DbAccessCreator.instance().LMSGetValue(sco, user, "cmi.score.max"));
	}

	public void testInitialize() throws Exception {
		Hashtable result = access.Initialize(user, sco);
		assertEquals(1, result.size());
	}

	public void testRemote() throws Exception {
		access = new ScormAccessClient(new URL("http://ws-dev.fisme.science.uu.nl/DWOmAccess/scormaccess"));
		testCommit();
		testInitialize();
	}
	public void testLocal() throws Exception {
		access = new ScormAccessClient(new URL("http://localhost:8080/DWOmAccess/scormaccess"));
		testCommit();
		testInitialize();
	}
	
	
}
