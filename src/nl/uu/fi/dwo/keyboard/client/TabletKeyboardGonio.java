package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardGonio extends TabletKeyboard {

	public TabletKeyboardGonio() {
		String b = res.base();
		t1_9.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T1.9.png'>");
		t1_9.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T1.9-2.png'>");
		t1_10.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T1.10.png'>");
		t1_10.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T1.10-2.png'>");

		t2_8.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T2.8.png'>");
		t2_8.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T2.8-2.png'>");
		t2_9.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T2.9.png'>");
		t2_9.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T2.9-2.png'>");
		t2_10.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T2.10.png'>");
		t2_10.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T2.10-2.png'>");

		t3_8.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T3.8.png'>");
		t3_8.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T3.8-2.png'>");
		t3_9.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T3.9.png'>");
		t3_9.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T3.9-2.png'>");
		t3_10.getUpFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.8.png'>");
		t3_10.getDownFace().setHTML("<img src='"+b+"/Statistiek/Touch/T3.8-2.png'>");

		t4_8.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T4.8.png'>");
		t4_8.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T4.8-2.png'>");
		t4_9.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T4.9.png'>");
		t4_9.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T4.9-2.png'>");
		t4_10.getUpFace().setHTML("<img src='"+b+"/Gonio/Touch/T4.10.png'>");
		t4_10.getDownFace().setHTML("<img src='"+b+"/Gonio/Touch/T4.10-2.png'>");

		disableKey(t4_14);
		disableKey(t4_15);
	}

	void onT1_9(ClickEvent e) {getEditor().insert('α');}
	void onT1_10(ClickEvent e) {getEditor().insert('β');}

	void onT2_8(ClickEvent e) {getEditor().insert("rad");}
	void onT2_9(ClickEvent e) {getEditor().insert('γ');}
	void onT2_10(ClickEvent e) {getEditor().insert('λ');}
	
	void onT3_8(ClickEvent e) {getEditor().insert('∠');}
	void onT3_9(ClickEvent e) {getEditor().insert('μ');}
	void onT3_10(ClickEvent e) {getEditor().insert('σ');}

	void onT4_8(ClickEvent e) {getEditor().insert("sin");}
	void onT4_9(ClickEvent e) {getEditor().insert("cos");}
	void onT4_10(ClickEvent e) {getEditor().insert("tan");}

}
