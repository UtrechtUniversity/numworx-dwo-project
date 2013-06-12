package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.IsWidget;

public interface LoginView extends IsWidget
{
	interface Presenter {
		void login();
		void login(String username, String password);
	}
	
	
	void setupModule(Presenter presenter);
}
