package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManager;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolLoginController {

    private static final Logger LOG = Logger.getLogger(SchoolLoginController.class.getName());

    private SchoolLoginPanel view = null;
    private DomUserFull currentUser = null;
    private SecuredUserSchoolLoginManager manager = new SecuredUserSchoolLoginManager();
    private DomSchoolsRolesAndClasses srcs;

    public DomSchoolsRolesAndClasses getSrcs() {
        return srcs;
    }

    public SchoolLoginController(SchoolLoginPanel view, DomUserFull user) throws Dwo2Exception {
        this.view = view;
        this.init(user);
    }

    public void init(DomUserFull user) throws Dwo2Exception {
        setCurrentUser(user);
        manager.getSchoolLogins(new AsyncCallback<DomSchoolsRolesAndClasses>() {
            @Override
            public void onFailure(Throwable t) {
                view.init(currentUser);
            }

            @Override
            public void onSuccess(DomSchoolsRolesAndClasses result) {
                //success and set all the data in the view
                srcs = result;
                view.update(srcs);
            }
        }
        );
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(DomUserFull currentUser) {
        this.currentUser = currentUser;
    }
}
