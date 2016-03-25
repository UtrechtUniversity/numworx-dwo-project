package nl.uu.fi.dwo.mobile.client.ui.dwokb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
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
import nl.uu.fi.dwo.keyboard.client.DWODesktopKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.DWOTabletKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.DesktopKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.KeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.TabletKeyboardFactory;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

public class CitrusKeyboard extends FlowPanel implements StatusBarIF, FormuleClipboardIF {

	KeyboardFactory factory;
	AbstractKeyboard kb;
	
	public CitrusKeyboard() {
		OsDetection detection = MGWT.getOsDetection();
		if(detection.isDesktop()
				//&& false
				) {
			factory = new DWODesktopKeyboardFactory();
		} else {
			factory = new DWOTabletKeyboardFactory();
		}
		kb = factory.getKeyboard();
		add(kb);
		kb.blur(); // we start hidden!

// css style!		
		Style style = getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		style.setBottom(0, Style.Unit.PX);
		style.setRight(0, Style.Unit.PX);
		style.setLeft(0, Style.Unit.PX);
		style.setBackgroundColor("rgb(210,210,210)");
		
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void addKnop(Widget knop, boolean right) {
	}

	@Override
	public void addNavPanel(Panel opdrnav) {
	}

	@Override
	public void setScrollPanel(AbstractKeyboard.HasHeight w, int h) {
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
	}

	@Override
	public void addLabel(Label label)
	{
		// TODO Auto-generated method stub
		
	}

}
