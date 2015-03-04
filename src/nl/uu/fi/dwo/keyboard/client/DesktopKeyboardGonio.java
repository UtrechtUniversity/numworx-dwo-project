package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardGonio extends DesktopKeyboard {

	public DesktopKeyboardGonio() {
		c25.setHTML("<span class='gonio gonio-C25'></span>");
		c26.setHTML("<span class='gonio gonio-C26'></span>");
		c27.setHTML("<span class='gonio gonio-C27'><span class='path1'><span class='path2'></span></span>");
		c28.setHTML("<span class='onderbouw onderbouw-C17'></span>");
		disableKey(c30);
		disableKey(c31);		
	}

	public AbstractKeyboard init() {
		disableKey(c29);
		return this;
	}
	
	@Override
	void onC25(ClickEvent e) {
		getEditor().insert('\u03C0');
	}
	@Override
	void onC26(ClickEvent e) {
		getEditor().insert("∠");
	}

	private AlphaKeys alphaKeys;
	
	@Override
	void onC27(ClickEvent e) {
		if(alphaKeys == null) {
			alphaKeys = new AlphaKeys();
		}
		if (alphaKeys.isAlphaShown()) {
			alphaKeys.hideAlpha();
		} else {
			alphaKeys.setEditor(getEditor());
			int x = c27.getAbsoluteLeft()-184/2+24/2;
			int y = c27.getAbsoluteTop()-44;
			alphaKeys.showAlfa(x, y);
		}
	}

	@Override
	void onC28(ClickEvent e) {
		getEditor().insert("°");	
	}

	@Override
	void onC29(ClickEvent e) {
	}

	@Override
	void onC30(ClickEvent e) {
	}

	@Override
	void onC31(ClickEvent e) {
	}

}
