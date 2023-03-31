package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.ui.IsWidget;

public interface LoginView extends IsWidget
{
	String getUsername();
	String getPassword();
	void allowGuest(boolean allow);
	void setupModule();
	
	HasClickHandlers getLoginBtn();
	HasClickHandlers getGuestBtn();
	HasKeyUpHandlers getMainPanel();
	
	void showError(String msg);
}
