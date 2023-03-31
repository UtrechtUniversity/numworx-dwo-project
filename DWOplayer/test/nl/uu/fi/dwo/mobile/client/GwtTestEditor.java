package nl.uu.fi.dwo.mobile.client;


import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;

import org.junit.Ignore;
import org.junit.Test;

public class GwtTestEditor extends BaseCase
{
	@Test
	public void test_true()
	{
		assertTrue(true);
	}

	@Test @Ignore // faalt met een unexpected error in canvas.measuretext
	public void xtest_tostring()
	{
		String formule = "$w1234$b6$n8@@7@";

		FormuleEditor editor = new FormuleEditor();

		editor.insert(formule);

		assertEquals(formule, editor.toString());
	}

	@Test @Ignore
	public void xtest_toStringViewer()
	{
		String formule = "$w1234$b6$n8@@7@";

		FormuleViewer editor = new FormuleViewer(formule);

		assertEquals(formule, editor.toString());
	}
}
