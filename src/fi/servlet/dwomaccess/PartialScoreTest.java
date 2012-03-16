package fi.servlet.dwomaccess;
import java.util.Vector;

import junit.framework.TestCase;


public class PartialScoreTest extends TestCase {

	private static final int sco = 19240;
	private static final int user = 70016;
	PartialScoreIF getter;
	protected void setUp() throws Exception {
		DWOmAccess dwOmAccess = new DWOmAccess();
		dwOmAccess.init();

		getter = dwOmAccess;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testGetScoreMapList() throws Exception {
		Vector result = getter.getScoreMapList(sco, user);
		assertNotNull("geen result?", result);
		System.out.println(result);
	}

}
