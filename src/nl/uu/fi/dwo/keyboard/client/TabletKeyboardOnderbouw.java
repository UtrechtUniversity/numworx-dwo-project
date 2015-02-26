package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardOnderbouw extends TabletKeyboard {

	public TabletKeyboardOnderbouw() {
		// move around
		t2_6.getUpFace().setHTML(t4_5.getUpFace().getHTML());
		t2_6.getDownFace().setHTML(t4_5.getDownFace().getHTML());

		t3_6.getUpFace().setHTML(t2_7.getUpFace().getHTML());
		t3_6.getDownFace().setHTML(t2_7.getDownFace().getHTML());

		t3_5.getUpFace().setHTML(t3_7.getUpFace().getHTML());
		t3_5.getDownFace().setHTML(t3_7.getDownFace().getHTML());

		t4_5.getUpFace().setHTML(t4_6.getUpFace().getHTML());
		t4_5.getDownFace().setHTML(t4_6.getDownFace().getHTML());
		t4_6.getUpFace().setHTML(t4_7.getUpFace().getHTML());
		t4_6.getDownFace().setHTML(t4_7.getDownFace().getHTML());

		disableKey(t1_8);
		disableKey(t1_9);
		disableKey(t1_10);
		disableKey(t1_11);

		disableKey(t2_7);
		disableKey(t2_8);
		disableKey(t2_9);
		disableKey(t2_10);
		disableKey(t2_11);

		disableKey(t3_7);
		disableKey(t3_8);
		disableKey(t3_9);
		disableKey(t3_10);

		disableKey(t4_7);
		disableKey(t4_8);
		disableKey(t4_9);
		disableKey(t4_10);
		
		String b = res.base();
		

	}

	@Override
	void onT2_6(ClickEvent e) {
		super.onT4_5(e);
	}

	@Override
	void onT3_6(ClickEvent e) {
		super.onT2_7(e);
	}

	@Override
	void onT3_5(ClickEvent e) {
		super.onT3_7(e);
	}

	@Override
	void onT4_5(ClickEvent e) {
		super.onT4_6(e);
	}
	@Override
	void onT4_6(ClickEvent e) {
		super.onT4_7(e);
	}

}
