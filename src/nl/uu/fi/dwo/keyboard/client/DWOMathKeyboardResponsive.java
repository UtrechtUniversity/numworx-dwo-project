package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DWOMathKeyboardResponsive extends AbstractKeyboard {

	private static DWOMathKeyboardResponsiveUiBinder uiBinder = GWT.create(DWOMathKeyboardResponsiveUiBinder.class);

	interface DWOMathKeyboardResponsiveUiBinder extends UiBinder<Widget, DWOMathKeyboardResponsive> {
	}

	public DWOMathKeyboardResponsive() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public int getKeyboardHeight() {
		return 4*37+15;
	}

}
