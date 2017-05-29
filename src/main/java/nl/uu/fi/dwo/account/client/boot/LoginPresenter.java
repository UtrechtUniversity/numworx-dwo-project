package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

public class LoginPresenter {

    private static final Logger LOG = Logger.getLogger(LoginPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    public interface Display {

        Widget asWidget();

        LoginView getViewInstance();

        void clear();

        public void setUser();

        public void setPassword();
    }

    public LoginPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        init();
    }

    final public void init() {

    }

    public void loginClicked(String user, String password) {
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = DwoGlobalVars.instance().initUser(user, password);
            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Void>() {
                @Override
                public Promise<Void> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
                        LOG.log(Level.INFO, "login succeeded for user:" + DwoGlobalVars.instance().getCurrentUser().getUniqueDisplayName());
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS));
                        LOG.log(Level.INFO,"login succeeded. Firing Login success event.");
                    } else {
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                        LOG.log(Level.INFO,"login failed. Firing Login fail event.");
                    }
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                        LOG.log(Level.INFO,"login failed. Firing Login fail event.");
                }
            }
            ).onResolve(new Runnable() {
                public void run() {
                    System.out.println("Need tot test onResolve and fill data here! Calling stuff to get results promise here!");
                }
            });;
        } catch (Dwo2Exception ex) {
            Logger.getLogger(LoginPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
