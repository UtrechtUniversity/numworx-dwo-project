package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LeerdoelenView extends Composite  {

	private static LeerdoelenViewUiBinder uiBinder = GWT.create(LeerdoelenViewUiBinder.class);

	interface LeerdoelenViewUiBinder extends UiBinder<Widget, LeerdoelenView> {
	}

	public LeerdoelenView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
