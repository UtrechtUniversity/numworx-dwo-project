package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PrintSeparator extends Composite {

	private static PrintSeparatorUiBinder uiBinder = GWT.create(PrintSeparatorUiBinder.class);

	@UiField
	Label page;
	
	interface PrintSeparatorUiBinder extends UiBinder<Widget, PrintSeparator> {
	}

	public PrintSeparator() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public PrintSeparator(int cur) {
		this();
		page.setText(String.valueOf(cur));
	}

}
