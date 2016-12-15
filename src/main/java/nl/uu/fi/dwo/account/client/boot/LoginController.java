package nl.uu.fi.dwo.account.client.boot;

import nl.uu.fi.dwo.account.client.boot.Results.*;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import org.osgi.util.promise.Promise;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class LoginController {

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    private LoginPanel view;
    private SecuredTeacherResultsManager manager = new SecuredTeacherResultsManager();

    LoginController(LoginPanel view) {
        this.view = view;
        init();
    }

    public void init() {

    }

//    public Promise<DomSchoolRoleAndClass> switchToSchoolLogin(DomSchoolRoleAndClass sc) {

//    public void login(....){
        //}
 //   }
}
