package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fi.writemathgwt.client.WritePanel;
import fi.writemathgwt.client.WritePanelHolder;

public class TabletKeyboardPen extends AbstractKeyboard implements WritePanelHolder {

	@UiField TabletKeyboardPad pad;
	@UiField SimplePanel writePanel;
	
	WritePanel panel;
	
	private static TabletKeyboardPenUiBinder uiBinder = GWT
			.create(TabletKeyboardPenUiBinder.class);

	interface TabletKeyboardPenUiBinder extends
			UiBinder<Widget, TabletKeyboardPen> {
	}

	public TabletKeyboardPen() {
		initWidget(uiBinder.createAndBindUi(this));
		pad.disableKey(pad.t1_16);
		pad.t2_16.getUpFace().setHTML("<img src='images/kb/Basis/Touch/T2.16.png'/>");
		pad.t4_16.getUpFace().setHTML("<img src='images/kb/Basis/Touch/T4.16-3.png'/>");
		pad.setDelegate(this);
		
		panel = new WritePanel(740,150,this,1);
		writePanel.setWidget(panel);
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
	public void writePanelChanged() {
		getEditor().clearAll();
		getEditor().insert(panel.parseFormule());
	}

}
