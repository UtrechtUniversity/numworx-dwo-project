package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

class DWOTabbedCombinedKeyboard extends AbstractKeyboard implements ChangeHandler {

	//private final boolean premium;
	private final CombinedState state;
	private Combined combined = Combined.NONE;
	private boolean isDesktop = true;
	private AbstractKeyboard current, desktop, tablet, math, pen, kabc, kABC, kGrUpper, kGrLower;
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
		main.addStyleName("combined");
		main.getElement().getStyle().setPaddingLeft(70, Unit.PX);
		initWidget(main);
		
		current = desktop = new DWODesktopKeyboard().init();
		current.setPremium(premium);
		current.setDelegate(this);

		tablet = new DWOTabletKeyboard().init();
		tablet.setPremium(premium);
		tablet.setDelegate(this);
		tablet.setVisible(false);
		
		math = new DWOMathKeyboard().init();
		math.setVisible(false);
		math.setKeyboard(1);
		math.setDelegate(this);

		pen = new DWOTabletKeyboardPen(593+200+108-70); // 70 px minder voor switchvak
		pen.setDelegate(this);
		pen.setVisible(false);
		main.add(pen);

		kabc = new DWOTabletKeyboardABC();
		kabc.setDelegate(this);
		kabc.setVisible(false);
		main.add(kabc);
		kABC = new DWOTabletKeyboardUpper();
		kABC.setDelegate(this);
		kABC.setVisible(false);
		main.add(kABC);

		kGrUpper = new DWOTabletKeyboardGrUpper();
		kGrUpper.setDelegate(this);
		kGrUpper.setVisible(false);
		main.add(kGrUpper);
		kGrLower = new DWOTabletKeyboardGrLower();
		kGrLower.setDelegate(this);
		kGrLower.setVisible(false);
		main.add(kGrLower);
		
		main.add(desktop);
		main.add(tablet);
		main.add(math);
		main.add(pen);

		if (combined == Combined.TABLET_ACTIVE || combined == Combined.TABLET_ACTIVE_SOFT) {
			desktop.setVisible(false);
			tablet.setVisible(true);
			current = tablet;
			isDesktop = false;
		}
	}

	@Override
	public int getKeyboardHeight() {
		if(isVisible())
			return current.getKeyboardHeight();
		return 0;
	}

	@Override
	public void onChange(ChangeEvent event) {
		Combined old = combined;
		combined = state.getCombined();
		if (old != combined) {
			if ((combined == Combined.TABLET_ACTIVE || combined == Combined.TABLET_ACTIVE_SOFT) && isDesktop) {
				isDesktop = false;
				switchTo(tablet);
				setVisible(true);
				resizeScrollPanel(getKeyboardHeight());
			} else 
			if(combined == Combined.DESKTOP_ACTIVE && !isDesktop) { // FOCUS
				isDesktop = true;
				switchTo(desktop);
				setVisible(true); // force soft
				resizeScrollPanel(getKeyboardHeight()); 					
			} else
			if(combined == Combined.TABLET && !isDesktop) { // SOFT FOCUS
				isDesktop = true;
				switchTo(desktop);
				setVisible(false);
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
		if(current==pen) pen.focus(); // does read formula
		super.focus();
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
		if (isDesktop) {
			setVisible(false);
			setCombined(Combined.TABLET);
		} else {
			setVisible(true);
			setCombined(Combined.TABLET_ACTIVE_SOFT);
		}
		resizeScrollPanel(getKeyboardHeight());
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.keyboard.client.AbstractKeyboard#blur()
	 */
	@Override
	public void blur() {		
		super.blur();
		resizeScrollPanel(getKeyboardHeight());
		setCombined(Combined.NONE);
	}
	
	int nr = 1;
	private boolean upper;
	@Override
	public void setKeyboard(int nr) {
		if(nr < 0 || nr > 4) nr = DEFAULT;
		if(this.nr != nr) {
			this.nr = nr;
			math.setKeyboard(nr);
			desktop.setKeyboard(nr);
			tablet.setKeyboard(nr);
			resizeScrollPanel(getKeyboardHeight());
		}
	}
	
	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		setActiveEditor(formuleEditor);
		desktop.setEditor(formuleEditor);
		tablet.setEditor(formuleEditor);
		kGrUpper.setEditor(formuleEditor);
		kGrLower.setEditor(formuleEditor);
		kabc.setEditor(formuleEditor);
		kABC.setEditor(formuleEditor);
		math.setEditor(formuleEditor);
		pen.setEditor(formuleEditor);
	}

	private void switchTo(AbstractKeyboard kto) {
		if(current != kto) {
			current.setVisible(false);
			current = kto;
			current.setVisible(true);
			resizeScrollPanel(getKeyboardHeight());
		}
	}

	@Override
	void switchGreek() {		
		if(current != math)
		{	
			switchTo(math);
		} else {
			if(upper)
				switchGrUpper();
			else
				switchGrLower();
		}
	}

	public void switch123() {
		switchTo(isDesktop?desktop:tablet);		
	}
	public void switchHand() {
		switchTo(pen);		
	}

	@Override
	void switchABC() {
		if(upper) switchLtUpper(); 
		else switchLtLower();
	}

	@Override
	void switchLower() {
		if(current == kGrUpper)
			switchGrLower();
		else
			switchLtLower();
	}

	@Override
	void switchUpper() {
		if(current == kGrLower)
			switchGrUpper();
		else
			switchLtUpper();
	}

	private void switchLtUpper() {
		upper = true;
		switchTo(kABC);
	}

	private void switchLtLower() {
		upper = false;
		switchTo(kabc);
	}

	private void switchGrUpper() {
		upper = true;
		switchTo(kGrUpper);
	}

	private void switchGrLower() {
		upper = false;
		switchTo(kGrLower);
	}

	private Object enterImage;
	
	@Override
	void setEnterImage(ImageResource resource) {
		if(resource != enterImage) {
			enterImage = resource;
			desktop.setEnterImage(resource);
			tablet.setEnterImage(resource);
			kabc.setEnterImage(resource);
			kABC.setEnterImage(resource);
			pen.setEnterImage(resource);
			kGrUpper.setEnterImage(resource);
			kGrLower.setEnterImage(resource);
			math.setEnterImage(resource);
		}
	}
 
	@Override
    void setEnterImage(DataResource resource) {
        if(resource != enterImage) {
            enterImage = resource;
            desktop.setEnterImage(resource);
            tablet.setEnterImage(resource);
            kabc.setEnterImage(resource);
            kABC.setEnterImage(resource);
            pen.setEnterImage(resource);
            kGrUpper.setEnterImage(resource);
            kGrLower.setEnterImage(resource);
            math.setEnterImage(resource);
        }
    }

	// FIXME maak goed voor isdesktop?
	
	@Override
	public void setEnterType(EnterType type) {
		
		boolean isEnter = enterImage == DWOTabletKeyboardFactory.resources.enter_svg();
		switch(type) {
		case ENTER:
			DataResource resource_svg = DWOTabletKeyboardFactory.resources.enter_svg();
			if (!isEnter) switchABC();
			setEnterImage(resource_svg);
			return;
		default:	
		case APPLY:
			resource_svg = DWOTabletKeyboardFactory.resources.apply_svg();
			if (isEnter) switch123();
			setEnterImage(resource_svg);
			return;
		}
	}
	
}
