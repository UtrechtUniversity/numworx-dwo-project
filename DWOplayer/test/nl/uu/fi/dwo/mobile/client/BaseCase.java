package nl.uu.fi.dwo.mobile.client;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class BaseCase extends GWTTestCase
{
	@Override
	public String getModuleName()
	{
		return "nl.uu.fi.dwo.mobile.DWO2player";
	}
	
	@Test 
	@org.junit.Ignore
	public void testDummy() { }
}
