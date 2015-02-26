package nl.uu.fi.dwo.interaction.client;

import com.google.gwt.user.client.ui.IsWidget;

public interface FormuleKeyboardIF {

	void setEditor(FormuleEditorIF formuleEditor);

	void backspace();

	void delete();

	void enter();

	void focus();
	
	FormuleEditorIF getEditor();

	void softFocus();

	void blur();

}
