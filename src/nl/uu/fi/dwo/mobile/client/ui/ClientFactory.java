package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public interface ClientFactory
{
	EventBus getEventBus();

	PlaceController getPlaceController();

	ViewModuleView getEntryView();

	SelectModuleView getHomeView();

	LoginView getLoginView();

	ProfileView getProfileView();

	TreeModuleView getTreeModuleView();
}
