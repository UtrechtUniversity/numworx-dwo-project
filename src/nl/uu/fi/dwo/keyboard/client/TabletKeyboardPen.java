package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TabletKeyboardPen extends Composite {

	private static TabletKeyboardPenUiBinder uiBinder = GWT
			.create(TabletKeyboardPenUiBinder.class);

	interface TabletKeyboardPenUiBinder extends
			UiBinder<Widget, TabletKeyboardPen> {
	}

	public TabletKeyboardPen() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
