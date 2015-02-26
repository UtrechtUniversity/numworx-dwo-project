package nl.uu.fi.dwo.mobile.client.ui.noordhoffkb;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.OsDetection;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.keyboard.client.DesktopKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.KeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.TabletKeyboardFactory;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

public class NoordhoffKeyboard extends SimplePanel implements StatusBarIF {

	KeyboardFactory factory;
	
	public NoordhoffKeyboard() {
		OsDetection detection = MGWT.getOsDetection();
		if(detection.isDesktop()
				//&& false
				) {
			factory = new DesktopKeyboardFactory();
		} else {
			factory = new TabletKeyboardFactory();
		}

	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void addKnop(PushButton kijkNaButton, boolean b) {
	}

	@Override
	public void addNavPanel(Panel onp) {
	}

	@Override
	public void setScrollPanel(Widget w, int h) {
		// TODO Auto-generated method stub
	}

	@Override
	public void zetMaat() {
	}

	@Override
	public void zetMaatNoordhoff() {
	}

	@Override
	public void zetMaatTrifork() {
	}

	@Override
	public int getStatusBarHeight() {
		return 0;
	}

	@Override
	public FormuleKeyboardIF getFormuleKeyboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		// TODO Auto-generated method stub
		return null;
	}

}
