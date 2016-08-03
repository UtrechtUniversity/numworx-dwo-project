package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class Login2ViewImpl extends LoginViewImpl {
	private static Login2ViewImplUiBinder uiBinder = GWT
			.create(Login2ViewImplUiBinder.class);

	@UiField(provided=true) String back;
	
	interface Login2ViewImplUiBinder extends
			UiBinder<Widget, Login2ViewImpl> {
	}
	
	// loadview using Login2ViewImpl.ui.xml
	// style and layout defined in this file
	Widget createAndBindUi() {
		back = URL.encodePathSegment(Window.Location.getHref());
		return uiBinder.createAndBindUi(this);
	}

}
