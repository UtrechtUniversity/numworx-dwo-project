package nl.uu.fi.dwo.interaction.client;

public interface FormuleKeyboardIF {

	void setEditor(FormuleEditorIF formuleEditor);

	void backspace();

	void delete();

	void enter();

	void focus();
	
	FormuleEditorIF getEditor();

	void softFocus();

}
