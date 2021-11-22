package nl.numworx.edexml;

import java.io.InputStream;
import java.util.Map;

import org.xml.sax.InputSource;

import junit.framework.TestCase;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class SomtodayTest extends TestCase {

	private SomtodayBuilder builder;

	protected void setUp() throws Exception {
		builder = new SomtodayBuilder();
	}

	protected void tearDown() throws Exception {
	}

	public void testSetSource() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/leerlingNummer_leerlingNaam.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		is.setEncoding("UTF-16");
		builder.setLeerlingenSource(is);
		assertNotNull(builder.getDocument());
	}

	public void testParseLeerlingen() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/leerlingNummer_leerlingNaam.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		builder.setLeerlingenSource(is);
		Map<String, DomUserFull> result = builder.parseLeerlingen();
		assertEquals(745, result.size());
	}

	public void testSetLesGroupLeerlingSource() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/lesgroep-leerlingnummer.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		is.setEncoding("UTF-8");
		builder.setLesGroupLeerlingSource(is);
		Map<String,DomSchoolClassFull> result = builder.parseGroepen();
		assertEquals(524, result.size());
		Map<String,?> memberships = builder.memberships();
		assertEquals(739, memberships.size());
	}

	public void testSetLesGroupDocentSource() throws Exception {
		InputStream in  = getClass().getResourceAsStream("/lesgroep-docent.csv");
		assertNotNull(in);
		InputSource is = new InputSource();
		is.setByteStream(in);
		is.setEncoding("UTF-8");
		builder.setLesGroupDocentSource(is);
		Map<String,DomUserFull> docenten = builder.parseLeerkrachten();
		assertEquals(6, docenten.size());
		Map<String,DomSchoolClassFull> result = builder.parseGroepen();
		assertEquals(29, result.size());
		Map<String,?> memberships = builder.memberships();
		assertEquals(6, memberships.size());
	}
}
