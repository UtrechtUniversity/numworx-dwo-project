package nl.uu.fi.dwo.account.client.boot;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherResultsManager;
import java.util.logging.Logger;

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
