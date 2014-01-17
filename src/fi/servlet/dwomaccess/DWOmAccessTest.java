package fi.servlet.dwomaccess;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

public class DWOmAccessTest extends TestCase {
	DWOmAccess access;

	@Override
	protected void setUp() throws Exception {
		access = new DWOmAccess();
		access.init();
	}

	@Override
	protected void tearDown() throws Exception {
		access.destroy();
	}

	public void testGetLaunchDataIntOutputStream() throws IOException {
		int sco = 76537; // wim kladje test
		OutputStream out = new FileOutputStream("test.xml");
		access.getLaunchData(sco, out);
		out.close();
		// TODO verity no base64 strings in text.xml.
	}

}
