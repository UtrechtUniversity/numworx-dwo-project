package nl.numworx.edexml;

import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;
import nl.numworx.edexml.ServerTest.Translator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class ServerTest extends TestCase {
	
	static class Translator implements DWO2ExceptionTranslatorInterface {

		@Override
		public String encodeJSON(Dwo2ExceptionCode code, String message) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String decodeMessageInJSON(String json) {
			return json;
		}

		@Override
		public Dwo2ExceptionCode decodeCodeInJSON(String json) {
			return Dwo2ExceptionCode.Rest_CanNotReachServer;
		}

		@Override
		public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
			return code.toString();
		}

	}

	static { 
		Dwo2ExceptionTranslator.setTranslator(new Translator());
	}
	

	private ServerBuilder builder;

	protected void setUp() throws Exception {
		builder = new ServerBuilder();
		builder.setSource("project_wim", "d7f69547d875d5984c7c0d185f62a81b", new URL("http://localhost:8080/dwo/"));
		builder.setRealm("@edex");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLeerlingen() throws Exception {
		Map<String, DomUserFull> result = builder.parseLeerlingen();
		assertEquals(400, result.size());
	}
	
	public void testLeerkrachten() throws Exception {
		Map<String, DomUserFull> result = builder.parseLeerkrachten();
		assertEquals(33, result.size());
	}

	public void testSchoolClas() throws Exception {
		Map<String,DomSchoolClassFull> result = builder.parseGroepen();
		assertEquals(53, result.size());
	}
	
	public void testMembers() throws Exception {
		Map<String,?> result = builder.memberships();
		assertEquals(414, result.size());
	}
}
