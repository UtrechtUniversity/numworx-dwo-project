package nl.numworx.gwtpatch.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class GWTPatchTest extends TestCase {

	GWTPatch diff;
	protected void setUp() throws Exception {
		diff = new GWTPatch();
	}

	protected void tearDown() throws Exception {
		diff = null;
	}

	public void testCreateDiff() {
		Object n = null;
		Boolean b = Boolean.valueOf(true);
		ObjectList arr = diff.createDiff(n, b);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "replace", item.getString("op"));
		assertEquals("path","",item.getString("path") );
		assertTrue("value", item.getBoolean("value"));
	}

	public void testdiff1() {
		Map n = Collections.EMPTY_MAP;
		Map b = Collections.singletonMap("aap", "noot");
		Map c = Collections.singletonMap("aap", "mies");
		ObjectList arr = diff.createDiff(n, b);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "add", item.getString("op"));
		assertEquals("path","/aap",item.getString("path") );
		assertEquals("value", "noot", item.getString("value"));		
	}
	public void testdiff2() {
		Map n = Collections.EMPTY_MAP;
		Map b = Collections.singletonMap("aap", "noot");
		Map c = Collections.singletonMap("aap", "mies");
		ObjectList arr = diff.createDiff(c, b);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "replace", item.getString("op"));
		assertEquals("path","/aap",item.getString("path") );
		assertEquals("noot", item.getString("value"));		
	}
	public void testdiff3() {
		Map n = Collections.EMPTY_MAP;
		Map b = Collections.singletonMap("aap", "noot");
		Map c = Collections.singletonMap("aap", "mies");
		ObjectList arr = diff.createDiff(c, n);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "remove", item.getString("op"));
		assertEquals("path","/aap",item.getString("path") );
		assertFalse("value", item.containsKey("value"));		
	}
	
	public void testdiff4() {
		List n = Collections.EMPTY_LIST;
		List<Boolean> b = Collections.singletonList(Boolean.TRUE);
		ObjectList arr = diff.createDiff(n, b);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "add", item.getString("op"));
		assertEquals("path","/0",item.getString("path") );
		assertTrue("value", item.getBoolean("value"));		
	}
	public void testdiff6() {
		List n = Collections.singletonList(Boolean.FALSE);
		List<Boolean> b = Collections.singletonList(Boolean.TRUE);
		ObjectList arr = diff.createDiff(n, b);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "replace", item.getString("op"));
		assertEquals("path","/0",item.getString("path") );
		assertTrue("value", item.getBoolean("value"));		
	}
	public void testdiff5() {
		List n = Collections.EMPTY_LIST;
		List<Boolean> b = Collections.singletonList(Boolean.TRUE);
		ObjectList arr = diff.createDiff(b, n);
		assertEquals(1, arr.size());
		ObjectMap item = arr.getObjectMap(0);
		assertEquals("op", "remove", item.getString("op"));
		assertEquals("path","/0",item.getString("path") );
		assertFalse("value", item.containsKey("value"));		
	}
	
	public void testdiff7() {
		Map aap = new HashMap();
		aap.put("aap", 1.0);
		aap.put("noot", 2.0);
		aap.put("mies", 3.0);
		Map noot = new TreeMap(aap);
		noot.put("mies", 4.0);
		noot.put("aap", 0.0);
		List list = Arrays.asList(aap, noot);
		List niew = Arrays.asList(noot, aap);
		ObjectList arr = diff.createDiff(list, niew);
		assertEquals(4, arr.size());
	}
	
	public void testdiff8() {
		List lang = Arrays.asList(1,2,3,4,5);
		List kort = Arrays.asList(1,2,3);
		ObjectList arr = diff.createDiff(lang, kort);
		assertEquals(2, arr.size());
		ObjectMap arr0 = arr.getObjectMap(0);
		ObjectMap arr1 = arr.getObjectMap(1);
		assertEquals("/4", arr0.getString("path"));
		assertEquals("remove", arr0.getString("op"));
		
	}
	
}
