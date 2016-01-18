package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class LoginActivity extends MGWTAbstractActivity
{
	private final AsyncCallback<Map<String, Object>> LOGIN_CALLBACK = new AsyncCallback<Map<String, Object>>()
	{

		@Override
		public void onFailure(Throwable caught)
		{
			GWT.log("login failure", caught);
			if (caught.getMessage().contains("LoginException"))
				Window.alert(Text.constants.EXR_WRONG_USERNAME_PASSWORD());
			else
				Window.alert("Unable to login");

		}

		@Override
		public void onSuccess(Map<String, Object> result)
		{
			DWOplayer.profiledata = result;
			if(next == null)
				DWOplayer.gotoCourses();
			else
				clientFactory.getPlaceController().goTo(next);
		}

	};

	
	
	ClientFactory clientFactory;
	private Place next;
	LoginView view;

	public LoginActivity(ClientFactory clientFactory, Place next)
	{
		this.clientFactory = clientFactory;
		this.next = null;
	}

	public LoginActivity(ClientFactory clientFactory2) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		SelectModuleItemHolder.destroy();
		view = clientFactory.getLoginView();
		
		addHandlerRegistration(view.getLoginBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				clientFactory.getRPCHandler().login(view.getUsername(), view.getPassword(), LOGIN_CALLBACK);
			}
		}));
		addHandlerRegistration(view.getGuestBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				DWOplayer.profiledata = null;
				if(next == null)
					DWOplayer.gotoCourses();
				else
					clientFactory.getPlaceController().goTo(next);
			}
		}));
		
		// Register enter handler
		addHandlerRegistration(view.getMainPanel().addKeyUpHandler(new KeyUpHandler () {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				// on key up, if there is data in the username and password area, simply login
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (!(view.getUsername().isEmpty()) && (!(view.getPassword().isEmpty()))) {
						clientFactory.getRPCHandler().login(view.getUsername(), view.getPassword(), LOGIN_CALLBACK);
						
					}
				}		
			}} ));

		panel.setWidget(view);
		Logger.getLogger("DWOplayer").log(Level.INFO,"Done with panel");
	
	}

}
