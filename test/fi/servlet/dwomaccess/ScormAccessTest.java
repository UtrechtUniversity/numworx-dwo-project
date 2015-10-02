package fi.servlet.dwomaccess;

import java.net.URL;
import java.util.Hashtable;

import org.json.simple.JSONValue;

import fi.dwo.client.persistence.DbAccessCreator;
import junit.framework.TestCase;

public class ScormAccessTest extends TestCase {

	private ScormAccessIF access;
	private int sco = 19240; // differentieren 2 
	private static final int user = 70016; // FIXME gebruik peterb, niet iemand willekeurig

	protected void setUp() throws Exception {
		access = new ScormAccess();
	}

	public void testCommit() throws Exception {
		Hashtable input = new Hashtable();
		input.put("cmi.score.max", "100");
		input.put("cmi.suspend_data", "{ \"x\" : \"\u03c0\\" + "u03c0\" } ");
		boolean result = access.Commit(user, sco, input);
		assertTrue(result);
		assertEquals("100", DbAccessCreator.instance().LMSGetValue(sco, user, "cmi.score.max"));
	}

	
	public void testJSON() throws Exception {
		String x = "\u03c0\u001B";
		String enc1 = JSONValue.toJSONString(x);
		String enc2 = ScormAccess.convertUEsc(enc1);
		Object dec1 = JSONValue.parse(enc1);
		Object dec2 = JSONValue.parse(enc2);
		assertEquals("TEST JSON encoder", dec1, dec2);
		assertEquals("EQUAL", x, dec1);
	}
	
	public void testInitialize() throws Exception {
		Hashtable result = access.Initialize(user, sco);
		assertEquals(3, result.size());
		System.out.println(result);
	}

	public void testRemote() throws Exception {
		access = new ScormAccessClient(new URL("http://ws.fisme.science.uu.nl/DWOmAccess/scormaccess"));
		//testCommit();
		testInitialize();
	}
	public void xtestLocal() throws Exception {
		access = new ScormAccessClient(new URL("http://localhost:8080/DWOmAccess/scormaccess"));
		testCommit();
		testInitialize();
	}
	
	
}
