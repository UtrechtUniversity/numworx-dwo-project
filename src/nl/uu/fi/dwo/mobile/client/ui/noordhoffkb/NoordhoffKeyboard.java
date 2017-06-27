package nl.uu.fi.dwo.mobile.client.ui.noordhoffkb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.OsDetection;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.keyboard.client.DesktopKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.KeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.TabletKeyboardFactory;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

public class NoordhoffKeyboard extends SimplePanel implements StatusBarIF, FormuleClipboardIF {

	KeyboardFactory factory;
	AbstractKeyboard kb;
	
	public NoordhoffKeyboard() {
		setStylePrimaryName("noordhoff");
		OsDetection detection = MGWT.getOsDetection();
		if(detection.isDesktop()
				//&& false
				) {
			factory = new DesktopKeyboardFactory();
		} else {
			factory = new TabletKeyboardFactory();
		}
		kb = factory.getKeyboard();
		setWidget(kb);
		kb.blur(); // we start hidden!

// css style!		
		Style style = getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		style.setBottom(0, Style.Unit.PX);
		style.setWidth(100, Style.Unit.PCT);

	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void addKnop(Widget kijkNaButton, boolean b) {
	}

	@Override
	public void addNavPanel(Panel onp) {
	}

	@Override
	public void setScrollPanel(HasHeight w, int h) {
		kb.setScrollPanel(w,h);
	}

	@Override
	public void zetMaat() {
	}

	@Override
	public int getStatusBarHeight() {
		return 0;
	}

	@Override
	public FormuleKeyboardIF getFormuleKeyboard() {
		return kb;
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		return this;
	}

	private String clipboard = "";
	@Override
	public String getClipboard() {
		return clipboard;
	}

	@Override
	public void setClipboard(String formule) {
		clipboard = formule;
	}

	@Override
	public void setKeyboard(int nr) {
		kb.setKeyboard(nr);
	}

	@Override
	public void setWriteMathSet(int nr) {
		kb.setWriteMathSet(nr);
	}

	@Override
	public void showScore(ScoreNavIF scoreNav) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLabel(Label label)
	{
		// TODO Auto-generated method stub
		
	}

}
