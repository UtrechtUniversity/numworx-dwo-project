package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.places.HasHash;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash.Type;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.HandlerRegistrations;

import dagger.Lazy;

public class LoginActivity extends AbstractActivity
{
	
	static final class Login_Stap2 implements Success<DomSchoolsRolesAndClassesV2, Void> {
		private final DwoGlobalVars vars;
				
		public Login_Stap2(DwoGlobalVars vars) {
			this.vars = vars;
		}


		@Override
		public Promise<Void> call(Promise<DomSchoolsRolesAndClassesV2> promise) throws Exception {
			DomSchoolsRolesAndClassesV2 value = promise.getValue();
			if(value != null) {
				vars.setSchoolLogins(value);
				vars.setActiveSchoolRoleAndClass(value.getActiveSchoolRoleAndClass());
				if(value.getActiveSchoolRoleAndClass() != null)
					vars.setCurrentSchoolClass(value.getActiveSchoolRoleAndClass().getSchoolClass());
				else
					vars.setCurrentSchoolClass(null);
			}
			return null;
		}
	}

	static final class Login_Stap1 implements Success<DomUserFullwLoginContext, DomSchoolsRolesAndClassesV2> {

		private RPCHandler rpc;
		private DwoGlobalVars vars;

		public Login_Stap1(RPCHandler rpc, DwoGlobalVars vars) {
			this.rpc = rpc;
			this.vars = vars;
		}

		@Override
		public Promise<DomSchoolsRolesAndClassesV2> call(Promise<DomUserFullwLoginContext> promise) throws Exception {
			DwoGlobalVars instance = vars;
			DomUserFullwLoginContext value = promise.getValue();
			if(value == null) {
				instance.setCurrentLoginContext(null);
				instance.setCurrentUser(null);
				return null;
			} else {
				instance.setCurrentLoginContext(value.getDomLoginContext());
				instance.setCurrentUser(value.getDomUserFull());
				return rpc.getSchoolLogins();
			}
		}
	}

	static final Logger LOG = Logger.getLogger(LoginActivity.class.getName()); 

	public final Failure FAILURE1 = new Failure() {
		
		@Override
		public void fail(Promise<?> promise) throws Exception {
			panel.setWidget(view.get());
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
			view.get().showError(string);
		}
	};

	public final Failure FAILURE2 = new Failure () {

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			panel.setWidget(view.get());
			Throwable caught = resolved.getFailure();
			LOG.log(Level.SEVERE, "login failure2 ", caught);
			if(vars.withUser())
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
			
			
			
    static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	
	ClientFactory clientFactory;
	private Place next;
	@Inject Lazy<LoginView> view;
	AcceptsOneWidget panel;

	private Deferred<DomUserFullwLoginContext> defer;

	protected HandlerRegistration registrations;

    private final DWOplayerParameters PARAMETERS;

	@Inject PlaceController placeController;

	@Inject HeaderView headerView;
	final private DwoGlobalVars vars;
	final private RPCHandler rpc;

	private Login_Stap2 LOGIN_STAP2;
	

//	public LoginActivity(ClientFactory clientFactory, Place next)
//	{
//		this(clientFactory);
//		this.next = next;
//	}

	@Inject LoginActivity(ClientFactory clientFactory, DWOplayerParameters p, RPCHandler rpc, PlaceController placeController, DwoGlobalVars vars) {
		this.clientFactory = clientFactory;
		this.PARAMETERS = p;
		this.rpc = rpc;
		this.dwoProfile = rpc.getDwoProfile();
		this.vars = vars;
		this.LOGIN_STAP1 = new Login_Stap1(rpc, vars);
		this.LOGIN_STAP2 = new Login_Stap2(vars);
		Place place = placeController.getWhere();
		if (place instanceof HasHash)
		  next = ((HasHash) place).getPlace();
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
		this.panel = panel;
		final boolean logout = vars.withUser();
		WaitScreen.instance().w();
		headerView.hide();
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
				view.get().showError(null);
				
				if(Boolean.TRUE.equals(nonPublic)) {
					view.get().allowGuest(false);
				} else {
					//view.allowGuest(true);
				dwoProfile.then(new Success<DomDwoProfile, Void>() {

					@Override
					public Promise<Void> call(Promise<DomDwoProfile> promise) throws Exception {
						String rights = promise.getValue().getDwoProfileRights();
						view.get().allowGuest(rights.indexOf('l') < 0);
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
						promise = rpc.getUserFromAuthToken(authToken)
// XXX wel of niet ook met saml?
						.recoverWith(p -> rpc.samlLogin(user_id, org_id))
						;
					else
						promise = rpc.samlLogin(user_id, org_id);
				} else {

					String authToken = Window.Location.getParameter("a");
					if (authToken != null && !authToken.isEmpty()) {
						if (!logout)
						{	panel.setWidget(new Label());
							promise = rpc.getUserFromAuthToken(authToken);
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
						panel.setWidget(view.get());
					}
					registrations = HandlerRegistrations.compose(
					
					(view.get().getLoginBtn().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							resolve();
						}
					})) ,
					(view.get().getGuestBtn().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							if (vars.withUser())
								clientFactory.logout(); // fail safe?
							if (defer != null)
								dwoProfile.onResolve(new Runnable() {
									public void run() {
										defer.resolve(null);
									}
								});
						}
					})),

					// Register enter handler
					(view.get().getMainPanel().addKeyUpHandler(new KeyUpHandler() {

						@Override
						public void onKeyUp(KeyUpEvent event) {
							// on key up, if there is data in the username and password area, simply login
							if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
								if (!(view.get().getUsername().isEmpty()) && (!(view.get().getPassword().isEmpty()))) {
									resolve();
								}
							}
						}
					})));

				}
				Logger.getLogger("DWOplayer").log(Level.FINE, "Done with panel");
				rearm(promise);
			}

			private boolean isSeb() {
				return SecureMode.SEB == PARAMETERS.getSecureMode();
			}
		}
		);
	}

	private void rearm(Promise<DomUserFullwLoginContext> promise) {
		promise
		.then(LOGIN_STAP1)
		.filter(LOGIN_LIMITED)
		.then(LOGIN_STAP2, FAILURE1).map(nop -> next)
		.then(new Login_Stap3(clientFactory, placeController, headerView, vars), FAILURE2);
	}

	private void resolve() {
		if (defer == null) return;
		final Promise<DomUserFullwLoginContext> login = rpc.login(view.get().getUsername(),
				view.get().getPassword());
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


	@Override
	public void onCancel() {
		if (registrations != null) registrations.removeHandler();
		super.onCancel();
	}

	@Override
	public void onStop() {
		if (registrations != null) registrations.removeHandler();
		super.onStop();
	}

}
