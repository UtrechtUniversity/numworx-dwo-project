package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleViewer;

import org.junit.Test;

public class EditorTests extends BaseTestCase
{
	@Test
	public void test_true()
	{
		assertTrue(true);
	}

	@Test
	public void test_tostring()
	{
		String formule = "$w1234$b6$n8@@7@";

		FormuleEditor editor = new FormuleEditor();

		editor.insert(formule);

		assertEquals(formule, editor.toString());
	}

	@Test
	public void test_toStringViewer()
	{
		String formule = "$w1234$b6$n8@@7@";

		FormuleViewer editor = new FormuleViewer(formule);

		assertEquals(formule, editor.toString());
	}
}
