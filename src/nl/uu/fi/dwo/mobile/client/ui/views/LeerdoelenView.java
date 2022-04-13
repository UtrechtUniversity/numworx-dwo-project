package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LeerdoelenView extends Composite  {

	private static LeerdoelenViewUiBinder uiBinder = GWT.create(LeerdoelenViewUiBinder.class);

	interface LeerdoelenViewUiBinder extends UiBinder<Widget, LeerdoelenView> {
	}

	public LeerdoelenView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button button;


	@UiHandler("button")
	void onClick(ClickEvent e) {
	}

}
