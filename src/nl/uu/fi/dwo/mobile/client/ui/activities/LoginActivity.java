package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class LoginActivity extends MGWTAbstractActivity
{
	private static final AsyncCallback<Map<String, Object>> LOGIN_CALLBACK = new AsyncCallback<Map<String, Object>>()
	{

		@Override
		public void onFailure(Throwable caught)
		{
			GWT.log("login failure", caught);
			if (caught.getMessage().contains("LoginException"))
				Window.alert("Gebruikersnaam/wachtwoord combinatie niet juist");
			else
				Window.alert("Unable to login");

		}

		@Override
		public void onSuccess(Map<String, Object> result)
		{
			DWOplayer.profiledata = result;
			DWOplayer.clientfactory.getPlaceController().goTo(new ProfilePlace("Profile"));
		}

	};

	
	
	private ClientFactory clientFactory;
	private LoginView view;

	public LoginActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getLoginView();
		
		addHandlerRegistration(view.getLoginBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				login(view.getUsername(), view.getPassword());
			}
		}));
		addHandlerRegistration(view.getGuestBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				login();
			}
		}));
		panel.setWidget(view);
	}
	public void login(String name, String password)
	{
		RPCHandler handler = clientFactory.getRPCHandler();
		handler.login(name, password, LOGIN_CALLBACK);
	}

	public void login() {
		DWOplayer.gotoCourses();
	}

}
