package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DesktopKeyboardOnderbouw extends DesktopKeyboard {

	public DesktopKeyboardOnderbouw() {
		disableKey(c5);
		disableKey(c7);
		disableKey(c8);
		disableKey(c9);
		disableKey(c10);
		disableKey(c11);
		disableKey(c12);
		disableKey(c19);
		disableKey(c20);
		c25.setHTML("<span class='onderbouw onderbouw-C16'></span>");
		c26.setHTML("<span class='onderbouw onderbouw-C17'></span>");
		disableKey(c27);
		disableKey(c28);
		disableKey(c29);
		disableKey(c30);
		disableKey(c31);
	}

	@Override
	protected void disableKey(FKey key) {
		super.disableKey(key);
		HasWidgets parent = (HasWidgets) key.getParent(); key.removeFromParent();
		parent.add(key);
	}
	@Override
	void onC26(ClickEvent e) {
		getEditor().insert("°");
	}
	@Override
	void onC25(ClickEvent e) {
		getEditor().insert('\u03C0');
	}

// disabled keys produce actions....	
	@Override void onC5(ClickEvent e) {}
	@Override void onC7(ClickEvent e) {}
	@Override void onC8(ClickEvent e) {}
	@Override void onC9(ClickEvent e) {}
	@Override void onC10(ClickEvent e) {}
	@Override void onC11(ClickEvent e) {}
	@Override void onC12(ClickEvent e) {}
	@Override void onC19(ClickEvent e) {}
	@Override void onC20(ClickEvent e) {}
	@Override void onC27(ClickEvent e) {}
	@Override void onC28(ClickEvent e) {}
	@Override void onC29(ClickEvent e) {}
	@Override void onC30(ClickEvent e) {}
	@Override void onC31(ClickEvent e) {}

	@Override
	public void functionKey(int code) {
		if(code >= 6) return;
		if(code == 5)
			getEditor().ndewortel();
		else
			super.functionKey(code);
	}
	
}
