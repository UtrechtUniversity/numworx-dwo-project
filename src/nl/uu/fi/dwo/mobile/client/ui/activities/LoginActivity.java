package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash.Type;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
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
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class LoginActivity extends MGWTAbstractActivity
{
	
	static final class Login_Stap1 implements Success<DomUserFullwLoginContext, DomSchoolsRolesAndClassesV2> {

		private ClientFactory clientFactory;

		public Login_Stap1(ClientFactory clientFactory) {
			this.clientFactory = clientFactory;
		}

		@Override
		public Promise<DomSchoolsRolesAndClassesV2> call(Promise<DomUserFullwLoginContext> promise) throws Exception {
			DwoGlobalVars instance = DwoGlobalVars.instance();
			DomUserFullwLoginContext value = promise.getValue();
			if(value == null) {
				instance.setCurrentLoginContext(null);
				instance.setCurrentUser(null);
				return null;
			} else {
				instance.setCurrentLoginContext(value.getDomLoginContext());
				instance.setCurrentUser(value.getDomUserFull());
				return clientFactory.getRPCHandler().getSchoolLogins();
			}
		}
	}

	static final Logger LOG = Logger.getLogger(LoginActivity.class.getName()); 

	public final Failure FAILURE1 = new Failure() {
		
		@Override
		public void fail(Promise<?> promise) throws Exception {
			panel.setWidget(view);
			Throwable caught = promise.getFailure();
			LOG.log(Level.WARNING, "login failure ", caught);
			if (caught instanceof NoSuchElementException)
				alert("Geen toegang voor deze site"); // Rekenwise limited
			else
			if (caught.getMessage().contains("LoginException"))
				alert(Text.constants.EXR_WRONG_USERNAME_PASSWORD());
			else
				alert("Unable to login"); // if exception is DWO2exception?
		}

		private void alert(String string) {
			view.showError(string);
		}
	};

	public final Failure FAILURE2 = new Failure () {

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			panel.setWidget(view);
			Throwable caught = resolved.getFailure();
			LOG.log(Level.SEVERE, "login failure2 ", caught);
			if(clientFactory.withUser())
				clientFactory.logout();
			defer = new Deferred<>();
			rearm(defer.getPromise());
		}		
	};

	private Promise<DomDwoProfileFull> dwoProfile;

	private final Success<DomUserFullwLoginContext, DomSchoolsRolesAndClassesV2> LOGIN_STAP1;

	public final Predicate<DomSchoolsRolesAndClassesV2> LOGIN_LIMITED = new Predicate<DomSchoolsRolesAndClassesV2>() {

		@Override
		public boolean test(DomSchoolsRolesAndClassesV2 t) {
			if(dwoProfile.isDone() && t == null && dwoProfile.getFailure() == null)
			{
				String r = dwoProfile.getValue().getDwoProfileRights();
				return (r.indexOf('l') < 0);
			}
			// else test if current school in limited-schools
			return true;
		}
	};		
			
			
			
	public static final Success<DomSchoolsRolesAndClassesV2, Void> LOGIN_STAP2 = 
			 new Success<DomSchoolsRolesAndClassesV2, Void>() {

				@Override
				public Promise<Void> call(Promise<DomSchoolsRolesAndClassesV2> promise) throws Exception {
					DwoGlobalVars instance = DwoGlobalVars.instance();
					DomSchoolsRolesAndClassesV2 value = promise.getValue();
					if(value != null) {
						instance.setSchoolLogins(value);
						instance.setActiveSchoolRoleAndClass(value.getActiveSchoolRoleAndClass());
						if(value.getActiveSchoolRoleAndClass() != null)
							instance.setCurrentSchoolClass(value.getActiveSchoolRoleAndClass().getSchoolClass());
						else
							instance.setCurrentSchoolClass(null);
					}
					return null;
				}
			};

    static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	
	ClientFactory clientFactory;
	private Place next;
	LoginView view;
	AcceptsOneWidget panel;

	private Deferred<DomUserFullwLoginContext> defer;

	public LoginActivity(ClientFactory clientFactory, Place next)
	{
		this(clientFactory);
		this.next = next;
	}

	public LoginActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.dwoProfile = clientFactory.getRPCHandler().getDwoProfile();
		this.LOGIN_STAP1 = new Login_Stap1(clientFactory);
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
		this.panel = panel;
		final boolean logout = clientFactory.withUser();
		WaitScreen.instance().w();
		clientFactory.getHeaderView().hide();
		clientFactory.logout().onResolve (
		
		new Runnable() {
			public void run() {
				WaitScreen.instance().hide();
// grab before clearing 
				Boolean nonPublic = null;
				if(next instanceof Hash) {
					Hash hash = (Hash) next;
					String token = hash.getToken();
					switch(hash.getType()) {
					  case cc: nonPublic = Boolean.TRUE; break;
					  case c: case s:
    					SelectModuleItem item = 
    							hash.getType() == Type.c
    								? SelectModuleItemHolder.getItemByID(token) 
    								: SelectModuleItemHolder.getScoByID(token); // type == Type.s;
    					if(item != null) {
    						nonPublic = item.getNonPublic();
					}}
				}
				SelectModuleItemHolder.destroy();
				String user_id = Cookies.getCookie(DWO_SAML_USER_ID);
				String org_id = Cookies.getCookie(DWO_SAML_ORGANIZATION_ID);
				view = clientFactory.getLoginView();
				view.showError(null);
				
				if(Boolean.TRUE.equals(nonPublic)) {
					view.allowGuest(false);
				} else {
					//view.allowGuest(true);
				dwoProfile.then(new Success<DomDwoProfile, Void>() {

					@Override
					public Promise<Void> call(Promise<DomDwoProfile> promise) throws Exception {
						String rights = promise.getValue().getDwoProfileRights();
						view.allowGuest(rights.indexOf('l') < 0);
						return null;
					}

				});
				}
				// TESTING		
				//		user_id = "292832126";
				//		org_id = "\"lti:385\"";
				Promise<DomUserFullwLoginContext> promise;
				if(logout && isSeb())
				{
					panel.setWidget(new Label());
					RootLayoutPanel.get().setVisible(false);
					logout();
					return;
				} 
				if ((user_id != null && org_id != null)) {

					if (logout) {
						panel.setWidget(new Label());
						logout();
						return;
					}
					panel.setWidget(new Label());
// ?a= en bovendien saml cookies.
					String authToken = Window.Location.getParameter("a");
					if (authToken != null && !authToken.isEmpty())
						promise = clientFactory.getRPCHandler().getUserFromAuthToken(authToken)
// XXX wel of niet ook met saml?
						.recoverWith(p -> clientFactory.getRPCHandler().samlLogin(user_id, org_id))
						;
					else
						promise = clientFactory.getRPCHandler().samlLogin(user_id, org_id);
				} else {

					String authToken = Window.Location.getParameter("a");
					if (authToken != null && !authToken.isEmpty()) {
						if (!logout)
						{	panel.setWidget(new Label());
							promise = clientFactory.getRPCHandler().getUserFromAuthToken(authToken);
						}
						else {
							// redirect to zonder ?a=
							UrlBuilder builder = Window.Location.createUrlBuilder();
							builder.removeParameter("a");
							String buildString = builder.buildString();
							Window.Location.assign(buildString);
							return;
						}
					} else {
						defer = new Deferred<>();
						promise = defer.getPromise();
						panel.setWidget(view);
					}
					addHandlerRegistration(view.getLoginBtn().addTapHandler(new TapHandler() {

						@Override
						public void onTap(TapEvent event) {
							resolve();
						}
					}));
					addHandlerRegistration(view.getGuestBtn().addTapHandler(new TapHandler() {

						@Override
						public void onTap(TapEvent event) {
							if (clientFactory.withUser())
								clientFactory.logout(); // fail safe?
							if (defer != null)
								dwoProfile.onResolve(new Runnable() {
									public void run() {
										defer.resolve(null);
									}
								});
						}
					}));

					// Register enter handler
					addHandlerRegistration(view.getMainPanel().addKeyUpHandler(new KeyUpHandler() {

						@Override
						public void onKeyUp(KeyUpEvent event) {
							// on key up, if there is data in the username and password area, simply login
							if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
								if (!(view.getUsername().isEmpty()) && (!(view.getPassword().isEmpty()))) {
									resolve();
								}
							}
						}
					}));

				}
				Logger.getLogger("DWOplayer").log(Level.FINE, "Done with panel");
				rearm(promise);
			}

			private boolean isSeb() {
				return SecureMode.SEB == DWOplayer.PARAMETERS.getSecureMode();
			}
		}
		);
	}

	private void rearm(Promise<DomUserFullwLoginContext> promise) {
		promise
		.then(LOGIN_STAP1)
		.filter(LOGIN_LIMITED)
		.then(LOGIN_STAP2, FAILURE1)
		.then(new Login_Stap3(clientFactory, next), FAILURE2);
	}

	private void resolve() {
		if (defer == null) return;
		final Promise<DomUserFullwLoginContext> login = clientFactory.getRPCHandler().login(view.getUsername(),
				view.getPassword());
		dwoProfile.onResolve(
		new Runnable() {
			public void run() {
				defer.resolveWith(login);
			}
		});
		return ;

				
	}

	private native static void logout()/*-{
		$wnd.logout()
	}-*/;

}
