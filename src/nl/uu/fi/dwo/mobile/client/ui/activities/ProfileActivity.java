package nl.uu.fi.dwo.mobile.client.ui.activities;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
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
		addHandlerRegistration(
		view.getLogoutBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				logout();
			}
		}));
		addHandlerRegistration(
		view.getSubmitBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				gotoCourses();
			}
		}));
		
		view.setupModule();

		panel.setWidget(view);
	}


	private void logout() {
		clientFactory.logout();
		clientFactory.getPlaceController().goTo(new LoginPlace());
	}


	private void gotoCourses() {
		DWOplayer.gotoCourses();
	}

}
