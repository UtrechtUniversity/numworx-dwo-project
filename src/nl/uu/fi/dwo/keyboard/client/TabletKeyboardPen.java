package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TabletKeyboardPen extends AbstractKeyboard {

	@UiField TabletKeyboardPad pad;
	@UiField SimplePanel writePanel;
	
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

	}

}
