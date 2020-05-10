package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

public class DWOMathKeyboardResponsive extends AbstractKeyboard {

	private static DWOMathKeyboardResponsiveUiBinder uiBinder = GWT.create(DWOMathKeyboardResponsiveUiBinder.class);

	interface DWOMathKeyboardResponsiveUiBinder extends UiBinder<Widget, DWOMathKeyboardResponsive> {
	}

	private int state;
	
	@UiField MathResponsiveCSS style;
	@UiField Label a9_1, a9_2, a1_1, a1_2, a5_1, a5_2;
	@UiField DWOTabletKeyboardPad pad;
	
	class State implements ClickHandler {
		final String S;
		State(String s) {S=s;}
		
		@Override
		public void onClick(ClickEvent event) {
			setStyleName(style.s0(), S==style.s0());
			setStyleName(style.s1(), S==style.s1());
			setStyleName(style.s2(), S==style.s2());
		}
		
	}
	
	public DWOMathKeyboardResponsive() {
		initWidget(uiBinder.createAndBindUi(this));
		State s2 = new State(style.s2());
		a9_1.addClickHandler(s2);
		a9_2.addClickHandler(s2);
		State s0 = new State(style.s0());
		a1_1.addClickHandler(s0);
		a1_2.addClickHandler(s0);
		State s1 = new State(style.s1());
		a5_1.addClickHandler(s1);
		a5_2.addClickHandler(s1);
		pad.setDelegate(this);
	}

	@Override
	public int getKeyboardHeight() {
		return 4*37+15;
	}

	@UiHandler({"t1_1","t1_2","t1_3","t1_4","t1_5","t1_6","t1_7","t1_8"}) void onT1(ClickEvent e) { doInsert(e); }
	@UiHandler({"t2_1","t2_2","t2_3","t2_4","t2_5","t2_6","t2_7","t2_8"}) void onT2(ClickEvent e) { doInsert(e); }
	@UiHandler({"t3_1","t3_2","t3_3","t3_4","t3_5","t3_6","t3_7","t3_8"}) void onT3(ClickEvent e) { doInsert(e); }
	@UiHandler({"t4_1","t4_2","t4_3","t4_4","t4_5","t4_6","t4_7","t4_8"}) void onT4(ClickEvent e) { doInsert(e); }

	@UiHandler({"t5_1","t5_2","t5_3","t5_4","t5_5","t5_6",}) void onT5(ClickEvent e) { doInsert(e); }
	@UiHandler({"t6_1","t6_2","t6_3","t6_4","t6_5","t6_6",}) void onT6(ClickEvent e) { doInsert(e); }
	@UiHandler({"t7_1","t7_2","t7_3","t7_4","t7_5","t7_6",}) void onT7(ClickEvent e) { doInsert(e); }
	@UiHandler({"t8_1","t8_2","t8_3","t8_4","t8_5","t8_6",}) void onT8(ClickEvent e) { doInsert(e); }

	@UiHandler({"t9_1","t9_2","t9_3","t9_4","t9_5","t9_6",}) void onT9(ClickEvent e) { doInsert(e); }
	@UiHandler({"t10_1","t10_2","t10_3","t10_4","t10_5","t10_6",}) void onT10(ClickEvent e) { doInsert(e); }
	@UiHandler({"t11_1","t11_2","t11_3","t11_4","t11_5","t11_6",}) void onT11(ClickEvent e) { doInsert(e); }
	@UiHandler({"t12_1","t12_2","t12_3", "t12_6",}) void onT12(ClickEvent e) { doInsert(e); }
	@UiHandler("t12_4") void onT12_4(ClickEvent e) { getEditor().insert("x\u0304"); }
	@UiHandler("t12_5") void onT12_5(ClickEvent e) { getEditor().insert("p\u0302"); }

	@Override
	public void onResize() {
		int w = getOffsetWidth();
		setStyleName(style.normal(), w >= ResponsiveCSS.SMALL);
		setStyleName(style.small(), w < ResponsiveCSS.SMALL && w >= ResponsiveCSS.EXTRASMALL);
		setStyleName(style.extrasmall(), w < ResponsiveCSS.EXTRASMALL);
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
	void switchHand() {
		getDelegate().switchHand();
	}
	
	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	@Override
	void setEnterImage(DataResource resource) {
	  pad.setEnterImage(resource);
	}

}
