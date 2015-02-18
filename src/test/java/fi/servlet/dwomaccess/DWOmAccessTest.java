package fi.servlet.dwomaccess;

import fi.servlet.dwomaccess.DWOmAccess;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

import org.apache.xmlrpc.applet.XmlRpcException;

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

	public void testGetLaunchDataBytes() throws IOException, XmlRpcException, SQLException {
		int sco = 105645; // wim kladje test
		OutputStream out = new FileOutputStream("test.json.gz");
		out.write(access.getLaunchDataBytes(sco));
		out.close();
		// TODO verity no base64 strings in text.xml.
		access.copy( new GZIPInputStream(new FileInputStream("test.json.gz")), System.out);
	}

}
