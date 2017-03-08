package nl.uu.fi.dwo.account.client.boot;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
class LoginPanelHandler {

    private static final Logger LOG = Logger.getLogger(LoginPanelHandler.class.getName());

    private LoginPanel view;

    LoginPanelHandler(LoginPanel view) {
        this.view = view;
        init();
    }

    public void init() {

    }

    public void loginClicked(String user, String password) {
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = DwoGlobalVars.instance().initUser(user, password);
            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Void>() {
                @Override
                public Promise<Void> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
                        LOG.log(Level.INFO, "login succeeded for user:" + DwoGlobalVars.instance().getCurrentUser());
                        view.onLoginSuccess();
                    } else {
                        view.onLoginFailure("Illegal credentials.");
                    }
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    view.onLoginFailure(resolved.getFailure().getMessage());
                }
            }
            );
        } catch (Dwo2Exception ex) {
            Logger.getLogger(LoginPanelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
