package nl.uu.fi.dwo.interaction.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormuleFontTest {

	@Test
	public void testCreateCopy() {
		FormuleFont een = FormuleFont.createFromFontSize(10);
		FormuleFont twee = een.createCopy();
		FormuleFont drie = een.createSmallCopy();
		assertEquals(een.toString(), een, twee);
		assertFalse(een == twee);
		assertNotEquals(drie.toString(), drie, een);
		
	}

	@Test
	public void testEqualsObject() {
		FormuleFont een = FormuleFont.createFromFontSize(10);
		FormuleFont twee = FormuleFont.createFromFontSize(10);
		assertEquals(een.toString(), een, twee);
		assertFalse(een == twee);
		een.setItalic(true);
		twee.setItalic(false);
		assertNotEquals(een, twee);
	}

}
