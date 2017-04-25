package nl.uu.fi.dwo.account.client.boot;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import org.osgi.util.promise.Promise;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class BootPanelController {

    private static final Logger LOG = Logger.getLogger(BootPanelController.class.getName());

    private final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();

    BootPanelController() {
        init();
    }

    private void init() {

    }

//    public Promise<DomSchoolRoleAndClass> switchToSchoolLogin(DomSchoolRoleAndClass sc) {

//    public void login(....){
        //}
 //   }
    
    public Promise<DomLoginContext> logout(){
        return accountManager.logout();
    }
}
