package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class TabletKeyboardOnderbouw extends TabletKeyboard {

	public TabletKeyboardOnderbouw() {
		// move around
		t2_6.setHTML(t4_5.getHTML());

		t3_6.setHTML(t2_7.getHTML());
		t3_5.setHTML(t3_7.getHTML());

		t4_5.setHTML(t4_6.getHTML());
		t4_6.setHTML(t4_7.getHTML());

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
