package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public interface StatusBarIF extends IsWidget {

	void addKnop(PushButton kijkNaButton, boolean b);
	void addNavPanel(Panel onp);
	void setScrollPanel(AbstractKeyboard.HasHeight w, int h);
	void setKeyboard(int nr);
	void setWriteMathSet(int nr);

	void zetMaat();
	int  getStatusBarHeight();
	
	FormuleKeyboardIF getFormuleKeyboard();
	FormuleClipboardIF getFormuleClipboard();

	void showScore(ScoreNavIF scoreNav);

}
