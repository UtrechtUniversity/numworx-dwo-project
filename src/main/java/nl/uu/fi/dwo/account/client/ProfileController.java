package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class ProfileController {

    private static final Logger LOG = Logger.getLogger(ProfileController.class.getName());

    private DomUserFull currentUser;
    private DomUserFull updateUser;
    private SecuredUserAccountManager handler = new SecuredUserAccountManager();

    public void init(DomUserFull user) {
        currentUser = user;
    }

    /**
     * Update the currentUser.
     *
     */
    public void update(final AsyncCallback<Boolean> callback) {
        LOG.log(Level.INFO, "Calling REST-interface login.");
//        handler.login(currentUser.getUserName(), currentUser.getPassword(), callback);
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

    /**
     * @return the updateUser
     */
    public DomUserFull getUpdateUser() {
        return updateUser;
    }

    /**
     * @param updateUser the updateUser to set
     */
    public void setUpdateUser(DomUserFull updateUser) {
        this.updateUser = updateUser;
    }
}
