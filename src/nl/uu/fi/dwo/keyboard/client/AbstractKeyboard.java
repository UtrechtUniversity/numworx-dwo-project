package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.user.client.ui.Composite;

abstract class AbstractKeyboard extends Composite implements FormuleKeyboardIF {

	FormuleEditorIF formuleEditor;

	AbstractKeyboard() {
	}

	public FormuleEditorIF getEditor() {
		return formuleEditor;
	}

	public void setEditor(FormuleEditorIF formuleEditor) {
		this.formuleEditor = formuleEditor;
	}

	protected void disableKey(Key key) {
		key.getUpFace().setHTML("");key.getDownFace().setHTML("");key.setEnabled(false);
	}

	@Override
	public void backspace() {
		getEditor().removeCurrentElement();
		
	}

	@Override
	public void delete() {
		getEditor().removeNextElement();
		
	}

	@Override
	public void enter() {
		getEditor().enter();
		
	}

	@Override
	public void focus() {
		this.setVisible(true);
		
	}

	@Override
	public void softFocus() {
	}

	public void blur() {
		this.setVisible(false);
	}

	void switchABC() {
	}

	void switch123() {	
	}
	
	void switchHand() {
	}

}
