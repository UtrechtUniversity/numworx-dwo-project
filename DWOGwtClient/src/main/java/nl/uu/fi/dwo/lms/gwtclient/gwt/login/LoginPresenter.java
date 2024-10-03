package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars.DwoGlobalVarsState;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedDialogWithConfirmDeferred;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
@RoleScope
public class LoginPresenter implements Success<DomDwoProfileFull, Void >{

    private static final Logger LOG = Logger.getLogger(LoginPresenter.class.getName());
    private static final String NEWSESSION = DwoLocalesForGWT.instance.GUI_Dialog_User_ConfirmNewLoginSession();
    private final String locale;
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private String defaultUsername = "";
    private String defaultPassword = "";
    private int stage;

    private DwoLocalesForGWT resourceBindings = DwoLocalesForGWT.instance;

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
        
        view.showMessage("<iframe src='//cdn.dwo.nl/resources/alert_"
            + locale
            + ".html'></iframe>");
    }

//    /**
//     * @return the resourceBindings
//     */
//    @JsMethod
//    public DwoLocalesForGWT getResourceBindings() {
//        return resourceBindings;
//    }
    /**
     * @param resourceBindings the resourceBindings to set
     */
    public void setResourceBindings(DwoLocalesForGWT resourceBindings) {
        this.resourceBindings = resourceBindings;
    }

    private final class LoginSucces implements Success<DwoGlobalVars.DwoGlobalVarsState, Boolean> {
		private final Boolean switchRole;

		private LoginSucces(Boolean switchRole) {
			this.switchRole = switchRole;
		}

		@Override
		public Promise<Boolean> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
		    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
		        if (!dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
		            eventBus.fireEvent(new MessageDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
		        };
		        boolean switchR = true;
		        LOG.log(Level.INFO, "login succeeded for user:" + dwoGlobalVars.getCurrentUser().getUniqueDisplayName());
		        try {
		            RoleType loginRole = dwoGlobalVars.getRole();    
		            if (RoleType.TEACHER == loginRole || RoleType.SCHOOLADMIN == loginRole || RoleType.STUDENT == loginRole) {
		                switchR = false;
		            }
		        } catch (Exception e) {
		            switchR = true;
		        }
		        if (switchR || switchRole) {
		            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_ROLE));
		        } else {
		            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_WELCOME));
		        }
		        LOG.log(Level.INFO, "login succeeded. Firing Login success event.");
		        //true means we are done
		        return Promises.resolved(false);
		    } else {
		        LOG.log(Level.INFO, "login failed, wrong login state: " + resolved.getValue().name());
		        dwoGlobalVars.clearCurrentUser();
		        view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
		        return Promises.resolved(true);
		    }
		}
	}

	public interface Display extends BasicDisplay {

        /**
         * Sets the username in the ui box.
         */
        public void setUsername(String username);

        /**
         * Sets the password in the ui box.
         */
        public void setPassword(String password);

        /**
         * Sets a response message in the ui box. For example when a user fails
         * to login
         */
        public void showMessage(String message);

        /**
         * Sets a warning text in the ui box. For example when the client can
         * not reach the remote server.
         */
        public void showWarning(String warning);

        /**
         * Hides the message or warning box.
         */
        public void hideMsgBox();

		void hideGuest();
    }

    @Inject LoginPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, GwtClientMessages rb)  {
        locale = rb.locale();
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        // broken calling view.clear();
//        init();
    }

    final public void init() {
//    view.init();
//    view.clear();
//        getView().setUsername(defaultUsername);
//        getView().setPassword(defaultPassword);
    	dwoGlobalVars.getProfile().then(this);
    }

//public String loginClickedJS(String user, String password) {
//        this.loginClicked(user, password, false);
//        return "done";
//    }
    /**
     * User login call. A login function is called.
     *
     * @param user username
     * @param password cleartext password.
     * @param switchRole In case a roleSwitch is desired, generally the value is
     * false.
     */
    @JsMethod
    public void loginClicked(String user, String password, final Boolean switchRole) {
        PromisedDialogWithConfirmDeferred d;
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = dwoGlobalVars.initUser(user, password, () -> Promises.resolved(Window.confirm(NEWSESSION)));
            loginUser.then(new LoginSucces(switchRole),
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    if (fail instanceof Dwo2Exception) {
                        //Login failed
                        LOG.log(Level.INFO, "dwo2exception thrown: " + fail.getMessage());
                        dwoGlobalVars.clearCurrentUser();
                        view.showWarning(fail.getLocalizedMessage());
                    } else {
                        dwoGlobalVars.clearCurrentUser();
                        LOG.log(Level.INFO, "none dwo2exception thrown: " + fail.getMessage());
                        view.showWarning(fail.getMessage());
                    }
                    eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                }
            }
            );
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
        } catch (Exception ex) {
            //Somehow not all exceptions are caught here.
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_InternalError());
        }
    }

    public void tokenLogin(String token) {
        try {
            Promise<DwoGlobalVarsState> statePromise;
            statePromise = dwoGlobalVars.initUserWithToken(token);
            statePromise.then(new LoginSucces(false),
                    resolved -> {
                        Throwable fail = resolved.getFailure();
                        dwoGlobalVars.clearCurrentUser();
                        if (fail instanceof Dwo2Exception) {
                            //Login failed
                            LOG.log(Level.INFO, "dwo2exception thrown: " + fail.getMessage());
                            Dwo2ExceptionCode code = ((Dwo2Exception) fail).getDwo2Code();
                            DwoLocale locale = DwoGlobalVars.getDwoLocale();
                            //view.hideMsgBox();
                        } else {
                            dwoGlobalVars.clearCurrentUser();
                            LOG.log(Level.INFO, "none dwo2exception thrown: " + fail.getMessage());
                            view.showWarning(fail.getMessage());
                        }
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                    }
            );
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
        } catch (Exception ex) {
            //Somehow not all exceptions are caught here.
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_InternalError());
        }

    }

    /**
     * Sets the default username and password in the login ui.
     *
     * @param u
     * @param pw
     */
    public void setDefaultLogin(String u, String pw) {
        defaultUsername = u;
        defaultPassword = pw;
    }

    public static boolean licenseIsValid(DomSchool s) {
        if (s.getExpire() == null) {
            return true;
        } else {
            Date now = new Date();
            return now.before(s.getExpire());
        }
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(int stage) {
      this.stage = stage;
    }
    
    @JsMethod public void loginGuest() {
      //if (stage > 0)
      {
        dwoGlobalVars.clearCurrentUser();
        DomLoginContext context = new DomLoginContext();
        dwoGlobalVars.setCurrentLoginContext(context);
        DomUserFull user = new DomUserFull();
        user.setSingleSchool(false);
        dwoGlobalVars.setCurrentUser(user);
        DomSchoolRoleAndClassV2 activeSchoolRoleAndClass = new DomSchoolRoleAndClassV2();
        DomRole role = new DomRole();
        role.setRoleName(RoleType.ANONYMOUS.name());
        role.setId(null);
        activeSchoolRoleAndClass.setRole(role);
        DomHasRole hasRole = new DomHasRole();
        activeSchoolRoleAndClass.setHasRole(hasRole);
        dwoGlobalVars.setActiveSchoolRoleAndClass(activeSchoolRoleAndClass );
 
        eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_GUEST));
      }
    }
    
    @JsMethod public void hyperlink(String tag) {
    	LOG.info("goto #" + tag);
        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
        String base = Location.createUrlBuilder().buildString();
        base = URL.encodeQueryString(base);
        if ("default".equals(locale) ) locale =  "nl";
    	if ("FORGOT".equals(tag)) {
    		Location.assign(
    				"/dwo/rest/public/user/requestNewPassword?language="+ locale + "&back=" + base
    		);
    		return;
    	}
    	if ("REGISTER".equals(tag)) {
    		Location.assign(
    				"/dwo/register/?locale=" + locale + "&next=" + base
    				);
    		return;
    	}
    }

	@Override
	public Promise<Void> call(Promise<DomDwoProfileFull> resolved) throws Exception {
		DomDwoProfile p = resolved.getValue();
		if (p.getDwoProfileRights().contains("l"))
			view.hideGuest();
		return null;
	}
}
