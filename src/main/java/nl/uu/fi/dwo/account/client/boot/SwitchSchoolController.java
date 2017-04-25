package nl.uu.fi.dwo.account.client.boot;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import org.osgi.util.promise.Promise;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class SwitchSchoolController {

    private static final Logger LOG = Logger.getLogger(SwitchSchoolController.class.getName());

    private SecuredUserAccountManager manager = new SecuredUserAccountManager();

    SwitchSchoolController() {
        init();
    }

    private void init() {

    }

//    public Promise<DomSchoolRoleAndClass> switchToSchoolLogin(DomSchoolRoleAndClass sc) {

//    public void login(....){
        //}
 //   }
    
    public Promise<DomUserFullwLoginContext> login(String user, String password){
        return manager.login(user, password);
    }
}
