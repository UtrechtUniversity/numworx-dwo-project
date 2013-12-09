package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.interaction.client.FormuleFont;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class FontTests extends GWTTestCase
{
	@Override
	public String getModuleName()
	{
		return "nl.uu.fi.dwo.mobile.DWOplayer";
	}

	@Test
	public void test_fontsize()
	{
		FormuleFont ff = FormuleFont.createFromFontSize(12);
		assertEquals(12, ff.getFontSize());
	}

	@Test
	public void test_copy()
	{
		FormuleFont ff = FormuleFont.createFromFontSize(12);
		FormuleFont ffcopy = ff.createCopy();
		assertEquals(ff.getFontStyle(), ffcopy.getFontStyle());
	}
}
