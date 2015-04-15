package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class DWOTabletKeyboardABC extends AbstractKeyboard {

	private static int HEIGHT = 166;
	int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}

	private static DWOTabletKeyboardABCUiBinder uiBinder = GWT
			.create(DWOTabletKeyboardABCUiBinder.class);

	interface DWOTabletKeyboardABCUiBinder extends
			UiBinder<Widget, DWOTabletKeyboardABC> {
	}

	public DWOTabletKeyboardABC() {
		pad = new DWOTabletKeyboardPad();
		pad.t3_16.addStyleName("is-active");
		pad.setDelegate(this);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField(provided=true)
	DWOTabletKeyboardPad pad;
	
	@UiField FKey t1_1,t1_2,t1_3,t1_4,t1_5,t1_6,t1_7,t1_8,t1_9,t1_10;
	@UiField FKey t2_1,t2_2,t2_3,t2_4,t2_5,t2_6,t2_7,t2_8,t2_9;
	@UiField FKey t3_1,t3_2,t3_3,t3_4,t3_5,t3_6,t3_7,t3_8,t3_9,t3_10, t3_11, t3_12;
	@UiField FKey t4_1;
	
	@UiHandler("t1_1") void onT1_1(ClickEvent e) {getEditor().insert('q');}
	@UiHandler("t1_2") void onT1_2(ClickEvent e) {getEditor().insert('w');}
	@UiHandler("t1_3") void onT1_3(ClickEvent e) {getEditor().insert('e');}
	@UiHandler("t1_4") void onT1_4(ClickEvent e) {getEditor().insert('r');}
	@UiHandler("t1_5") void onT1_5(ClickEvent e) {getEditor().insert('t');}
	@UiHandler("t1_6") void onT1_6(ClickEvent e) {getEditor().insert('y');}
	@UiHandler("t1_7") void onT1_7(ClickEvent e) {getEditor().insert('u');}
	@UiHandler("t1_8") void onT1_8(ClickEvent e) {getEditor().insert('i');}
	@UiHandler("t1_9") void onT1_9(ClickEvent e) {getEditor().insert('o');}
	@UiHandler("t1_10") void onT1_10(ClickEvent e) {getEditor().insert('p');}

	@UiHandler("t2_1") void onT2_1(ClickEvent e) {getEditor().insert('a');}
	@UiHandler("t2_2") void onT2_2(ClickEvent e) {getEditor().insert('s');}
	@UiHandler("t2_3") void onT2_3(ClickEvent e) {getEditor().insert('d');}
	@UiHandler("t2_4") void onT2_4(ClickEvent e) {getEditor().insert('f');}
	@UiHandler("t2_5") void onT2_5(ClickEvent e) {getEditor().insert('g');}
	@UiHandler("t2_6") void onT2_6(ClickEvent e) {getEditor().insert('h');}
	@UiHandler("t2_7") void onT2_7(ClickEvent e) {getEditor().insert('j');}
	@UiHandler("t2_8") void onT2_8(ClickEvent e) {getEditor().insert('k');}
	@UiHandler("t2_9") void onT2_9(ClickEvent e) {getEditor().insert('l');}

	@UiHandler("t3_1") void onT3_1(ClickEvent e) {switchUpper();}
	@UiHandler("t3_2") void onT3_2(ClickEvent e) {getEditor().insert('z');}
	@UiHandler("t3_3") void onT3_3(ClickEvent e) {getEditor().insert('x');}
	@UiHandler("t3_4") void onT3_4(ClickEvent e) {getEditor().insert('c');}
	@UiHandler("t3_5") void onT3_5(ClickEvent e) {getEditor().insert('v');}
	@UiHandler("t3_6") void onT3_6(ClickEvent e) {getEditor().insert('b');}
	@UiHandler("t3_7") void onT3_7(ClickEvent e) {getEditor().insert('n');}
	@UiHandler("t3_8") void onT3_8(ClickEvent e) {getEditor().insert('m');}
	@UiHandler("t3_9") void onT3_9(ClickEvent e) {getEditor().insert('!');}
	@UiHandler("t3_10") void onT3_10(ClickEvent e) {getEditor().insert(',');}
	@UiHandler("t3_11") void onT3_11(ClickEvent e) {getEditor().insert('.');}
	@UiHandler("t3_12") void onT3_12(ClickEvent e) {getEditor().insert('?');}
	
	@UiHandler("t4_1") void onT4_1(ClickEvent e) {getEditor().insert(' ');}

	@Override
	void switch123() {
		getDelegate().switch123();
	}
	@Override
	void switchHand() {
		getDelegate().switchHand();
	}
	@Override
	void switchUpper() {
		getDelegate().switchUpper();
	}
	
	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}
	
	@Override
	public void blur() {
		getDelegate().blur();
	}

	
}
