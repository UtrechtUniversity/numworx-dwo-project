package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class ProfileActivity extends MGWTAbstractActivity
{
	ClientFactory clientFactory;

	public ProfileActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		ProfileView view = clientFactory.getProfileView();

		view.setupModule();

		panel.setWidget(view);
	}

}
