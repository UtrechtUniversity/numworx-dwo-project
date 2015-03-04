package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardStatistiek extends TabletKeyboard {

	public TabletKeyboardStatistiek() {
		t1_9.setHTML("<span class='statistiek statistiek-T1-9'></span>");
		t1_10.setHTML("<span class='statistiek statistiek-T1-9'></span>");

//		t2_8.setHTML("<span class='statistiek statistiek-T2-8'></span>");
		t2_9.setHTML("<span class='statistiek statistiek-T2-9'></span>");
		t2_10.setHTML("<span class='statistiek statistiek-T2-10'></span>");

		t3_8.setHTML("<span class='statistiek statistiek-T3-8'></span>");
		t3_9.setHTML("<span class='statistiek statistiek-T3-9'></span>");
		t3_10.setHTML("<span class='statistiek statistiek-T3-10'></span>");

		t4_8.setHTML("<span class='statistiek statistiek-T4-8'></span>");
		t4_9.setHTML("<span class='statistiek statistiek-T4-9'><span class='path1'></span><span class='path2'></span><span class='path3'></span></span>");
		t4_10.setHTML("<span class='statistiek statistiek-T4-10'></span>");

		disableKey(t4_14);
		disableKey(t4_15);
	}

	void onT1_9(ClickEvent e) {getEditor().insert("binompdf");}
	void onT1_10(ClickEvent e) {getEditor().insert('!');}

	void onT2_8(ClickEvent e) {getEditor().insert('μ');}
	void onT2_9(ClickEvent e) {getEditor().insert("normalcdf");}
	void onT2_10(ClickEvent e) {getEditor().insert("p\u0302");}
	
	void onT3_8(ClickEvent e) {getEditor().insert('σ');}
	void onT3_9(ClickEvent e) {getEditor().insert("invNorm");}
	void onT3_10(ClickEvent e) {getEditor().insert("nCr");}

	void onT4_8(ClickEvent e) {getEditor().insert('Δ');}
	void onT4_9(ClickEvent e) {getEditor().sigma();}
	void onT4_10(ClickEvent e) {getEditor().insert("nPr");}

}
