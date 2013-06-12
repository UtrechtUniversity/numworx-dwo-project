package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class ProfileActivity extends MGWTAbstractActivity implements ProfileView.Presenter
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

		view.setupModule(this);

		panel.setWidget(view);
	}

	@Override
	public void logout() {
		DWOplayer.profiledata = null;
		clientFactory.getPlaceController().goTo(new LoginPlace());
	}

	@Override
	public void gotoCourses() {
		DWOplayer.gotoCourses();
	}

}
