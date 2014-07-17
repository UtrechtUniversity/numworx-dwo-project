package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public class DummyClientFactory implements ClientFactory {

	private EventBus eventBus;
	private ViewModuleView entryView;
	
	public DummyClientFactory() {
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return null;
	}

	@Override
	public ViewModuleView getEntryView() {
		return entryView;
	}

	@Override
	public SelectModuleView getHomeView() {
		return null;
	}

	@Override
	public LoginView getLoginView() {
		return null;
	}

	@Override
	public ProfileView getProfileView() {
		return null;
	}

	@Override
	public TreeModuleView getTreeModuleView() {
		return null;
	}

	@Override
	public RPCHandler getRPCHandler() {
		return null;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void setEntryView(ViewModuleView entryView) {
		this.entryView = entryView;
	}

}
