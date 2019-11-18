package nl.uu.fi.dwo.mobile.client.ui.dwokb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.OsDetection;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.keyboard.client.DWODesktopKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.DWOTabletKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.KeyboardFactory;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerCss;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

public class DWOKeyboard extends FlowPanel implements StatusBarIF, FormuleClipboardIF {

	private int STATUS_BAR_HEIGHT = KeyBoardTabPanel.KEYB_STATIC_HEIGHT;
    KeyboardFactory factory;
	AbstractKeyboard kb;
	FlowPanel staticPanel;
	private HasHeight scrollPanel;
	private static DWOplayerCss dwoplayercss = DWOplayer.DWO_BUNDLE.dwoplayercss();
	
	public DWOKeyboard() {
		setStylePrimaryName("dwo");
		OsDetection detection = MGWT.getOsDetection();
		if(detection.isDesktop() && !TouchStartEvent.isSupported()
				//&& false // voor tablet keyboard deze uitcommentarieren
				) {
			factory = new DWODesktopKeyboardFactory();
		} else {
			factory = new DWOTabletKeyboardFactory();
		}
		java.util.logging.Logger.getLogger("DWOKeyboard").info("Keyboard " + DWOplayer.isPremium());
		
		factory.setPremium(DWOplayer.isPremium()); // inject premium feature
		kb = factory.getKeyboard();
		add(kb);
		staticPanel = new FlowPanel();
		add(staticPanel);
		kb.blur(); // we start hidden!

// css style! FIXME naar dwoplayercss
		Style style;
		addStyleName(dwoplayercss.DWOkeyboard());;
		
		style = staticPanel.getElement().getStyle();
		style.setHeight(getStatusBarHeight(), Unit.PX);
		style.setWidth(100, Unit.PCT);
		style.setBackgroundColor("rgb(255,255,255)");

		style = kb.getElement().getStyle();
		style.setProperty("margin", "0 auto");
		style.setWidth(1024, Unit.PX); // MAXIMUM BREEDTE TOETSENBORDEN
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
	public void addKnop(Widget knop, boolean right) {
		if(knop == null) return;
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
	  scrollPanel = w;
	  kb.setScrollPanel(w,h);
	}

	@Override
	public void zetMaat() {
	}

	@Override
	public int getStatusBarHeight() {
		return STATUS_BAR_HEIGHT;
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

	//private Timer scoreTimer;
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

	@Override
	public void addLabel(Label label)
	{
		if (label == null) return;
		
		label.setStyleName(dwoplayercss.navigatiebalkLabel(), true);
		staticPanel.add(label);
	}

  @Override
  public void hide() {
    STATUS_BAR_HEIGHT=0;
    staticPanel.removeFromParent();
    kb.setScrollPanel(scrollPanel, 0);
    
  }

}
