package nl.numworx.edexml;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.xml.sax.InputSource;

import junit.framework.TestCase;
import nl.numworx.edexml.ServerITTest.Translator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class ServerITTest extends TestCase {
	
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
		  System.err.println(json);
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

	public void testIT() throws Exception {
	  EdeXmlBuilder input = new EdeXmlBuilder();
      InputStream in  = getClass().getResourceAsStream("/sample1.xml");
      InputSource is = new InputSource();
      is.setByteStream(in);
      input.setSource(is);
      
      Map<String, DomUserFull> leerlingen = input.parseLeerlingen();
      Map<String, DomSchoolClassFull> groepen = input.parseGroepen();
      Map<String, DomUserFull> leerkrachten = input.parseLeerkrachten();
      Map<String, Collection<String>> members = input.memberships();
      
      builder.addSchoolClasses(groepen);
      builder.addStudents(leerlingen, members, groepen);
      builder.addTeachers(leerkrachten, members, groepen);
       input = new EdeXmlBuilder();
       in  = getClass().getResourceAsStream("/sample2.xml");
       is = new InputSource();
      is.setByteStream(in);
      input.setSource(is);
      
      leerlingen = input.parseLeerlingen();
      groepen = input.parseGroepen();
      leerkrachten = input.parseLeerkrachten();
      members = input.memberships();
      
      builder.addSchoolClasses(groepen);
      builder.addStudents(leerlingen, members, groepen);
      builder.addTeachers(leerkrachten, members, groepen);
	
	}
	
}
