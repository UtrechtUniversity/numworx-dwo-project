package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;

public class DWOTabletKeyboardFactory implements KeyboardFactory {

	public DWOTabletKeyboardFactory() {
	}

	@Override
	public AbstractKeyboard getKeyboard() {
		//return new TabbedTouchKeyboard(new TabletKeyboardStatistiek());
		return new DWOTabbedTouchKeyboard();
	}

	static public final DWOkeyboardBundle resources = GWT.create(DWOkeyboardBundle.class);
}
