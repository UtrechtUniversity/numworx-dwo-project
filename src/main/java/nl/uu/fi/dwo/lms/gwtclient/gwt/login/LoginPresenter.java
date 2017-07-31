package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
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

    public LoginPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        init();
    }

    final public void init() {

    }

    public void loginClicked(String user, String password, Boolean switchRole) {
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = dwoGlobalVars.initUser(user, password);
            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Void>() {
                @Override
                public Promise<Void> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
                        LOG.log(Level.INFO, "login succeeded for user:" + dwoGlobalVars.getCurrentUser().getUniqueDisplayName());
                        if (switchRole) {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS));
                        } else {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_RESULTS));
                        }
                        LOG.log(Level.INFO, "login succeeded. Firing Login success event.");
                    } else {
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                        eventBus.fireEvent(new DialogEvent(("Login failed, unknown usercode and password combination.") ));
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
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                  } else {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError,fail.getMessage())));
//                        eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                        //throw directly
                    }
                }
            }
            ); //                    .onResolve(new Runnable() {
                    //                public void run() {
                    //                    System.out.println("Need tot test onResolve and fill data here! Calling stuff to get results promise here!");
                    //                }
                    //            }
//         );
        } catch (Dwo2Exception ex) {
            Logger.getLogger(LoginPresenter.class.getName()).log(Level.SEVERE, null, ex);
            eventBus.fireEvent(new DialogEvent(ex));
        }
    }

}
