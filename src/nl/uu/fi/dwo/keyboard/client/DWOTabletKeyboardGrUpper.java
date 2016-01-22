package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DWOTabletKeyboardGrUpper extends AbstractKeyboard {

	private static int HEIGHT = 166;
	int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}

	private static TabletKeyboardUpperUiBinder uiBinder = GWT
			.create(TabletKeyboardUpperUiBinder.class);

	interface TabletKeyboardUpperUiBinder extends
			UiBinder<Widget, DWOTabletKeyboardGrUpper> {
	}

	public DWOTabletKeyboardGrUpper() {
		pad = new DWOTabletKeyboardPad();
		pad.t1_16.addStyleName("is-active");;
		pad.setDelegate(this);
		initWidget(uiBinder.createAndBindUi(this));
	}
	@UiField(provided=true)
	DWOTabletKeyboardPad pad;
	
	@UiField FKey t1_1,t1_2,t1_3,t1_4,t1_5,t1_6,t1_7,t1_8,t1_9,t1_10;
	@UiField FKey t2_1,t2_2,t2_3,t2_4,t2_5,t2_6,t2_7,t2_8,t2_9;
	@UiField FKey t3_1,t3_2,t3_3,t3_4,t3_5,t3_6,t3_7,t3_8,t3_9,t3_10, t3_11, t3_12;
	@UiField FKey t4_1;

	@UiHandler({"t1_1","t1_2"}) void onT1_1(ClickEvent e) {}
	@UiHandler({"t1_3","t1_4","t1_5","t1_6","t1_7","t1_8","t1_9","t1_10",
		 "t2_1","t2_2","t2_3","t2_4","t2_5","t2_6","t2_7","t2_8","t2_9",
		 "t3_2","t3_3","t3_4","t3_5","t3_6","t3_7","t3_8","t3_9","t3_10","t3_11","t3_12"}) 
	void onT1_2(ClickEvent e) {doInsert(e);}


	@UiHandler("t3_1") void onT3_1(ClickEvent e) {switchLower();}
	@UiHandler("t4_1") void onT4_1(ClickEvent e) {getEditor().insert(' ');}

	@Override
	public void blur() {
		getDelegate().blur();
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
	void switchLower() {
		getDelegate().switchLower();
	}
	@Override
	void switchABC() {
		getDelegate().switchABC();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	void setEnterImage(ImageResource resource) {
		pad.setEnterImage(resource);
	}

}
