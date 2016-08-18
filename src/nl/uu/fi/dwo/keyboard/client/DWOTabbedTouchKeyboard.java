package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class DWOTabbedTouchKeyboard extends AbstractKeyboard {

	private static int HEIGHT = 166;
	private AbstractKeyboard current;
	private AbstractKeyboard k123;
	private DWOTabletKeyboardABC kabc;
	private DWOTabletKeyboardUpper kABC;
	private DWOTabletKeyboardGrUpper kGrUpper;
	private DWOTabletKeyboardGrLower kGrLower;
	private DWOMathKeyboard math;
	private DWOTabletKeyboardPen pen;
	private AbstractKeyboard[] stock = new AbstractKeyboard[5];
	
	
	
	private AbstractKeyboard createKeyboard(int i) {
		switch(i) {
//		case 0: return new TabletKeyboardOnderbouw();
		default: return new DWOTabletKeyboard().init();
//		case 2: return new TabletKeyboardGonio();
//		case 3: return new TabletKeyboardStatistiek();
//		case 4: return new TabletKeyboardMeetkunde();
		}
	}

	
	DWOTabbedTouchKeyboard() {
		this(1);
	}
	
	DWOTabbedTouchKeyboard(int nr) {
		AbstractKeyboard tk = createKeyboard(nr);
		stock [nr] = tk;
		this.nr = nr;
		this.k123 = tk;
		this.current = tk;
		main = new FlowPanel();
		main.setStyleName("keyboard-container");
		main.addStyleName("touch");
		main.add(tk);
		tk.setDelegate(this);
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
		pen = new DWOTabletKeyboardPen();
		pen.setDelegate(this);
		pen.setVisible(false);
		main.add(pen);
		
		math = new DWOMathKeyboard().init();
		math.setDelegate(this);
		math.setVisible(false);
		main.add(math);
		
		initWidget(main);
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		setActiveEditor(formuleEditor);
		
		k123.setEditor(formuleEditor);
		kabc.setEditor(formuleEditor);
		kABC.setEditor(formuleEditor);
		pen.setEditor(formuleEditor);
		kGrUpper.setEditor(formuleEditor);
		kGrLower.setEditor(formuleEditor);
		math.setEditor(formuleEditor);
	}


	@Override
	public void softFocus() {
		focus();
	}
	
	@Override
	public void focus() {
		if(current==pen) pen.focus(); // does read formula
		super.focus();
		resizeScrollPanel(HEIGHT);
		FocusOnTouch.focus();
	}


	@Override
	public void blur() {
		super.blur();
		resizeScrollPanel(0);
		setEnterType(EnterType.APPLY);
	}


	boolean upper;
	@Override
	void switchABC() {
		if(upper) switchLtUpper(); 
		else switchLtLower();
	}

	@Override
	void switch123() {
		if(current != k123) {
			current.setVisible(false);
			current = k123;
			current.setVisible(true);
		}
	}

	@Override
	void switchHand() {
		if(current != pen) {
			current.setVisible(false);
			current = pen;
			current.focus(); // does read formula
		}
	}

	@Override
	void switchUpper() {
		if(current == kGrLower)
			switchGrUpper();
		else
			switchLtUpper();
	}


	private void switchLtUpper() {
		if(current != kABC) {
			current.setVisible(false);
			current = kABC;
			upper = true;
			current.setVisible(true);
		}
	}

	@Override
	void switchLower() {
		if(current == kGrUpper)
			switchGrLower();
		else
			switchLtLower();
	}


	private void switchLtLower() {
		if(current != kabc) {
			current.setVisible(false);
			current = kabc;
			upper = false;
			current.setVisible(true);
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

	private void switchTo(AbstractKeyboard kto) {
		if(current != kto) {
			current.setVisible(false);
			current = kto;
			current.setVisible(true);
		}
	}

	private void switchGrUpper() {
		if(current != kGrUpper) {
			current.setVisible(false);
			current = kGrUpper;
			upper = true;
			current.setVisible(true);
		}
	}

	private void switchGrLower() {
		if(current != kGrLower) {
			current.setVisible(false);
			current = kGrLower;
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

	int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void setKeyboard(int nr) {
		if(nr < 0 || nr > 4) nr = DEFAULT;
		if(this.nr != nr) {
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
		}
	}

	@Override
	public void setWriteMathSet(int nr) {
		pen.setWriteMathSet(nr);
	}

	@Override
	public void setEnterType(EnterType type) {
		ImageResource resource;
		boolean isEnter = enterImage == DWOTabletKeyboardFactory.resources.enter();
		switch(type) {
		case ENTER:
			resource = DWOTabletKeyboardFactory.resources.enter();
			if (!isEnter) switchABC();
			break;
		default:	
		case APPLY:
			resource = DWOTabletKeyboardFactory.resources.apply();
			if (isEnter) switch123();
		}
		setEnterImage(resource);
	}
	
	private ImageResource enterImage = DWOTabletKeyboardFactory.resources.apply();
	
	@Override
	void setEnterImage(ImageResource resource) {
		if(resource != enterImage) {
			enterImage = resource;
			current.setEnterImage(resource);
			k123.setEnterImage(resource);
			kabc.setEnterImage(resource);
			kABC.setEnterImage(resource);
			pen.setEnterImage(resource);
			kGrUpper.setEnterImage(resource);
			kGrLower.setEnterImage(resource);
			math.setEnterImage(resource);

		}
	}
	
}
