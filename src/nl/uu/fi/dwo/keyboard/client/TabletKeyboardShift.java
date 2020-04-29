package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class TabletKeyboardShift extends AbstractKeyboard {

	private static TabletKeyBoardShiftUiBinder uiBinder = GWT.create(TabletKeyBoardShiftUiBinder.class);

	interface TabletKeyBoardShiftUiBinder extends UiBinder<Widget, TabletKeyboardShift> {
	}

	public TabletKeyboardShift() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField FKey t4_1;
	
	private static int HEIGHT = 166;
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	public void addShiftHandler(ClickHandler clickHandler) {
		t4_1.addClickHandler(clickHandler);		
	}

	@UiHandler("t1_1") void onT1_1(ClickEvent e) {getEditor().insert('!');}
	@UiHandler("t1_2") void onT1_2(ClickEvent e) {getEditor().insert('~');}
	@UiHandler("t2_1") void onT2_1(ClickEvent e) {getEditor().insert('#');}
	@UiHandler("t2_2") void onT2_2(ClickEvent e) {getEditor().insert('&');}

	@UiHandler("t3_1") void onT3_1(ClickEvent e) {getEditor().insert('%');}
	@UiHandler("t3_2") void onT3_2(ClickEvent e) {getEditor().insert('^');}
	@UiHandler("t4_2") void onT4_2(ClickEvent e) {getEditor().insert('*');}

}
