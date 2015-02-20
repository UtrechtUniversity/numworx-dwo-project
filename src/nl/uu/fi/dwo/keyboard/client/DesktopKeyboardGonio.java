package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardGonio extends DesktopKeyboard {

	public DesktopKeyboardGonio() {
		disableKey(c31);
		c24.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C24.png'>");
		c24.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C24-2.png'>");
		c25.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C25.png'>");
		c25.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C25-2.png'>");
		c26.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C26.png'>");
		c26.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C26-2.png'>");
		c27.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C27.png'>");
		c27.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C27-2.png'>");
		c28.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C28.png'>");
		c28.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C28-2.png'>");
		disableKey(c29);
		disableKey(c30);
	}

	@Override
	void onC24(ClickEvent e) {
		getEditor().insert("degree");
	}
	@Override
	void onC25(ClickEvent e) {
		getEditor().insert('\u03C0');
	}
	@Override
	void onC26(ClickEvent e) {
		getEditor().insert("angle");
	}

	@Override
	void onC27(ClickEvent e) {
		// TODO popup		
	}

	@Override
	void onC28(ClickEvent e) {
		getEditor().insert("rad");		
	}

	@Override
	void onC29(ClickEvent e) {
	}

	@Override
	void onC30(ClickEvent e) {
	}

	@Override
	void onC31(ClickEvent e) {
	}

}
