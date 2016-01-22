package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fi.writemathgwt.client.WritePanel;
import fi.writemathgwt.client.WritePanelHolder;

public class DWOTabletKeyboardPen extends AbstractKeyboard implements WritePanelHolder {

	private static int HEIGHT = 166;
	int getKeyboardHeight() {
		return HEIGHT;
	}

	@UiField DWOTabletKeyboardPad pad;
	@UiField SimplePanel writePanel;
	
	WritePanel panel;
	
	private static TabletKeyboardPenUiBinder uiBinder = GWT
			.create(TabletKeyboardPenUiBinder.class);

	interface TabletKeyboardPenUiBinder extends
			UiBinder<Widget, DWOTabletKeyboardPen> {
	}

	public DWOTabletKeyboardPen() {
		initWidget(uiBinder.createAndBindUi(this));
		pad.t4_16.addStyleName("is-active");;
		pad.setDelegate(this);
		
		panel = new WritePanel(593,150,this,1);
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

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
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
		panel.setTekenSet(nr);
	}

	void setEnterImage(ImageResource resource) {
		pad.setEnterImage(resource);
	}

}
