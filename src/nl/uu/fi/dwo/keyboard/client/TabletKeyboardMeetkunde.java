package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardMeetkunde extends TabletKeyboardGonio {

	public TabletKeyboardMeetkunde() {
		t2_8.getUpFace().setHTML("<span class='meetkunde meetkunde-T2-8'></span>");
		t3_8.getUpFace().setHTML("<span class='meetkunde meetkunde-T3-8'></span>");
	}
	void onT2_8(ClickEvent e) {getEditor().insert('°');}
	void onT3_8(ClickEvent e) {getEditor().insert('~');}

}
