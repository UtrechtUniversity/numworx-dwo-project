package nl.uu.fi.dwo.interaction.client;

public interface FormuleKeyboardIF {

	void setEditor(FormuleEditorIF formuleEditor);

	void backspace();

	void delete();

	void enter();

	FormuleEditorIF getEditor();

}
