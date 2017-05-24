package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import org.osgi.util.promise.Promise;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class BootPanelController implements ValueChangeHandler{

    private static final Logger LOG = Logger.getLogger(BootPanelController.class.getName());

    private final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
    EventBus eventBus;
    RootPanel rootPanel;

    BootPanelController(EventBus eventBus) {
        this.eventBus=eventBus;
    }

    public void init(RootPanel rootPanel) {
        this.rootPanel = rootPanel;
    }

//    public Promise<DomSchoolRoleAndClass> switchToSchoolLogin(DomSchoolRoleAndClass sc) {

//    public void login(....){
        //}
 //   }
    
    public Promise<DomLoginContext> logout(){
        return accountManager.logout();
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
