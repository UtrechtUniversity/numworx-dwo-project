package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardMeetkunde extends DesktopKeyboardGonio {

	public DesktopKeyboardMeetkunde() {
		c29.getUpFace().setHTML(c28.getUpFace().getHTML());
		c28.getUpFace().setHTML("<span class='meetkunde meetkunde-C28'></span>");
	}
	
	@Override
	void onC28(ClickEvent e) {
		getEditor().insert("~");
	}
	void onC29(ClickEvent e) {
		super.onC28(e);
	}
	public AbstractKeyboard init() {
		return this;
	}
}
