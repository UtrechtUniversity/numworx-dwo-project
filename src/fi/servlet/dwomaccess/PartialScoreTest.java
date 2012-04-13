package fi.servlet.dwomaccess;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import junit.framework.TestCase;


public class PartialScoreTest extends TestCase {

	private static final int sco = 19240;
	private static final int user = 70016;
	PartialScoreIF getter;

	public void testGetScoreMapList() throws Exception {
		DWOmAccess dwOmAccess = new DWOmAccess();
		dwOmAccess.init();
		getter = dwOmAccess;
		doGetScoreMapList();
	}
	
	
	@SuppressWarnings("unchecked")
	public void doGetScoreMapList() throws Exception {
		Vector result = getter.getScoreMapList(sco, user);
		assertNotNull("geen result?", result);
		System.out.println(result);
	}

	public void testLocalhost() throws Exception {
		URL url = new URL("http://localhost:8080/DWOmAccess/partialScore");
		getter = new PartialScoreClient(url );
		doGetScoreMapList();
	}
	
	public void testRemote() throws Exception {
		URL url = new URL("http://ws.fi.uu.nl/DWOmAccess/partialScore");
		getter = new PartialScoreClient(url );
		doGetScoreMapList();
	}
	
	
	
}
