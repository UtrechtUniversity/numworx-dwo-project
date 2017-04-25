package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.user.client.Window;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
class BootPanelHandler {

    private static final Logger LOG = Logger.getLogger(BootPanelHandler.class.getName());

    private BootPanel view;
    private BootPanelController controller = new BootPanelController();
    BootPanelHandler(BootPanel view) {
        this.view = view;
        init();
    }

    public void init() {

    }

    public void logoutClicked() {
        Promise<DomLoginContext> promResults;
        promResults= controller.logout();
        promResults.then(new Success<DomLoginContext, Void>() {
                @Override
                public Promise<Void> call(Promise<DomLoginContext> resolved) throws Exception {
                    //resolved means, not logged out
                    view.logoutFailed();
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    //failure means logged out, success, show login screen
                    view.logoutSuccess();
                    
                    
                }
            });   
    }

}
