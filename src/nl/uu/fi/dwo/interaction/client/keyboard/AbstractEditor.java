package nl.uu.fi.dwo.interaction.client.keyboard;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class AbstractEditor implements FormuleEditorIF {

	public static final AbstractEditor NULL = new AbstractEditor();
	
	
	protected AbstractEditor() {
	}

	@Override
	public void clearAll() {
	}

	@Override
	public void insert(String text) {
	}

	@Override
	public FormuleFont getDefaultFont() {
		return null;
	}

	@Override
	public void setFont(FormuleFont font) {
	}

	@Override
	public void setCurrentElementRepaint() {
	}

	@Override
	public void enter() {
	}

	@Override
	public void removeCurrentElement() {
	}

	@Override
	public void removeNextElement() {
	}

	@Override
	public void cursorToLeft() {
	}

	@Override
	public void cursorToLeftShift() {
	}

	@Override
	public void cursorToRight() {
	}

	@Override
	public void cursorToRightShift() {
	}

	@Override
	public void cursorUp() {
	}

	@Override
	public void cursorDown() {
	}

	@Override
	public void insert(char charAt) {
	}

	@Override
	public String getSelectionString() {
		return null;
	}

	@Override
	public void knip(FormuleClipboardIF clip) {
	}

	@Override
	public void kopieer(FormuleClipboardIF clip) {
	}

	@Override
	public void plak(FormuleClipboardIF clip) {
	}

	@Override
	public void macht() {
	}

	@Override
	public void wortel() {
	}

	@Override
	public void breuk() {
	}

	@Override
	public void kwadraat() {
	}

	@Override
	public void ndewortel() {
	}

	@Override
	public void haakjes() {
	}

	@Override
	public void integraal() {
	}

	@Override
	public void prv() {
	}

	@Override
	public void ndelog() {
	}

	@Override
	public void abs() {
	}

	@Override
	public void subscript() {
	}

	@Override
	public void bin() {
	}

	@Override
	public void diff() {
	}
	
	@Override
	public void diff_partial() {
		
	}

	@Override
	public void limiet0() {
	}

	@Override
	public void limiet1() {
	}

	@Override
	public void limiet2() {
	}

	@Override
	public void primitieve() {
	}

	@Override
	public void conjug() {
	}

	@Override
	public void sigma() {
	}

}
