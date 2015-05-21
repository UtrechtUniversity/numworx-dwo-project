package nl.uu.fi.dwo.mobile.client.ui.dwokb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
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

public class DWOKeyboard extends FlowPanel implements StatusBarIF, FormuleClipboardIF {

	KeyboardFactory factory;
	AbstractKeyboard kb;
	FlowPanel staticPanel;
	
	public DWOKeyboard() {
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
		staticPanel = new FlowPanel();
		add(staticPanel);
		kb.blur(); // we start hidden!

// css style!		
		Style style = getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		style.setBottom(0, Style.Unit.PX);
		style.setRight(0, Style.Unit.PX);
		style.setLeft(0, Style.Unit.PX);
		style.setBackgroundColor("rgb(210,210,210)");
		
		style = staticPanel.getElement().getStyle();
		style.setHeight(getStatusBarHeight(), Unit.PX);
		style.setWidth(100, Unit.PCT);
		style.setBackgroundImage("url("
				+ DWOplayer.PARAMETERS.getResource("images/resources/footerbgimage.png")
				+ ")");

		style = kb.getElement().getStyle();
		style.setProperty("margin", "0 auto");
		style.setWidth(882, Unit.PX);
		style.setPosition(Position.RELATIVE);
		
//		staticPanel.addDomHandler(new MouseUpHandler() {
//
//			@Override
//			public void onMouseUp(MouseUpEvent event) {
//				showScore(null);
//				
//			}} , MouseUpEvent.getType());
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void addKnop(PushButton knop, boolean right) {
		if(knop == null) return;
		Style style = knop.getElement().getStyle();
		if(right)
			style.setFloat(Style.Float.RIGHT);
		else
			style.setFloat(Style.Float.LEFT);
		style.setDisplay(Display.INLINE_BLOCK);
		style.setMarginTop(10, Style.Unit.PX);
		style.setWidth(80 ,Style.Unit.PX);
		style.setProperty("horizontalAlign", "center"); //TODO: helpt dit?
		staticPanel.add(knop);
	}

	@Override
	public void addNavPanel(Panel opdrnav) {
		opdrnav.getElement().getStyle().setFloat(Style.Float.LEFT);
		opdrnav.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		staticPanel.clear();
		staticPanel.add(opdrnav);
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
		return KeyBoardTabPanel.KEYB_STATIC_HEIGHT;
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

	Timer scoreTimer;
	@Override
	public void showScore(ScoreNavIF scoreNav) {
//		final Style style = staticPanel.getElement().getStyle();
//		style.setHeight(getStatusBarHeight()*2, Unit.PX);
//		scoreTimer = new Timer() {
//			
//			@Override
//			public void run() {
//				scoreTimer = null;
//				style.setHeight(getStatusBarHeight(), Unit.PX);
//			}
//		};
//		scoreTimer.schedule(2000);
	}

}
