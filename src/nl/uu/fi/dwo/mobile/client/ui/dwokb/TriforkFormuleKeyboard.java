package nl.uu.fi.dwo.mobile.client.ui.dwokb;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;

import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

public class TriforkFormuleKeyboard extends FormuleKeyboard implements
		StatusBarIF {

	public TriforkFormuleKeyboard() {
	}

	public void zetMaat() {
		tp.zetMaatTrifork();
	}

	@Override
	public void addNavPanel(Panel opdrnav) {
	}

	@Override
	public void addKnop(PushButton knop, boolean right) {
	}

	@Override
	public int getStatusBarHeight() {
		return 0;
	}

	
}
