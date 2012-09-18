package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * @see GWT
 * 
 * @author Danny Hendrix
 * 
 */
public class ClientFactoryImpl implements ClientFactory
{
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private ViewModuleView entryView;
	private SelectModuleViewImpl selectModuleView;
	private LoginViewImpl loginView;
	private ProfileViewImpl profileView;

	public ClientFactoryImpl()
	{
		entryView = new ViewModuleViewImpl();
	}

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController()
	{
		return placeController;
	}

	@Override
	public ViewModuleView getEntryView()
	{
		return entryView;
	}

	@Override
	public SelectModuleView getHomeView()
	{
		if (selectModuleView == null)
			selectModuleView = new SelectModuleViewImpl();
		return selectModuleView;
	}

	@Override
	public LoginView getLoginView()
	{
		if (loginView == null)
			loginView = new LoginViewImpl();
		return loginView;
	}

	@Override
	public ProfileView getProfileView()
	{
		if (profileView == null)
			profileView = new ProfileViewImpl();
		return profileView;
	}

}
