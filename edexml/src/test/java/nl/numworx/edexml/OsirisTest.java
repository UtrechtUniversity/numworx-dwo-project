package nl.numworx.edexml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.xml.sax.InputSource;

import junit.framework.TestCase;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class OsirisTest extends TestCase {

	private OsirisBuilder builder;

	protected void setUp() throws Exception {
		this.builder = new OsirisBuilder();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParseLeerlingen() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/NUMWORX-STUDENT.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		is.setEncoding("UTF-8");
		builder.setLeerlingenSource(is);
		Map<String, DomUserFull> result = builder.parseLeerlingen();
		assertEquals(17, result.size());
		Map<String, Collection<String>> memberships = builder.memberships();
		assertEquals(17, memberships.size());
		
	}

	public void testParseGroepen() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/NUMWORX_CURS.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setGroepenSource(is);
		Map<String, DomSchoolClassFull> groepen = builder.parseGroepen();
		assertEquals(1, groepen.size());
	}

	public void testParseLeerkrachten() throws Exception {
		InputStream in = getClass().getResourceAsStream("/NUMWORX-DOCENT.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setLeerkrachtenSource(is);
		Map<String, DomUserFull> leerkrachten = builder.parseLeerkrachten();
		assertEquals(1, leerkrachten.size());
		Map<String, Collection<String>> memberships = builder.memberships();
		assertEquals(1, memberships.size());
	}

	public void testParseLeerkrachtenEnGroepen() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/NUMWORX_CURS.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setGroepenSource(is);
		in = getClass().getResourceAsStream("/NUMWORX-DOCENT.csv");
		assertNotNull(in);
		is = new InputSource();
		is.setByteStream(in);
		builder.setLeerkrachtenSource(is);
		Map<String, DomUserFull> leerkrachten = builder.parseLeerkrachten();
		assertEquals(1, leerkrachten.size());
		Map<String, Collection<String>> memberships = builder.memberships();
		assertEquals(1, memberships.size());
		assertFalse(memberships.values().iterator().next().iterator().next().contains("*"));
	}
}
