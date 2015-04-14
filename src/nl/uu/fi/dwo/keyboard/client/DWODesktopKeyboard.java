/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author peterboon
 *
 */
public class DWODesktopKeyboard extends AbstractKeyboard {
	
	private static TabletKeyboardUiBinder uiBinder = GWT
			.create(TabletKeyboardUiBinder.class);

	interface TabletKeyboardUiBinder extends UiBinder<Widget, DWODesktopKeyboard> {
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public DWODesktopKeyboard() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
	}

	@UiField(provided=true)
	DWOkeyboardBundle resources = DWOTabletKeyboardFactory.resources;
	
	
	@UiField
	FKey t3_1,t3_2,t3_3,t3_4, t3_5,t3_6,t3_7,t3_8, t3_9,t3_10,t3_11,t3_12, t3_13,t3_14;//,t3_15;
	@UiField
	FKey t4_1,t4_2,t4_3,t4_4, t4_5,t4_6,t4_7,t4_8, t4_9,t4_10,t4_11,t4_12, t4_13,t4_14;//,t4_15;


	@UiHandler("t3_1") void onT3_1(ClickEvent e) {getEditor().insert('1');}
	@UiHandler("t3_2") void onT3_2(ClickEvent e) {getEditor().insert('2');}
	@UiHandler("t3_3") void onT3_3(ClickEvent e) {getEditor().insert('3');}
	@UiHandler("t3_4") void onT3_4(ClickEvent e) {getEditor().insert('+');}
	@UiHandler("t3_5") void onT3_5(ClickEvent e) {getEditor().insert('a');}
	@UiHandler("t3_6") void onT3_6(ClickEvent e) {getEditor().insert('b');}
	@UiHandler("t3_7") void onT3_7(ClickEvent e) {getEditor().integraal();}
	@UiHandler("t3_8") void onT3_8(ClickEvent e) {getEditor().prv();}
	@UiHandler("t3_9") void onT3_9(ClickEvent e) {getEditor().integraal();}
	@UiHandler("t3_10") void onT3_10(ClickEvent e) {getEditor().diff();}
	@UiHandler("t3_11") void onT3_11(ClickEvent e) {getEditor().diff();}
	@UiHandler("t3_12") void onT3_12(ClickEvent e) {getEditor().insert('⟨');}
	@UiHandler("t3_13") void onT3_13(ClickEvent e) {getEditor().insert('⟩');}
//	@UiHandler("t3_14") void onT3_14(ClickEvent e) {getEditor().insert('\u2264');}
//	@UiHandler("t3_15") void onT3_15(ClickEvent e) {getEditor().insert('\u2265');}

	@UiHandler("t4_1") void onT4_1(ClickEvent e) {getEditor().insert('0');}
	@UiHandler("t4_2") void onT4_2(ClickEvent e) {getEditor().insert(',');}
	@UiHandler("t4_3") void onT4_3(ClickEvent e) {getEditor().haakjes();}
	@UiHandler("t4_4") void onT4_4(ClickEvent e) {getEditor().insert('-');}
	@UiHandler("t4_5") void onT4_5(ClickEvent e) {getEditor().breuk();}
	@UiHandler("t4_6") void onT4_6(ClickEvent e) {getEditor().insert('\u03c0');}
	@UiHandler("t4_7") void onT4_7(ClickEvent e) {getEditor().limiet0();}
	@UiHandler("t4_8") void onT4_8(ClickEvent e) {getEditor().limiet1();}
	@UiHandler("t4_9") void onT4_9(ClickEvent e) {getEditor().limiet2();}
	@UiHandler("t4_10") void onT4_10(ClickEvent e) {getEditor().sigma();}
	@UiHandler("t4_11") void onT4_11(ClickEvent e) {getEditor().conjug();}
	@UiHandler("t4_12") void onT4_12(ClickEvent e) {getEditor().insert('←');}
	@UiHandler("t4_13") void onT4_13(ClickEvent e) {getEditor().insert('→');}
//	@UiHandler("t4_14") void onT4_14(ClickEvent e) {getEditor().insert(" of ");}
//	@UiHandler("t4_15") void onT4_15(ClickEvent e) {getEditor().insert('∞');}

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchABC() {
		getDelegate().switchABC();
	}

	@Override
	void switchHand() {
		getDelegate().switchHand();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	DWODesktopKeyboard init() {
		return this;
	}
}
