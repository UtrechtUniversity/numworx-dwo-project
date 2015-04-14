package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedDesktopKeyboard extends AbstractKeyboard {

	private static final int HEIGHT = 44;

	private DesktopKeyboard current, stock[] = new DesktopKeyboard[5];
	private int nr;
	private FlowPanel main;

	TabbedDesktopKeyboard(int nr) {
		current = createKeyboard(nr);
		stock[nr] = current;
		main = new FlowPanel();
		initWidget(main);
		main.setStyleName("keyboard-container");
		main.addStyleName("computer");
		main.add(current);
		current.setDelegate(this);
	}

	public TabbedDesktopKeyboard() {
		this(DEFAULT);
	}


	@Override
	public void setKeyboard(int nr) {
		if(nr < 0 || nr > 4) nr = DEFAULT;
		if(this.nr != nr) {
			current.removeFromParent();
			if(stock[nr] == null) stock[nr] = createKeyboard(nr);
			this.nr = nr;
			current = stock[nr];
			current.setEditor(getEditor());
			current.setDelegate(this);
			main.add(current);
		}
	}
	
	private DesktopKeyboard createKeyboard(int i) {
		switch(i) {
		case 0: return new DesktopKeyboardOnderbouw();
		case 1: return new DesktopKeyboard().init();
		case 2: return new DesktopKeyboardGonio().init();
		case 3: return new DesktopKeyboardStatistiek();
		case 4: return new DesktopKeyboardMeetkunde().init();
		}
		return new DesktopKeyboard().init();
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		setActiveEditor(formuleEditor);
		current.setEditor(formuleEditor);
	}

	private Widget scrollPanel; 
	private int origHeight = 426 - 40;
	private int origDelta = 0;
	
	void resizeScrollPanel(int size) {
		origDelta = size;
		if(scrollPanel != null)
			scrollPanel.setPixelSize(-1, origHeight - size);
	}
	public void setScrollPanel(Widget w, int h) {
		scrollPanel = w;
		origHeight = h;
		if(scrollPanel != null) scrollPanel.setPixelSize(-1, origHeight - origDelta);
	}

	public int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void focus() {
		super.focus();
		resizeScrollPanel(HEIGHT);
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

	
}
