package nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ValueSortedMapTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPutKV() {
		ValueSortedMap<String,String> m = new ValueSortedMap<>(String::compareTo);
		assertNull(m.put("1", "een"));
		assertEquals("een", m.put("1", "twee"));
		assertNull(m.put("2", "twee"));
		Iterator<Entry<String, String>> s = m.entrySet().iterator();
		assertEquals("1", s.next().getKey());
		assertEquals("2", s.next().getKey());
	}

	@Test
	public void testRemove() {
		ValueSortedMap<String,String> m = new ValueSortedMap<>(String::compareTo);
		assertNull(m.put("1", "een"));
		assertEquals("een", m.remove("1"));
		assertTrue(m.isEmpty());
	}

	@Test
	public void testValues() {
		ValueSortedMap<String,String> m = new ValueSortedMap<>(String::compareTo);
		assertNull(m.put("0", "xxx"));
		assertNull(m.put("1", "een"));
		assertEquals("xxx",m.put("0", "null"));
		Collection<String> values = m.values();
		Iterator<String> iter = values.iterator();
		assertEquals("een", iter.next());
		assertEquals("null", iter.next());
		assertFalse(iter.hasNext());
		
	}

}
