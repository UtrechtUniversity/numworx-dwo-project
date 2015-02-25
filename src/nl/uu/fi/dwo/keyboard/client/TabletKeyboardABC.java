package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TabletKeyboardABC extends AbstractKeyboard {

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}

	private static TabletKeyboardABCUiBinder uiBinder = GWT
			.create(TabletKeyboardABCUiBinder.class);

	interface TabletKeyboardABCUiBinder extends
			UiBinder<Widget, TabletKeyboardABC> {
	}

	public TabletKeyboardABC() {
		pad = new TabletKeyboardPad();
		pad.disableKey(pad.t1_16);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField(provided=true)
	TabletKeyboardPad pad;
	
	@UiField Key t1_1,t1_2,t1_3,t1_4,t1_5,t1_6,t1_7,t1_8,t1_9,t1_10;
	@UiField Key t2_1,t2_2,t2_3,t2_4,t2_5,t2_6,t2_7,t2_8,t2_9,t2_10;
	@UiField Key t3_1,t3_2,t3_3,t3_4,t3_5,t3_6,t3_7,t3_8,t3_9,t3_10;
	@UiField Key t4_1;
}
