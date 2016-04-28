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

    private ProfilePanel view;
    private DomUserFull currentUser;
    private DomUserFull updateUser;
    private SecuredUserAccountManager caller = new SecuredUserAccountManager();

    public void init(DomUserFull user) {
        currentUser = user;
    }

    /**
     * Update the currentUser.
     *
     * @param callback
     */
    public void callUpdate() {
        LOG.log(Level.INFO, "Calling REST-interface login.");
        caller.update(updateUser, new AsyncCallback<DomUserFull>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
                view.init(currentUser);
            }

            @Override
            public void onSuccess(DomUserFull result) {
                //success and set all the data in the view
                view.init(result);
                currentUser = updateUser;
                updateUser = currentUser.duplicate();
            }
        });
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
