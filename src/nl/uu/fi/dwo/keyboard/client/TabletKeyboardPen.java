package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fi.writemathgwt.client.WritePanel;
import fi.writemathgwt.client.WritePanelHolder;

public class TabletKeyboardPen extends AbstractKeyboard implements WritePanelHolder {

	private static int HEIGHT = 166;
	private int height = HEIGHT;
	public int getKeyboardHeight() {
		return height;
	}

	@UiField TabletKeyboardPad pad;
	@UiField SimplePanel writePanel;
	@UiField KeyboardCSS style;
	
	WritePanel panel;
	
	private static TabletKeyboardPenUiBinder uiBinder = GWT
			.create(TabletKeyboardPenUiBinder.class);

	interface TabletKeyboardPenUiBinder extends
			UiBinder<Widget, TabletKeyboardPen> {
	}

	public TabletKeyboardPen(boolean small) {
		Widget w;
		initWidget(w = uiBinder.createAndBindUi(this));
		if(small) {
			w.addStyleName(style.small());
			height = 145;
		}
		pad.disableKey(pad.t1_16);
		pad.t4_16.addStyleName("is-active");;
		pad.setDelegate(this);
		
		panel = new WritePanel(small?504:740,small?125:150,this,1);
		writePanel.setWidget(panel);
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchABC() {
		getDelegate().switchABC();
	}

	@Override
	void switch123() {
		getDelegate().switch123();
	}

	private boolean recurse;
	@Override
	public void writePanelChanged() {
		if(recurse) return;
		getEditor().clearAll();
		getEditor().insert(panel.parseFormule());
	}

	public void focus() {
		recurse = true;
		String formula = getEditor().toString();
		panel.readFormula(formula);
		super.focus();
		recurse = false;
	}

	@Override
	public void setWriteMathSet(int nr) {
		panel.setTekenSet(nr+1);
	}
	
}
