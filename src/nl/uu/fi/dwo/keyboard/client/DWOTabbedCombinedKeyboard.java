package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

class DWOTabbedCombinedKeyboard extends AbstractKeyboard implements ChangeHandler {

	//private final boolean premium;
	private final CombinedState state;
	private Combined combined = Combined.NONE;
	private boolean isDesktop = true, soft;
	private AbstractKeyboard current, desktop, tablet;
	private FlowPanel main;
	
	
	public DWOTabbedCombinedKeyboard(boolean premium, CombinedState state) {
		//this.premium = premium;
		this.state = state;
		if (state != null) {
			state.addChangeHandler(this);
			combined = state.getCombined();
		}
		main = new FlowPanel(); 
		main.setStyleName("keyboard-container");
		main.addStyleName("touch");
		initWidget(main);
		
		current = desktop = new DWODesktopKeyboard().init();
		current.setPremium(premium);
		current.setDelegate(this);

		tablet = new DWOTabletKeyboard().init();
		tablet.setPremium(premium);
		tablet.setDelegate(this);
		tablet.setVisible(false);
		
		main.add(desktop);
		main.add(tablet);

		if (combined == Combined.TABLET_ACTIVE) {
			desktop.setVisible(false);
			tablet.setVisible(true);
			current = tablet;
			isDesktop = false;
		}
	}

	@Override
	int getKeyboardHeight() {
		if(isVisible())
			return current.getKeyboardHeight();
		return 0;
	}

	@Override
	public void onChange(ChangeEvent event) {
		Combined old = combined;
		combined = state.getCombined();
		if (old != combined) {
			if (combined == Combined.TABLET_ACTIVE && isDesktop) {
				isDesktop = false;
				desktop.setVisible(false);
				tablet.setVisible(true);
				setVisible(true);
				current = tablet;
				resizeScrollPanel(getKeyboardHeight());
			} else 
			if(combined == Combined.DESKTOP_ACTIVE && !isDesktop) { // FOCUS
				if (soft) { setCombined(Combined.TABLET); } // redirect to softfocus
				isDesktop = false;
				desktop.setVisible(true);
				tablet.setVisible(false);
				setVisible(!soft); // force soft
				current = desktop;
				resizeScrollPanel(getKeyboardHeight()); 					
			} else
				if(combined == Combined.TABLET && !isDesktop) { // SOFT FOCUS
					isDesktop = false;
					desktop.setVisible(true);
					tablet.setVisible(false);
					setVisible(false);
					current = desktop;
					resizeScrollPanel(getKeyboardHeight()); 					
				} 
		}
		
	}

	void setCombined(Combined c) {
		combined = c;
		if (state != null) state.setCombined(c);
	}

	private HasHeight scrollPanel; 
	private int origHeight = 426 - 40;
	private int origDelta = 0;
	
	void resizeScrollPanel(int size) {
		setPixelSize(-1, size);
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

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.keyboard.client.AbstractKeyboard#focus()
	 */
	@Override
	public void focus() {
		// if(current==pen) pen.focus(); // does read formula
		super.focus();
		soft = false;
		resizeScrollPanel(getKeyboardHeight());
		FocusOnTouch.focus();
		if (isDesktop) {
			setCombined(Combined.DESKTOP_ACTIVE);
		} else {
			setCombined(Combined.TABLET_ACTIVE);
		}
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.keyboard.client.AbstractKeyboard#softFocus()
	 */
	@Override
	public void softFocus() {
		FocusOnTouch.focus();
		soft = true;
		if (isDesktop) {
			setVisible(false);
			setCombined(Combined.TABLET);
		} else {
			setVisible(true);
			setCombined(Combined.TABLET_ACTIVE);
		}
		resizeScrollPanel(getKeyboardHeight());
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.keyboard.client.AbstractKeyboard#blur()
	 */
	@Override
	public void blur() {		
		super.blur();
		setCombined(Combined.NONE);
	}
	
	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		setActiveEditor(formuleEditor);
		desktop.setEditor(formuleEditor);
		tablet.setEditor(formuleEditor);
//		grupper.setEditor(formuleEditor);
//		grlower.setEditor(formuleEditor);
//		math.setEditor(formuleEditor);
//		pen.setEditor(formuleEditor);
	}

	
}
