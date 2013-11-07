package fi.servlet.dwomaccess;

import java.io.IOException;

import junit.framework.TestCase;

public class XMLEncoderTest extends TestCase {

	private DWOmAccess access;

	protected void setUp() throws Exception {
		access = new DWOmAccess();
		access.init();
	}

	protected void tearDown() throws Exception {
		access.destroy();
	}

	public void testEncode() throws IOException {
		int sco = 67842;
		access.getLaunchData(sco, System.out);
	}

}
