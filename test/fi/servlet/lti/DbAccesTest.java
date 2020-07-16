package fi.servlet.lti;

import junit.framework.TestCase;

public class DbAccesTest extends TestCase {

	public void testIsoToUtf() {
		String encoded = "CassÃ©";
		assertEquals("Cassé", DbAccess.isoToUtf(encoded));
	}
	public void testIsoToUtfnull() {
		String encoded = null;
		assertNull( DbAccess.isoToUtf(encoded));
	}

}
