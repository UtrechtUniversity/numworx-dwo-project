package nl.uu.fi.dwo.interaction.client;

public interface FormuleEditorIF {

	void clearAll();

	void insert(String text);

	FormuleFont getDefaultFont();

	void setFont(FormuleFont font);

	void setCurrentElementRepaint();

	void enter();

	void removeCurrentElement();

	void removeNextElement();

	void cursorToLeft();

	void cursorToRight();

	void insert(char charAt);

	String getSelectionString();

	void macht();

	void wortel();

	void breuk();

	void kwadraat();

	void ndewortel();

	void haakjes();

	void integraal();

}
