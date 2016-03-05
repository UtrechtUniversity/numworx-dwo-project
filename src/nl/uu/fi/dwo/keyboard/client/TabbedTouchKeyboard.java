package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedTouchKeyboard extends AbstractKeyboard {

	private AbstractKeyboard current;
	private AbstractKeyboard k123;
	private TabletKeyboardABC kabc;
	private TabletKeyboardUpper kABC;
	private TabletKeyboardPen pen;
	private AbstractKeyboard[] stock = new AbstractKeyboard[5];
	
	
	
	private AbstractKeyboard createKeyboard(int i) {
		switch(i) {
		case 0: 
				return new TabletOnderbouwKeyboard().init();
				//return new TabletKeyboardOnderbouw();
		case 1: return new TabletKeyboard().init();
		case 2: return new TabletKeyboardGonio();
		case 3: return new TabletKeyboardStatistiek();
		case 4: return new TabletKeyboardMeetkunde();
		}
		return new TabletKeyboard().init();
	}

	
	TabbedTouchKeyboard() {
		this(DEFAULT);
	}
	
	TabbedTouchKeyboard(int nr) {
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
		kabc = new TabletKeyboardABC();
		kabc.setDelegate(this);
		kabc.setVisible(false);
		main.add(kabc);
		kABC = new TabletKeyboardUpper();
		kABC.setDelegate(this);
		kABC.setVisible(false);
		main.add(kABC);
		pen = new TabletKeyboardPen();
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


	@Override
	public void softFocus() {
		focus();
	}
	
	@Override
	public void focus() {
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

	int getKeyboardHeight() {
		return current.getKeyboardHeight();
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

}
