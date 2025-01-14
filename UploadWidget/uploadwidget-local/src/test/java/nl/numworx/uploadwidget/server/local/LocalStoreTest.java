package nl.numworx.uploadwidget.server.local;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;

public class LocalStoreTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("java.io.tmpdir", System.getProperty("user.dir"));
	}

	private Store store;
	@Before
	public void setUp() throws Exception {
		store = new LocalStore();
	}

	@After
	public void tearDown() throws Exception {
		Iterable<AtomEntry> i = store.getEntries("");
		List<String> u = new ArrayList<>();
		i.forEach( a -> u.add(a.url));
		u.forEach(store::deleteByURL);
	}

	@Test
	public void test() throws IOException {
		byte[] test = "test".getBytes();
		InputStream stream = new ByteArrayInputStream(test);
		AtomEntry entry = new AtomEntry();
		entry.length = (long) test.length;
		entry.title = "test";
		Map<String, String> map = Collections.emptyMap();
		store.addEntry(entry, map, stream);
		stream.close();
		entry = store.getEntries("").iterator().next();

		stream = new URL(entry.url).openStream();
		for (byte b: test) {
			int x = stream.read();
			assertEquals(b, x);
		}
		assertEquals(0, stream.available());
		stream.close();
		
	}

}
