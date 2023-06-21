package nl.numworx.osiris;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.csv.CSVParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.numworx.osiris.servlet.InstallServlet;

public class MainTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParse() throws UnsupportedEncodingException, IOException {
		InputStream in = getClass().getResourceAsStream("/kopie.csv");
		Excel e = new Excel();
		CSVParser o = e.parse(in);
		in.close();
		e.verify(InstallServlet.STUDENTEN);
		assertEquals("rows", 11, o.getHeaderNames().size());
		assertEquals("cols", 11, e.records.size());
	}
	@Test
	public void testParseBOM() throws UnsupportedEncodingException, IOException {
		InputStream in = getClass().getResourceAsStream("/bom-kopie.csv");
		Excel e = new Excel();
		CSVParser o = e.parse(in);
		in.close();
		e.verify(InstallServlet.STUDENTEN);
		assertEquals("rows", 11, o.getHeaderNames().size());
		assertEquals("cols", 11, e.records.size());
	}

}
