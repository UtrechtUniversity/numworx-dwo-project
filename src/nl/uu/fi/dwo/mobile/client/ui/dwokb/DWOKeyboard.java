package nl.uu.fi.dwo.mobile.client.ui.dwokb;

import java.util.Objects;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.keyboard.client.Combined;
import nl.uu.fi.dwo.keyboard.client.CombinedState;
import nl.uu.fi.dwo.keyboard.client.DWOCombinedKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.KeyboardFactory;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerCss;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.utils.CopyToClipboard;

public class DWOKeyboard extends FlowPanel implements StatusBarIF, FormuleClipboardIF, CombinedState, RequiresResize {
	private static final ChangeEvent CHANGE_EVENT = new ChangeEvent() {};

	static final int KEYB_STATIC_HEIGHT = 44;

	private int STATUS_BAR_HEIGHT = KEYB_STATIC_HEIGHT;
    final KeyboardFactory factory;
	AbstractKeyboard kb;
	FlowPanel staticPanel;
	private HasHeight scrollPanel;
	private static DWOplayerCss dwoplayercss = DWOplayer.DWO_BUNDLE.dwoplayercss();
		
	public DWOKeyboard(ActivityComponent activity) {
		setStylePrimaryName("dwo");
		
		factory = new DWOCombinedKeyboardFactory();
		factory.setCombinedState(this);
		
		staticPanel = new FlowPanel();
				
		java.util.logging.Logger.getLogger("DWOKeyboard").info("Keyboard " + activity.isPremium() + " " + state);
		
		factory.setPremium(activity.isPremium()); // inject premium feature

// css style! FIXME naar dwoplayercss
		Style style;
		addStyleName(dwoplayercss.DWOkeyboard());;
		
		style = staticPanel.getElement().getStyle();
		style.setHeight(getStatusBarHeight(), Unit.PX);
		style.setWidth(100, Unit.PCT);
		style.setBackgroundColor("rgb(255,255,255)");

		
//		staticPanel.addDomHandler(new MouseUpHandler() {
//
//			@Override
//			public void onMouseUp(MouseUpEvent event) {
//				showScore(null);
//				
//			}} , MouseUpEvent.getType());
	}

	public boolean isDesktopKeyboard() {
		boolean isTablet = state == Combined.TABLET_ACTIVE || state == Combined.TABLET_ACTIVE_SOFT;
		return !isTablet;
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
		if (kb == null) {
			kb = factory.getKeyboard();
			Style style = kb.getElement().getStyle();
			//style.setProperty("margin", "0 auto");
			style.setWidth(1024, Unit.PX); // MAXIMUM BREEDTE TOETSENBORDEN
			style.setPosition(Position.RELATIVE);
			add(kb);
			add(staticPanel);
			kb.blur(); // we start hidden!		
		}
		return kb;
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		return this;
	}

	private String clipboard = null;

	Storage storage = Storage.getLocalStorageIfSupported();
	
	private Combined state = Combined.NONE;
	private static final String CLIPBOARD = "nl.numworx.clipboard";

	@Override
	public String getClipboard() {
		if (storage != null) {
			String c = storage.getItem(CLIPBOARD);
			if (c != null) clipboard = c;
		}
		return Objects.toString(clipboard,"");
	}

	@Override
	public void setClipboard(String formule) {
		
		clipboard = Objects.toString(formule, "");
		if (storage != null) {
			try {
				storage.setItem(CLIPBOARD, clipboard);
			} catch(Throwable t) {
				storage = null; // jammer dan.
			}
		}
		CopyToClipboard.setText(clipboard);
	}

	@Override
	public void setKeyboard(int nr) {
		kb.setKeyboard(nr);
	}
	@Override
	public void setSoortKeyboard(int soort) {
		kb.setSoortKeyboard(soort);
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

  	ChangeHandler h;

	private RequiresResize rr = () -> {};
	
	@Override
	public void setOnResize(RequiresResize rr) {
		this.rr = rr;
	}
	
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		h = handler;
		return () -> {h = null;};
	}
	
	@Override
	public void setCombined(Combined state) {
		this.state = state;
		if (h != null) h.onChange(CHANGE_EVENT);
	}
	
	@Override
	public Combined getCombined() {
		return state;
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.ui.StatusBarIF#setCombinedState(nl.uu.fi.dwo.keyboard.client.CombinedState)
	 */
	@Override
	public void setCombinedState(CombinedState state) {
		setCombined(state.getCombined());
		factory.setCombinedState(state);
	}

	@Override
	public int getWidth() {
		return 70;
	}

	@Override
	public void onResize() {
		kb.onResize();
		rr.onResize();
	}

}
