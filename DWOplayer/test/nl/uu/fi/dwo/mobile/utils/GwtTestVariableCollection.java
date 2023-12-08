package nl.uu.fi.dwo.mobile.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Test;

import nl.uu.fi.dwo.mobile.client.BaseCase;

public class GwtTestVariableCollection extends BaseCase {
	  private Logger LOG;
	  public void gwtSetUp() throws Exception {
		    LOG = Logger.getLogger("VariableCollection");
	  }
	@Test
	public void testSetVariablesOK() {
		String randVarString = "a=1;b=2";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);
		assertTrue(wellSet);
		Map<?, ?> map = vc.getRandomValues();
		assertEquals(2, map.size());
		Set<String> set = new HashSet<String>(); set.add("a"); set.add("b");
		assertEquals(set, new HashSet<>(map.keySet()));
	}
	@Test
	public void testSetVariablesNietOK() {
		String randVarString = "a=1..3; ;b=2..4";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);
		assertFalse(wellSet); // parse error
 		Map<?, ?> map = vc.getRandomValues();
 		String[] namen = vc.getVariableNames();
 		LOG.severe("random = " + map);
 		LOG.severe("namen = " + Arrays.toString(namen));
		assertEquals("map", 2, map.size());
		assertEquals("namen", 2, namen.length);
		Set<String> set = new HashSet<String>(); set.add("a"); set.add("b");
		assertEquals(set, new HashSet<>(map.keySet()));
	}

}
