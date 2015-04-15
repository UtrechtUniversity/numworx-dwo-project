package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.keyboard.AbstractEditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractKeyboard extends Composite implements FormuleKeyboardIF {

	protected static final int DEFAULT = 0;
	FormuleEditorIF formuleEditor = AbstractEditor.NULL;
	AbstractKeyboard delegate;

	AbstractKeyboard getDelegate() {
		return delegate;
	}

	void setDelegate(AbstractKeyboard delegate) {
		this.delegate = delegate;
	}

	AbstractKeyboard() {
	}

	public FormuleEditorIF getEditor() {
		return formuleEditor;
	}

	public void setEditor(FormuleEditorIF formuleEditor) {
		setEditor0(formuleEditor);
	}

	private void setEditor0(FormuleEditorIF formuleEditor) {
		if(formuleEditor == null) formuleEditor = AbstractEditor.NULL;
		this.formuleEditor = formuleEditor;
	}

	protected void disableKey(FKey key) {
		key.setHTML("");key.addStyleName("disabled");
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
		getEditor().setCurrentElementRepaint();
	}

	void switchABC() {
	}

	void switchGreek() {
	}
	
	void switch123() {	
	}
	
	void switchHand() {
	}

	void switchUpper() {
	}
	
	void switchLower() {
	}

	public void setScrollPanel(Widget w, int h) {
	}

	public void setKeyboard(int nr) {
	}

	public void setWriteMathSet(int nr) {
	}

	final protected void setActiveEditor(FormuleEditorIF formuleEditor) {
		FormuleEditorIF old = getEditor();
		setEditor0(formuleEditor);
		if(old != getEditor())
		{
			old.setFont(old.getDefaultFont());
			old.setCurrentElementRepaint();
		}
	}

	abstract int getKeyboardHeight();
}
