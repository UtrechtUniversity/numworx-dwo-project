package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;

public interface LoginView extends IsWidget
{
	String getUsername();
	String getPassword();
	void setupModule();
	
	HasTapHandlers getLoginBtn();
	HasTapHandlers getGuestBtn();
}
