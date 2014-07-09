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
	
	void cursorToLeftShift();

	void cursorToRight();
	
	void cursorToRightShift();
	
	void cursorUp();
	
	void cursorDown();

	void insert(char charAt);

	String getSelectionString();

	void macht();

	void wortel();

	void breuk();

	void kwadraat();

	void ndewortel();

	void haakjes();

	void integraal();
	
	void prv();
	
	void ndelog();
	
	void abs();
	
	void subscript();
	
	void bin();
	
	void diff();
	
	void limiet0();
	
	void limiet1();
	
	void limiet2();
	
	void primitieve();
	
	void conjug();
	
	void sigma();
	
	
}
