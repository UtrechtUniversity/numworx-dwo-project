package fi.servlet.dwomaccess;

import fi.servlet.dwomaccess.DWOmAccess;
import fi.servlet.dwomaccess.DWOmAccess;
import java.io.IOException;
import java.io.StringWriter;

import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import junit.framework.TestCase;

public class XMLEncoderTest extends TestCase {

	private DWOmAccess access;

        @Override
	protected void setUp() throws Exception {
		access = new DWOmAccess();
		access.init();
	}

        @Override
	protected void tearDown() throws Exception {
		access.destroy();
	}

	public void testJSON()  throws IOException {
		int sco = 67842;
		StringWriter out = new StringWriter();
		access.getJSONLaunchData(sco, out);
		Object r = JSONValue.parse(out.toString());
		assertNotNull(r);
	}
	public void testJSON2()  throws IOException {
		int sco = 76537;
		StringWriter out = new StringWriter();
		access.getJSONLaunchData(sco, out);
		assertFalse(out.toString().contains("javaclass"));
		Object r = JSONValue.parse(out.toString());
		assertNotNull(r);
	}
	
	public void testJSON3()  throws IOException {
		int sco = 111753;
		StringWriter out = new StringWriter();
		access.getJSONLaunchData(sco, out);
//		assertFalse(out.toString().contains("javaclass"));
		Object r = JSONValue.parse(out.toString());
		assertNotNull(r);
	}
	public void testJSON4()  throws IOException {
		int sco = 53507;
		StringWriter out = new StringWriter();
		access.getJSONLaunchData(sco, out);
//		assertFalse(out.toString().contains("javaclass"));
		Object r = JSONValue.parse(out.toString());
		assertNotNull(r);
	}

}
