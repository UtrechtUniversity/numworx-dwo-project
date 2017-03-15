package nl.uu.fi.dwo.formule.client.formuleobjects;

import junit.framework.TestCase;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.junit.client.GWTTestCase;

public class RegelTest extends TestCase {

	class MockHolder extends FormuleHolder {
		@Override
		public Canvas createCanvas(FormuleElement element) {
			return null;
		}

		@Override
		public Context2d createContext2d(FormuleElement element) {
			return null; // measure
		}

		@Override
		public double measureWidth(FormuleElement element, FormuleFont f,
				String string) {

			return f.getFontSize() * string.length();
		}
	}

	public String getModuleName() {
		return "nl.uu.fi.dwo.formule.Formule";
	}

	public void testZetMaat() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel  regel = holder.getCurrentRegel();
		FormuleTeken t1 = new FormuleTeken(regel, '+');
		regel.insert(t1);
		regel.validate();
		assertFalse(t1.getHeight() == 0);
		assertFalse(t1.getWidth() == 0);
		assertFalse(regel.getWidth() == 0);
	}
}
