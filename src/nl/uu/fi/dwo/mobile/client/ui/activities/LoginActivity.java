package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

//import fi.dwo.rest.exceptions.Dwo2Exception;
//import fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class LoginActivity extends MGWTAbstractActivity
{
	private final AsyncCallback<Map<String, Object>> LOGIN_CALLBACK = new AsyncCallback<Map<String, Object>>()
	{

		@Override
		public void onFailure(Throwable caught)
		{
			GWT.log("login failure", caught);
//			if (caught instanceof Dwo2Exception) {
//				Window.alert( Dwo2ExceptionTranslator.getLocalizedCodeExplanation(null, ((Dwo2Exception) caught).getDwo2Code()));
//			} else

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

    static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	
	ClientFactory clientFactory;
	private Place next;
	LoginView view;

	public LoginActivity(ClientFactory clientFactory, Place next)
	{
		this.clientFactory = clientFactory;
		this.next = next;
	}

	public LoginActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		boolean logout = DWOplayer.profiledata != null;
		clientFactory.logout();
		
		SelectModuleItemHolder.destroy();
		String user_id = Cookies.getCookie(DWO_SAML_USER_ID);
		String org_id = Cookies.getCookie(DWO_SAML_ORGANIZATION_ID);
		DWOplayer.profiledata = null;
		view = clientFactory.getLoginView();

// TESTING		
//		user_id = "292832126";
//		org_id = "\"lti:385\"";
		
		if(user_id != null && org_id != null) {
			
			if( logout) {
				panel.setWidget(new Label());
				logout();
				return;
			}
			panel.setWidget(new Label());
			clientFactory.getRPCHandler().samlLogin(user_id, org_id, LOGIN_CALLBACK);
			return;		
		}
		
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
		Logger.getLogger("DWOplayer").log(Level.FINE,"Done with panel");
	
	}

	private native static void logout()/*-{
		$wnd.logout()
	}-*/;

}
