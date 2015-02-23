package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardOnderbouw extends DesktopKeyboard {

	public DesktopKeyboardOnderbouw() {
		disableKey(c5);
		disableKey(c7);
		disableKey(c8);
		disableKey(c9);
		disableKey(c10);
		disableKey(c11);
		disableKey(c12);
		disableKey(c19);
		disableKey(c20);
		c24.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C24.png'>");
		c24.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C24-2.png'>");
		c25.getUpFace().setHTML("<img src='images/kb/Gonio/Desktop/C25.png'>");
		c25.getDownFace().setHTML("<img src='images/kb/Gonio/Desktop/C25-2.png'>");
		disableKey(c26);
		disableKey(c27);
		disableKey(c28);
		disableKey(c29);
		disableKey(c30);
		disableKey(c31);
	}

	@Override
	protected void disableKey(Key key) {
		super.disableKey(key);
		//key.removeFromParent();
	}
	@Override
	void onC24(ClickEvent e) {
		getEditor().insert("°");
	}
	@Override
	void onC25(ClickEvent e) {
		getEditor().insert('\u03C0');
	}

}
