package nl.uu.fi.dwo.mobile.utils;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class VariableCollectionTest extends GWTTestCase {
	@Override
	public String getModuleName()
	{
		return "nl.uu.fi.dwo.mobile.DWO2player";
	}

	@Test
	public void testSetVariablesOK() {
		String randVarString = "a=1;b=2";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);
		assertTrue(wellSet);
		Map map = vc.getRandomValues();
		assertEquals(2, map.size());
		Set<String> set = new HashSet<String>(); set.add("a"); set.add("b");
		assertEquals(set, new HashSet<>(map.keySet()));
	}

}
