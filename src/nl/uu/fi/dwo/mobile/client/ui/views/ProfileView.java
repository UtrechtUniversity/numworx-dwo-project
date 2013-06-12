package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.IsWidget;

public interface ProfileView extends IsWidget
{
	interface Presenter {
		void logout();
		void gotoCourses();
	}
	
	void setupModule(Presenter presenter);
}
