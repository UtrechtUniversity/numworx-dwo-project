package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Machtvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.WortelVak;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Panel;

public class GwtTestRegel extends GWTTestCase {

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

		@Override
		public Panel getAsPanel() {
			return null;
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
	
	public void testSelection() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken t1 = new FormuleTeken(regel, "1");
		FormuleTeken t2 = new FormuleTeken(regel, "2");
		FormuleTeken t3 = new FormuleTeken(regel, "3");
		regel.insert(t1); regel.insert(t2); regel.insert(t3);
		regel.validate();
		int w = regel.getWidth();
		int h = regel.getHeight();
		FormuleRegel r = regel.selection(2, h/2, w-2, h/2);
		//assertNull(r);
		assertTrue(t1.isSelected());
		String selectie = regel.getSelectionString();
		assertEquals("123", selectie);
	}
	
	public void testMathML12() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken twee = new FormuleTeken(regel, "2");
		FormuleTeken een = new FormuleTeken(regel, "1");
		Machtvak macht = new Machtvak(regel);
		macht.getChild().insert(twee);
		regel.insert(een);
		regel.insert(macht);
		String mathml = regel.toMathML();
		assertEquals("<mrow><msup><mtext>1</mtext><mtext>2</mtext></msup></mrow>",mathml);
	}

	public void testMathML21() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken twee = new FormuleTeken(regel, "2");
		FormuleTeken een = new FormuleTeken(regel, "1");
		Machtvak macht = new Machtvak(regel);
		macht.getChild().insert(twee);
		regel.insert(macht);
		regel.insert(een);
		String mathml = regel.toMathML();
		assertEquals("<mrow><msup><mtext/><mtext>2</mtext></msup><mtext>1</mtext></mrow>",mathml);
	}

	public void testMathML2() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken twee = new FormuleTeken(regel, "2");
		FormuleTeken een = new FormuleTeken(regel, "1");
		Machtvak macht = new Machtvak(regel);
		macht.getChild().insert(twee);
		regel.insert(macht);
		//regel.insert(een);
		String mathml = regel.toMathML();
		assertEquals("<msup><mtext/><mtext>2</mtext></msup>",mathml);
	}

	public void testMathML0() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken twee = new FormuleTeken(regel, "2");
		FormuleTeken een = new FormuleTeken(regel, "1");
		Machtvak macht = new Machtvak(regel);
		macht.getChild().insert(twee);
		//regel.insert(macht);
		//regel.insert(een);
		String mathml = regel.toMathML();
		assertEquals("<mtext>\u25AF</mtext>",mathml);
	}
	public void testMathMLW2() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken twee = new FormuleTeken(regel, "2");
		Machtvak macht = new Machtvak(regel);
		WortelVak wortel = new WortelVak(regel);
		macht.getChild().insert(twee);
		wortel.getChild().insert(macht);
		regel.insert(wortel);
		String mathml = regel.toMathML();
		assertEquals("<msqrt><msup><mtext/><mtext>2</mtext></msup></msqrt>",mathml);
	}
	public void testMathML() {
		FormuleHolder holder = new MockHolder();
		FormuleRegel regel = holder.getCurrentRegel();
		FormuleTeken twee = new FormuleTeken(regel, "2");
		Machtvak macht = new Machtvak(regel);
		WortelVak wortel = new WortelVak(regel);
		//macht.getChild().insert(twee);
		wortel.getChild().insert(macht);
		regel.insert(wortel);
		String mathml = regel.toMathML();
		assertEquals("<msqrt><msup><mtext/><mtext>\u25AF</mtext></msup></msqrt>",mathml);
	}

	public void testMathMLlt() {
      FormuleHolder holder = new MockHolder();
      FormuleRegel regel = holder.getCurrentRegel();
      FormuleTeken twee = new FormuleTeken(regel, "<");
      regel.insert(twee);
      String mathml = regel.toMathML();
      assertEquals("<mtext>&lt;</mtext>",mathml);	  
	}
	
	
}
