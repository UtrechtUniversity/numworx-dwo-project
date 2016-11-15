package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.UserBar;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class LoginActivity extends MGWTAbstractActivity
{
	
	static final Logger LOG = Logger.getLogger(LoginActivity.class.getName()); 
	
	private final AsyncCallback<Map<String, Object>> LOGIN_CALLBACK = new AsyncCallback<Map<String, Object>>()
	{

		@Override
		public void onFailure(Throwable caught)
		{
			LOG.log(Level.WARNING, "login failure", caught);

			if (caught.getMessage().contains("LoginException"))
				Window.alert(Text.constants.EXR_WRONG_USERNAME_PASSWORD());
			else
				Window.alert("Unable to login");

		}

		@Override
		public void onSuccess(Map<String, Object> result)
		{
			if(DWOplayer.dwoProfile.isDone() && result == null)
			{
				String r = DWOplayer.dwoProfile.getValue().getDwoProfileRights();
				if(r.indexOf('l') >= 0)
				{
					Window.alert("Geen toegang voor deze site" );
					return;
				}
			}
			
			
			DWOplayer.profiledata = result;
			if(next == null)
				DWOplayer.gotoCourses();
			else
				clientFactory.getPlaceController().goTo(next);
		}

	};
	
	
	public static final Failure FAILURE1 = new Failure() {
		
		@Override
		public void fail(Promise<?> promise) throws Exception {
			Throwable caught = promise.getFailure();
			LOG.log(Level.WARNING, "login failure", caught);

			if (caught.getMessage().contains("LoginException"))
				Window.alert(Text.constants.EXR_WRONG_USERNAME_PASSWORD());
			else
				Window.alert("Unable to login");
		}
	};

	public final Failure FAILURE2 = new Failure () {

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			defer = new Deferred<>();
			rearm(defer.getPromise());
		}		
	};
	
	
	public static final Success<DomUserFullwLoginContext, DomSchoolsRolesAndClassesV2> LOGIN_STAP1 =
			
			new Success<DomUserFullwLoginContext, DomSchoolsRolesAndClassesV2>() {
				
				@Override
				public Promise<DomSchoolsRolesAndClassesV2> call(Promise<DomUserFullwLoginContext> promise) throws Exception {
					DwoGlobalVars instance = DwoGlobalVars.getInstance();
					DomUserFullwLoginContext value = promise.getValue();
					if(value == null) {
						instance.setCurrentLoginContext(null);
						instance.setCurrentUser(null);
						return Promises.resolved(null);
					} else {
						instance.setCurrentLoginContext(value.getDomLoginContext());
						instance.setCurrentUser(value.getDomUserFull());
						return DWOplayer.clientfactory.getRPCHandler().getSchoolLogins();
					}
				}
			};

	public static final Success<DomSchoolsRolesAndClassesV2, Void> LOGIN_STAP2 = 
			 new Success<DomSchoolsRolesAndClassesV2, Void>() {

				@Override
				public Promise<Void> call(Promise<DomSchoolsRolesAndClassesV2> promise) throws Exception {
					DwoGlobalVars instance = DwoGlobalVars.getInstance();
					DomSchoolsRolesAndClassesV2 value = promise.getValue();
					if(value != null) {
						instance.setSchoolLogins(value);
						if(value.getActiveSchoolRoleAndClass() != null)
							instance.setCurrentSchoolClass(value.getActiveSchoolRoleAndClass().getSchoolClass());
						else
							instance.setCurrentSchoolClass(null);
					}
					return null;
				}
			};

	private final Success<Void, Void> LOGIN_STAP3 = new Success<Void, Void>() {

		@Override
		public Promise<Void> call(Promise<Void> resolved) throws Exception {
			if(next == null)
				DWOplayer.gotoCourses();
			else
				clientFactory.getPlaceController().goTo(next);
			return null;

		}
	};

    static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	
	ClientFactory clientFactory;
	private Place next;
	LoginView view;

	private Deferred<DomUserFullwLoginContext> defer;

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
		boolean logout = DWOplayer.withUser();
		clientFactory.logout();
		
		SelectModuleItemHolder.destroy();
		String user_id = Cookies.getCookie(DWO_SAML_USER_ID);
		String org_id = Cookies.getCookie(DWO_SAML_ORGANIZATION_ID);
		view = clientFactory.getLoginView();
		DWOplayer.dwoProfile.then(new Success<DomDwoProfile, Void>() {

			@Override
			public Promise<Void> call(Promise<DomDwoProfile> promise)
					throws Exception {
				String rights = promise.getValue().getDwoProfileRights();
				view.allowGuest(rights.indexOf('l') < 0);
				return null;
			}
			
		});
// TESTING		
//		user_id = "292832126";
//		org_id = "\"lti:385\"";
		Promise<DomUserFullwLoginContext> promise;
		
		if(user_id != null && org_id != null) {
			
			if( logout) {
				panel.setWidget(new Label());
				logout();
				return;
			}
			panel.setWidget(new Label());
			//clientFactory.getRPCHandler().samlLogin(user_id, org_id, LOGIN_CALLBACK);
			promise = clientFactory.getRPCHandler().samlLogin(user_id, org_id);
		} else {

		String authToken = Window.Location.getParameter("a");
		if(authToken != null && ! authToken.isEmpty())
		{
			if (!logout)
				promise = clientFactory.getRPCHandler().getUserFromAuthToken(authToken);
			else {
				// redirect to zonder ?a=
				UrlBuilder builder = Window.Location.createUrlBuilder();
				builder.removeParameter("a");
				String buildString = builder.buildString();
				Window.Location.assign(buildString);
				return;
			}
		}
		
		defer = new Deferred<>();
		promise = defer.getPromise();
		
		addHandlerRegistration(view.getLoginBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				resolve();
			}
		}));
		addHandlerRegistration(view.getGuestBtn().addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				if(clientFactory.withUser()) clientFactory.logout(); // fail safe?
				defer.resolve(null);
			}
		}));
		
		// Register enter handler
		addHandlerRegistration(view.getMainPanel().addKeyUpHandler(new KeyUpHandler () {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				// on key up, if there is data in the username and password area, simply login
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (!(view.getUsername().isEmpty()) && (!(view.getPassword().isEmpty()))) {
						resolve();						
					}
				}		
			}} ));

		panel.setWidget(view);
		}
		Logger.getLogger("DWOplayer").log(Level.FINE,"Done with panel");
		rearm(promise);
	
	}

	private void rearm(Promise<DomUserFullwLoginContext> promise) {
		promise.then(LOGIN_STAP1).then(LOGIN_STAP2, FAILURE1).then(LOGIN_STAP3, FAILURE2);
	}

	private Promise<Void> resolve() {
		Promise<DomUserFullwLoginContext> login = clientFactory.getRPCHandler().login(view.getUsername(), view.getPassword());
		return defer.resolveWith(login/*.fallbackTo((defer = new Deferred<DomUserFullwLoginContext>()).getPromise())*/);
	}

	private native static void logout()/*-{
		$wnd.logout()
	}-*/;

}
