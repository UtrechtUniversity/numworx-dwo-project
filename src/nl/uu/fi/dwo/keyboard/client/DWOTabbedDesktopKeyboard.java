package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class DWOTabbedDesktopKeyboard extends AbstractKeyboard {

	private AbstractKeyboard k123, current, stock[] = new AbstractKeyboard[5];
	private int nr;
	private FlowPanel main;
	private DWOTabletKeyboardGrUpper grupper;
	private DWOTabletKeyboardGrLower grlower;
	private DWOMathKeyboard math;

	DWOTabbedDesktopKeyboard(int nr) {
		k123 = current = createKeyboard(nr);
		stock[nr] = current;
		main = new FlowPanel();
		initWidget(main);
		main.setStyleName("keyboard-container");
		main.addStyleName("touch");
		main.add(current);
		current.setDelegate(this);
//		grupper = new DWOTabletKeyboardGrUpper();
//		grupper.setDelegate(this);
//		grlower = new DWOTabletKeyboardGrLower();
//		disableKey(grlower.pad.t3_16);
//		disableKey(grlower.pad.t4_16);
//		disableKey(grupper.pad.t3_16);
//		disableKey(grupper.pad.t4_16);
//		
//		grlower.setDelegate(this);
//		grupper.setVisible(false);
//		main.add(grupper);
//		grlower.setVisible(false);
//		main.add(grlower);
		math = new DWOMathKeyboard().init();
		math.setVisible(false);
		math.setDelegate(this);
		disableKey(math.pad.t3_16);
		disableKey(math.pad.t4_16);
		main.add(math);
		
		setPixelSize(-1, getKeyboardHeight());
	}

	public DWOTabbedDesktopKeyboard() {
		this(1);
	}


	@Override
	public void setKeyboard(int nr) {
		if(nr < 0 || nr > 4) nr = DEFAULT;
		if(this.nr != nr) {
			boolean isCurrent = k123 == current;
			boolean isShown = current.isVisible() && isCurrent;
			k123.removeFromParent();
			if(stock[nr] == null) stock[nr] = createKeyboard(nr);
			this.nr = nr;
			k123 = stock[nr];
			k123.setEditor(getEditor());
			k123.setDelegate(this);
			main.add(k123);
			k123.setVisible(isShown);
			if(isCurrent) current = k123;
			resizeScrollPanel(getKeyboardHeight());
		}
	}
	
	private AbstractKeyboard createKeyboard(int i) {
		switch(i) {
//		case 0: return new DesktopKeyboardOnderbouw();
		default:
		case 1: return new DWODesktopKeyboard().init();
//		case 2: return new DesktopKeyboardGonio().init();
//		case 3: return new DesktopKeyboardStatistiek();
//		case 4: return new DesktopKeyboardMeetkunde().init();
		}
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		setActiveEditor(formuleEditor);
		k123.setEditor(formuleEditor);
//		grupper.setEditor(formuleEditor);
//		grlower.setEditor(formuleEditor);
		math.setEditor(formuleEditor);
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

	int getKeyboardHeight() {
		return current.getKeyboardHeight();
	}

	@Override
	public void focus() {
		super.focus();
		resizeScrollPanel(getKeyboardHeight());
		FocusOnTouch.focus();
	}

	@Override
	public void softFocus() {
		FocusOnTouch.focus();
	}


	@Override
	public void blur() {
		super.blur();
		resizeScrollPanel(0);
	}

	private boolean upper;
	
	@Override
	public void switchGreek() {
		switchTo(math);
//		if(upper) switchUpper();
//		else switchLower();
	}

	public void switchUpper() {
		upper = true;
		switchTo(grupper);
	}
	
	public void switchLower() {
		upper = false;
		switchTo(grlower);
	}
	
	public void switch123() {
		switchTo(k123);		
	}

	private void switchTo(AbstractKeyboard kto) {
		if(current != kto) {
			current.setVisible(false);
			current = kto;
			current.setVisible(true);
			resizeScrollPanel(getKeyboardHeight());
		}
	}

	public void switchABC() {
		switchGreek();
	}
	
	
}
