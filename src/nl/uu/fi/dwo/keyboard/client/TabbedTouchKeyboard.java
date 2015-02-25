package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.user.client.ui.FlowPanel;

public class TabbedTouchKeyboard extends AbstractKeyboard {
	
	private AbstractKeyboard current;
	private TabletKeyboard k123;
	private TabletKeyboardABC kabc;
	private TabletKeyboardUpper kABC;
	private TabletKeyboardPen pen;
	
	TabbedTouchKeyboard(TabletKeyboard tk) {
		this.k123 = tk;
		this.current = tk;
		FlowPanel main;
		main = new FlowPanel();
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
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		k123.setEditor(formuleEditor);
		kabc.setEditor(formuleEditor);
		kABC.setEditor(formuleEditor);
		pen.setEditor(formuleEditor);
	}

	@Override
	public void softFocus() {
		setVisible(true);
	}

	boolean upper;
	@Override
	void switchABC() {
		if(upper) switchUpper(); 
		else switchLower();
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
			current.setVisible(true);
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

}
