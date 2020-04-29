package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.keyboard.client.i18n.Key;

public class DesktopKeyboardOnderbouw extends DesktopKeyboard {

	public DesktopKeyboardOnderbouw(boolean small) {
	    super(small);
		atEnd(c5); FKey c32 = c5;
		atEnd(c7); FKey c33 = c7;
		disableKey(c8);
		disableKey(c9);
		disableKey(c10);
		//disableKey(c11);
		disableKey(c12);
		disableKey(c19);
		disableKey(c20);
		c25.setHTML("<span class='onderbouw onderbouw-C16'></span>");
		c26.setHTML("<span class='onderbouw onderbouw-C17'></span>");
		c27.setHTML("<span class='onderbouw' style='font-size:16px'>ℝ</span>");
		c28.setHTML("<span class='onderbouw' style='font-size:18px'>∪</span>");
		c29.setHTML("<span class='onderbouw' style='font-size:18px'>÷</span>"); c29.addStyleName("bg-blue");
		c30.setHTML("<span class='onderbouw' style='font-size:18px'>×</span>"); c30.addStyleName("bg-blue");
		c31.setHTML("<span class='onderbouw' style='font-size:18px'>\u2212</span>"); c31.addStyleName("bg-blue");
		c32.setHTML("<span class='onderbouw' style='font-size:18px'>+</span>"); c32.addStyleName("bg-blue");
		c33.setHTML("<span class='onderbouw' style='font-size:18px'>=</span>"); c33.addStyleName("bg-blue");
		//disableKey(c30);
		//disableKey(c31);
	}

	@Override
	protected void disableKey(FKey key) {
		super.disableKey(key);
		atEnd(key);
	}

  private void atEnd(FKey key) {
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
	@Override void onC8(ClickEvent e) {}
	@Override void onC9(ClickEvent e) {}
	@Override void onC10(ClickEvent e) {}
	@Override void onC12(ClickEvent e) {}
	@Override void onC19(ClickEvent e) {}
	@Override void onC20(ClickEvent e) {}
	@Override void onC27(ClickEvent e) { getEditor().insert('ℝ'); }
	@Override void onC28(ClickEvent e) { getEditor().insert('∪'); }
	Key keys = GWT.create(Key.class);
	char slash = keys.slash().charAt(0);
	@Override void onC29(ClickEvent e) { getEditor().insert(slash); }
	@Override void onC30(ClickEvent e) { getEditor().insert('*'); }
	@Override void onC31(ClickEvent e) { getEditor().insert('-'); }
    @Override void onC5(ClickEvent e) { getEditor().insert('+'); }
    @Override void onC7(ClickEvent e) { getEditor().insert('='); }

	@Override
	public void functionKey(int code) {
		if(code >= 6) return;
		if(code == 5)
			getEditor().ndewortel();
		else
			super.functionKey(code);
	}

  @Override
  public int getKeyboardHeight() {
    return HEIGHT;
  }
	
}
