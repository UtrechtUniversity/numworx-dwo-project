package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardStatistiek extends TabletKeyboard {

	public TabletKeyboardStatistiek() {
		String b = res.base();
		t1_9.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T1.9.png'>");
		t1_9.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T1.9-2.png'>");
		t1_10.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T1.10.png'>");
		t1_10.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T1.10-2.png'>");

		t2_8.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T2.8.png'>");
		t2_8.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T2.8-2.png'>");
		t2_9.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T2.9.png'>");
		t2_9.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T2.9-2.png'>");
		t2_10.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T2.10.png'>");
		t2_10.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T2.10-2.png'>");

		t3_8.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.8.png'>");
		t3_8.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.8-2.png'>");
		t3_9.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.9.png'>");
		t3_9.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.9-2.png'>");
		t3_10.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.10.png'>");
		t3_10.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.10-2.png'>");

		t4_8.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T4.8.png'>");
		t4_8.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T4.8-2.png'>");
		t4_9.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T4.9.png'>");
		t4_9.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T4.9-2.png'>");
		t4_10.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T4.10.png'>");
		t4_10.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T4.10-2.png'>");

		disableKey(t4_14);
		disableKey(t4_15);
	}

	void onT1_9(ClickEvent e) {getEditor().insert("Bpdf");}
	void onT1_10(ClickEvent e) {getEditor().insert('!');}

	void onT2_8(ClickEvent e) {getEditor().insert('μ');}
	void onT2_9(ClickEvent e) {getEditor().insert("Ncdf");}
	void onT2_10(ClickEvent e) {getEditor().insert("p\u0302");}
	
	void onT3_8(ClickEvent e) {getEditor().insert('σ');}
	void onT3_9(ClickEvent e) {getEditor().insert("invN");}
	void onT3_10(ClickEvent e) {getEditor().insert("nCr");}

	void onT4_8(ClickEvent e) {getEditor().insert('Δ');}
	void onT4_9(ClickEvent e) {getEditor().sigma();}
	void onT4_10(ClickEvent e) {getEditor().insert("nPr");}

}
