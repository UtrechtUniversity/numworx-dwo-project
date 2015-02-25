package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TabletKeyboardUpper extends AbstractKeyboard {

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}

	private static TabletKeyboardUpperUiBinder uiBinder = GWT
			.create(TabletKeyboardUpperUiBinder.class);

	interface TabletKeyboardUpperUiBinder extends
			UiBinder<Widget, TabletKeyboardUpper> {
	}

	public TabletKeyboardUpper() {
		pad = new TabletKeyboardPad();
		pad.disableKey(pad.t1_16);
		pad.t2_16.getUpFace().setHTML("<img src='images/kb/Basis/Touch/T2.16.png'/>");
		pad.t3_16.getUpFace().setHTML("<img src='images/kb/Basis/Touch/T3.16-3.png'/>");
		initWidget(uiBinder.createAndBindUi(this));
	}
	@UiField(provided=true)
	TabletKeyboardPad pad;
	
	@UiField Key t1_1,t1_2,t1_3,t1_4,t1_5,t1_6,t1_7,t1_8,t1_9,t1_10;
	@UiField Key t2_1,t2_2,t2_3,t2_4,t2_5,t2_6,t2_7,t2_8,t2_9;
	@UiField Key t3_1,t3_2,t3_3,t3_4,t3_5,t3_6,t3_7,t3_8,t3_9,t3_10, t3_11, t3_12;
	@UiField Key t4_1;

	@UiHandler("t1_1") void onT1_1(ClickEvent e) {getEditor().insert('Q');}
	@UiHandler("t1_2") void onT1_2(ClickEvent e) {getEditor().insert('W');}
	@UiHandler("t1_3") void onT1_3(ClickEvent e) {getEditor().insert('E');}
	@UiHandler("t1_4") void onT1_4(ClickEvent e) {getEditor().insert('R');}
	@UiHandler("t1_5") void onT1_5(ClickEvent e) {getEditor().insert('T');}
	@UiHandler("t1_6") void onT1_6(ClickEvent e) {getEditor().insert('Y');}
	@UiHandler("t1_7") void onT1_7(ClickEvent e) {getEditor().insert('U');}
	@UiHandler("t1_8") void onT1_8(ClickEvent e) {getEditor().insert('I');}
	@UiHandler("t1_9") void onT1_9(ClickEvent e) {getEditor().insert('O');}
	@UiHandler("t1_10") void onT1_10(ClickEvent e) {getEditor().insert('P');}

	@UiHandler("t2_1") void onT2_1(ClickEvent e) {getEditor().insert('A');}
	@UiHandler("t2_2") void onT2_2(ClickEvent e) {getEditor().insert('S');}
	@UiHandler("t2_3") void onT2_3(ClickEvent e) {getEditor().insert('D');}
	@UiHandler("t2_4") void onT2_4(ClickEvent e) {getEditor().insert('F');}
	@UiHandler("t2_5") void onT2_5(ClickEvent e) {getEditor().insert('G');}
	@UiHandler("t2_6") void onT2_6(ClickEvent e) {getEditor().insert('H');}
	@UiHandler("t2_7") void onT2_7(ClickEvent e) {getEditor().insert('J');}
	@UiHandler("t2_8") void onT2_8(ClickEvent e) {getEditor().insert('K');}
	@UiHandler("t2_9") void onT2_9(ClickEvent e) {getEditor().insert('L');}

	@UiHandler("t3_1") void onT3_1(ClickEvent e) {switchUpper();}
	@UiHandler("t3_2") void onT3_2(ClickEvent e) {getEditor().insert('Z');}
	@UiHandler("t3_3") void onT3_3(ClickEvent e) {getEditor().insert('X');}
	@UiHandler("t3_4") void onT3_4(ClickEvent e) {getEditor().insert('C');}
	@UiHandler("t3_5") void onT3_5(ClickEvent e) {getEditor().insert('V');}
	@UiHandler("t3_6") void onT3_6(ClickEvent e) {getEditor().insert('B');}
	@UiHandler("t3_7") void onT3_7(ClickEvent e) {getEditor().insert('N');}
	@UiHandler("t3_8") void onT3_8(ClickEvent e) {getEditor().insert('M');}
	@UiHandler("t3_9") void onT3_9(ClickEvent e) {getEditor().insert('!');}
	@UiHandler("t3_10") void onT3_10(ClickEvent e) {getEditor().insert(',');}
	@UiHandler("t3_11") void onT3_11(ClickEvent e) {getEditor().insert('.');}
	@UiHandler("t3_12") void onT3_12(ClickEvent e) {getEditor().insert('?');}
	
	@UiHandler("t4_1") void onT4_1(ClickEvent e) {getEditor().insert(' ');}

}
