package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DWOTabletKeyboardPad extends AbstractKeyboard {

	private static int HEIGHT = 166;
	int getKeyboardHeight() {
		return HEIGHT;
	}

	private static TabletKeyboardPadUiBinder uiBinder = GWT
			.create(TabletKeyboardPadUiBinder.class);

	interface TabletKeyboardPadUiBinder extends
			UiBinder<Widget, DWOTabletKeyboardPad> {
	}

	@UiField(provided=true)
	DWOkeyboardBundle resources = DWOTabletKeyboardFactory.resources;

	public DWOTabletKeyboardPad() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField FKey t1_16,t1_17,t2_16,t2_17,t3_16, t4_16,t4_17;
	
	@UiHandler("t1_16") void onT1_16(ClickEvent e) {switchGreek();}
	@UiHandler("t1_17") void onT1_17(ClickEvent e) {backspace();}

	@UiHandler("t2_16") void onT2_16(ClickEvent e) {switch123();}
	@UiHandler("t2_17") void onT2_17(ClickEvent e) {enter();}

	@UiHandler("t3_16") void onT3_16(ClickEvent e) {switchABC();}

	@UiHandler("t4_16") void onT4_16(ClickEvent e) {switchHand();}
	@UiHandler("t4_17") void onT4_17(ClickEvent e) {blur();}


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
	void switchHand() {
		getDelegate().switchHand();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}
}
