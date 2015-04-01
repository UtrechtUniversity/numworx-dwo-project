package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardGonio extends TabletKeyboard {

	public TabletKeyboardGonio() {
		t1_9.setHTML("<span class='gonio gonio-T1-9'></span>");
		t1_10.setHTML("<span class='gonio gonio-T1-10'></span>");

		//t2_8.setHTML("<span class='gonio gonio-T2-8'></span>");
		t2_9.setHTML("<span class='gonio gonio-T2-9'></span>");
		t2_10.setHTML("<span class='gonio gonio-T2-10'></span>");

		t3_8.setHTML("<span class='gonio gonio-T3-8'></span>");
		t3_9.setHTML("<span class='gonio gonio-T3-9'></span>");
		t3_10.setHTML("<span class='gonio gonio-T3-10'></span>");

		t4_8.setHTML("<span class='gonio gonio-T4-8'></span>");
		t4_9.setHTML("<span class='gonio gonio-T4-9'></span>");
		t4_10.setHTML("<span class='gonio gonio-T4-10'></span>");
		pad.t2_16.addStyleName("is-active");
		disableKey(t4_14);
		disableKey(t4_15);
	}

	void onT1_9(ClickEvent e) {getEditor().insert('α');}
	void onT1_10(ClickEvent e) {getEditor().insert('β');}

	//void onT2_8(ClickEvent e) {getEditor().insert("rad");}
	void onT2_9(ClickEvent e) {getEditor().insert('γ');}
	void onT2_10(ClickEvent e) {getEditor().insert('λ');}
	
	void onT3_8(ClickEvent e) {getEditor().insert('∠');}
	void onT3_9(ClickEvent e) {getEditor().insert('μ');}
	void onT3_10(ClickEvent e) {getEditor().insert('σ');}

	void onT4_8(ClickEvent e) {getEditor().insert("sin");}
	void onT4_9(ClickEvent e) {getEditor().insert("cos");}
	void onT4_10(ClickEvent e) {getEditor().insert("tan");}

}
