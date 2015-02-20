package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardMeetkunde extends DesktopKeyboardGonio {

	public DesktopKeyboardMeetkunde() {
		c24.getUpFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C24.png'>");
		c24.getDownFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C24-2.png'>");
		c25.getUpFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C25.png'>");
		c25.getDownFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C25-2.png'>");
		c26.getUpFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C26.png'>");
		c26.getDownFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C26-2.png'>");
		c27.getUpFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C27.png'>");
		c27.getDownFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C27-2.png'>");
		c28.getUpFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C28.png'>");
		c28.getDownFace().setHTML("<img src='images/kb/Meetkunde/Desktop/C28-2.png'>");
	}
	
	@Override
	void onC28(ClickEvent e) {
		getEditor().insert("~");
	}


}
