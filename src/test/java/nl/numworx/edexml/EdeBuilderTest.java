package nl.numworx.edexml;

import java.io.InputStream;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class EdeBuilderTest extends TestCase {

	EdeXmlBuilder builder;

	
	protected void setUp() throws Exception {
		builder = new EdeXmlBuilder();
	}

	protected void tearDown() throws Exception {
	}

	public void testSetSource() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/sample1.xml");
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setSource(is);
		assertNotNull(builder.getDocument());
	}
	
	public void testParseGroepen() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/sample1.xml");
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setSource(is);
		Map<String, DomSchoolClassFull> result = builder.parseGroepen();
		assertEquals(8, result.size());
	}
	
	public void testParseLeerlingen() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/sample1.xml");
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setSource(is);
		Map<String, DomUserFull> result = builder.parseLeerlingen();
		assertEquals(3, result.size());
	}
	
	public void testParseLeerkrachten() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/sample1.xml");
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setSource(is);
		Map<String, DomUserFull> result = builder.parseLeerkrachten();
		assertEquals(3, result.size());
	}
}
