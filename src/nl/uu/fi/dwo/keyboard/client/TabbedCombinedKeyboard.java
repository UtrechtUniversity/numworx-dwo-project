package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class TabbedCombinedKeyboard extends AbstractKeyboard implements ChangeHandler {

	private AbstractKeyboard current;
	private AbstractKeyboard k123;
	private TabletKeyboardABC kabc;
	private TabletKeyboardUpper kABC;
	private TabletKeyboardPen pen;
	private AbstractKeyboard[] stock = new AbstractKeyboard[10];
	
	
	
	private AbstractKeyboard createKeyboard(int i) {
		switch(i) {
		case 0: 
				return new TabletOnderbouwKeyboard(small).init();
				//return new TabletKeyboardOnderbouw();
		case 1: return new TabletKeyboard(small).init();
		case 2: return new TabletKeyboardGonio(small);
		case 3: return new TabletKeyboardStatistiek(small);
		case 4: return new TabletKeyboardMeetkunde(small);

		case 5: return new DesktopKeyboardOnderbouw(small);
		case 6: return new DesktopKeyboard(small).init();
		case 7: return new DesktopKeyboardGonio(small).init();
		case 8: return new DesktopKeyboardStatistiek(small);
		case 9: return new DesktopKeyboardMeetkunde(small).init();

		}
		return new TabletKeyboard(small).init();
	}

	
	TabbedCombinedKeyboard(CombinedState state) {
		this(DEFAULT, false, state);
	}
	
	TabbedCombinedKeyboard(boolean small) {
	  this(DEFAULT, small, null);
	  
	}
	
	final boolean small = false;
	boolean desktop;
	private CombinedState state;

	TabbedCombinedKeyboard(int nr, boolean small, CombinedState state) {
	    //this.small = false;
	    this.state = state;
	    state.addChangeHandler(this);
		AbstractKeyboard tk = createKeyboard(nr);
		stock [nr] = tk;
		this.nr = nr;
		this.k123 = tk;
		this.current = tk;
		main = new FlowPanel();
		main.setStyleName("keyboard-container");
		main.addStyleName("touch");

		Style st = main.getElement().getStyle();
		st.setProperty("transformOrigin", "left");
		st.setProperty("transform", "scaleX(0.96)");
		st.setMarginLeft(70, Unit.PX);

		main.add(tk);
		tk.setDelegate(this);
		kabc = new TabletKeyboardABC(small);
		kabc.setDelegate(this);
		kabc.setVisible(false);
		main.add(kabc);
		kABC = new TabletKeyboardUpper(small);
		kABC.setDelegate(this);
		kABC.setVisible(false);
		main.add(kABC);
		pen = new TabletKeyboardPen(small);
		pen.setDelegate(this);
		pen.setVisible(false);
		main.add(pen);
		initWidget(main);
		resize();
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		setActiveEditor(formuleEditor);
		
		k123.setEditor(formuleEditor);
		kabc.setEditor(formuleEditor);
		kABC.setEditor(formuleEditor);
		pen.setEditor(formuleEditor);
	}

	private boolean hasDesktop() {
		return nr >= 5;
	}
	
	@Override
	public void softFocus() {
		if (hasDesktop()) {
			FocusOnTouch.focus();
			state.setCombined(Combined.TABLET);
		} else {
			tabletfocus();
			state.setCombined(Combined.TABLET_ACTIVE_SOFT);
		}
	}
	
	@Override
	public void focus() {
		if (hasDesktop()) {
			super.focus();
			resizeScrollPanel(getKeyboardHeight());
			FocusOnTouch.focus();
			state.setCombined(Combined.DESKTOP_ACTIVE);
		} else {
			tabletfocus();
			state.setCombined(Combined.TABLET_ACTIVE);
		}
	}


	private void tabletfocus() {
		if(current==pen) pen.focus(); // does read formula
		super.focus();
		resize();
		FocusOnTouch.focus();
	}

	private void resize() {
		if( isVisible())
		{
			resizeScrollPanel(getKeyboardHeight());
		}
		setHeight(getKeyboardHeight() + "px");
	}

	@Override
	public void blur() {
		super.blur();
		resizeScrollPanel(0);
		state.setCombined(Combined.NONE);
	}


	boolean upper;
	@Override
	void switchABC() {
		if(upper) switchUpper(); 
		else switchLower();
		resize();
	}

	@Override
	void switch123() {
		if(current != k123) {
			current.setVisible(false);
			current = k123;
			current.setVisible(true);
			resize();
		}
	}

	@Override
	void switchHand() {
		if(current != pen) {
			current.setVisible(false);
			current = pen;
			current.focus(); // does read formula
			resize();
		}
	}

	@Override
	void switchUpper() {
		if(current != kABC) {
			current.setVisible(false);
			current = kABC;
			upper = true;
			current.setVisible(true);
		}
	}

	@Override
	void switchLower() {
		if(current != kabc) {
			current.setVisible(false);
			current = kabc;
			upper = false;
			current.setVisible(true);
		}
	}

	private HasHeight scrollPanel; 
	private int origHeight = 426 - 40;
	private int origDelta = 0;
	private int nr;
	private FlowPanel main;
	
	void resizeScrollPanel(int size) {
		origDelta = size;
		if(scrollPanel != null)
			scrollPanel.setHeight(origHeight - size);
	}

	@Override
	public void setScrollPanel(HasHeight w, int h) {
		scrollPanel = w;
		origHeight = h;
		if(scrollPanel != null) scrollPanel.setHeight(origHeight - origDelta);
	}

	public int getKeyboardHeight() {
		return current.getKeyboardHeight();
	}

	private boolean isDesktopKeyboard() {
		Combined state = this.state.getCombined();
		boolean isTablet = state == Combined.TABLET_ACTIVE || state == Combined.TABLET_ACTIVE_SOFT;
		return !isTablet;
	}

	
	@Override
	public void setKeyboard(int nr) {
		if(nr < 0 || nr > 4) nr = DEFAULT;
		if (isDesktopKeyboard()) nr = nr + 5;
		if(this.nr != nr) {
			main.setStyleName("touch", !isDesktopKeyboard());
			main.setStyleName("computer", isDesktopKeyboard());
			boolean iscurrent = current == k123;
			boolean shown = k123.isVisible() && iscurrent;
			if(stock[nr] == null) stock[nr] = createKeyboard(nr);
			this.nr = nr;
			k123.removeFromParent();
			k123 = stock[nr];
			k123.setEditor(getEditor());
			k123.setDelegate(this);
			k123.setVisible(shown);
			if(iscurrent) current = k123;
			main.add(k123);
			if (shown) resize();
		}
	}


	@Override
	public void setWriteMathSet(int nr) {
		pen.setWriteMathSet(nr);
	}

	private EnterType type = EnterType.APPLY;

	@Override
	public void setEnterType(EnterType type) {
		EnterType old = this.type;
		this.type = type;
		if ( old != type ) {
			// actie
			switch(type) {
			case APPLY: switch123(); break;
			case ENTER: switchABC(); break;
			}
		}
	}


	@Override
	public void onChange(ChangeEvent event) {
		Combined combined = state.getCombined();
		GWT.log("change to " + combined);
		if (Combined.NONE == combined) return;
		if (hasDesktop()) {
			if(combined == Combined.TABLET_ACTIVE||combined==Combined.TABLET_ACTIVE_SOFT) {
				setKeyboard(nr-5);
				tabletfocus();
			}
		} else { 
			if (combined == Combined.DESKTOP_ACTIVE) {
				setKeyboard(nr);
				switch123();
			} else if (combined == Combined.TABLET) {
				setKeyboard(nr);
				switch123();
				setVisible(false);
				resizeScrollPanel(0);
				FocusOnTouch.focus();
			}
		}
	}

}
