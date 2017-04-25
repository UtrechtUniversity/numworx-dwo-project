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
class SwitchSchoolPanelHandler {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolPanelHandler.class.getName());

    private SwitchSchoolPanel view;

    SwitchSchoolPanelHandler(SwitchSchoolPanel view) {
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
                        LOG.log(Level.INFO, "login succeeded for user:" + DwoGlobalVars.instance().getCurrentUser().getUniqueDisplayName());
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
            ).onResolve(new Runnable() {
                public void run() {
                    System.out.println("Need tot test onResolve and fill data here! Calling stuff to get results promise here!");
                }
            });;
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SwitchSchoolPanelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
