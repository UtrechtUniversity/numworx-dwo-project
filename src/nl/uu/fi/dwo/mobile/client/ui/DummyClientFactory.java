package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class DummyClientFactory implements ClientFactory {

	private static EventBus eventBus = new SimpleEventBus();
	private ViewModuleView entryView;
	private IsWidget logoutWidget;
	
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

	public void setEntryView(ViewModuleView entryView) {
		this.entryView = entryView;
	}

	@Override
	public SCORM_guest setupAPI(Map<String, Object> profiledata) {
		return DWOplayer.api;
	}

	@Override
	public IsWidget getMenuWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IsWidget getLogoutWidget() {
		return logoutWidget;
	}

	@Override
	public void setLogoutWidget(IsWidget widget) {
		logoutWidget = widget;
	}

	@Override
	public void logout() {
	}

}
