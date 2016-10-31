package nl.uu.fi.dwo.interaction.client.json;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import junit.framework.TestCase;

public class ObjectArrayTest extends TestCase {

	public void test1() throws Exception {
		Object[] arr = new Object[] { "asd", Boolean.TRUE, Integer.valueOf(123) };
		
		ObjectList list = JSONUtilities.wrapList(arr);
	
		assertEquals("asd", list.getString(0));
		assertTrue(list.getBoolean(1));
		assertEquals(123, list.getInt(2));
	}
	
	public void test2() throws Exception {
		Object[] arr = new Object[] { "asd", Boolean.TRUE, Integer.valueOf(123) };
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		list.add(arr);
		ObjectList l = JSONUtilities.wrapList(list);
		
		assertEquals("asd", l.getObjectList(0).getString(0));
	}

	public void test3() throws Exception {
		Object[] arr = new Object[] { "asd", Boolean.TRUE, Integer.valueOf(123) };
		HashMap<String, Object[]> list = new HashMap<String, Object[]>();
		list.put("arr", arr);
		ObjectMap l = JSONUtilities.wrapMap(list);
		
		assertEquals("asd", l.getObjectList("arr").getString(0));
	}


}
