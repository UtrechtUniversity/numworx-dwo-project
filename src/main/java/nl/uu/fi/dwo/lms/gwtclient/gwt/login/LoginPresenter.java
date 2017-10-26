package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

public class LoginPresenter {

    private static final Logger LOG = Logger.getLogger(LoginPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    

    public interface Display extends IsWidget {

        Widget asWidget();

        void clear();

        public void setUsername(String username);

        public void setPassword(String password);
    }

    //** should become part of the PresenterFactories ie. DWO.LoginPresenter.loginClicked.
        private native static void setDWO(DwoGlobalVars gv, LoginPresenter p) /*-{
    	var api = {
    			"loginTest" : function() {
    				return p.@nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter::loginClickedJS(Ljava/lang/String;Ljava/lang/String;)("gert_project", "passw")
                        },
    			"loginClicked" : function(user, password) {
    				return p.@nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter::loginClickedJS(Ljava/lang/String;Ljava/lang/String;)(user, password)
                        },
    			"getServer" : function() {
    				return gv.@nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars::getServer()
                        }
    		};
    	$wnd.DwoLoginPresenter = api;
    }-*/;
                
    public LoginPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        this.setDWO(dwoGlobalVars,this);
        init();
    }

    
    final public void init() {
    }
    
public String loginClickedJS(String user, String password) {
        this.loginClicked(user, password, true);
        return "done";
    }
 
    public void loginClicked(String user, String password, final Boolean switchRole) {
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = dwoGlobalVars.initUser(user, password);
            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Void>() {
                @Override
                public Promise<Void> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
                        boolean switchR = true;
                        LOG.log(Level.INFO, "login succeeded for user:" + dwoGlobalVars.getCurrentUser().getUniqueDisplayName());
                        try {
                            if (dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER.name())) {
                                switchR = false;
                            }
                        } catch (Exception e) {
                            switchR = true;
                        }
                        if (switchR || switchRole) {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS));
                        } else {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_SCHOOLCLASSES));
                        }
                        LOG.log(Level.INFO, "login succeeded. Firing Login success event.");
                    } else {
                        dwoGlobalVars.clearCurrentUser();
                        eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, "Wrong login state.")));
                        // TODO fix login stuff
//                        Window.Location.assign("");
                        LOG.log(Level.INFO, "login failed. Firing Login fail event.");

                    }
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    if (fail instanceof Dwo2Exception) {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        //note the order of the events in case ofan exception
                        //that might break the running thread.
                        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                    } else {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, fail.getMessage())));
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                    }
                }
            }
            );
        } catch (Dwo2Exception ex) {
            Logger.getLogger(LoginPresenter.class.getName()).log(Level.SEVERE, null, ex);
            eventBus.fireEvent(new DialogEvent(ex));
        }
    }

}
