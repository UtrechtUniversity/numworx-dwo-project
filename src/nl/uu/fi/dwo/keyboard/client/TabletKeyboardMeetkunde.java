package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardMeetkunde extends TabletKeyboardGonio {

	public TabletKeyboardMeetkunde() {
		t2_8.getUpFace().setHTML("<img src='images/kb/Meetkunde/Touch/T2.8.png'>");
		t2_8.getDownFace().setHTML("<img src='images/kb/Meetkunde/Touch/T2.8-2.png'>");
		t3_8.getUpFace().setHTML("<img src='images/kb/Meetkunde/Touch/T3.8.png'>");
		t3_8.getDownFace().setHTML("<img src='images/kb/Meetkunde/Touch/T3.8-2.png'>");
	}
	void onT2_8(ClickEvent e) {getEditor().insert('°');}
	void onT3_8(ClickEvent e) {getEditor().insert('~');}

}
