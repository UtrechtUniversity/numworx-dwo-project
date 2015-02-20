package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class KeyboardGWT implements EntryPoint {

	@Override
	public void onModuleLoad() {
		DesktopKeyboard panel = new DesktopKeyboard();
		RootPanel.get().add(panel);

	}

}
