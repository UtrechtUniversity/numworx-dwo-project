package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
class ProfileController {

    private static final Logger LOG = Logger.getLogger(ProfileController.class.getName());

    private ProfilePanel view = null;
    private DomUserFull currentUser = null;
    private DomUserFull updateUser = null;
    private SecuredUserAccountManager manager = new SecuredUserAccountManager();

    public ProfileController(ProfilePanel view, DomUserFull user) {
        this.view = view;
        this.init(user);
    }

    public void init(DomUserFull user) {
        currentUser = user;
        updateUser = currentUser.duplicate();
    }

    /**
     * Update the currentUser.
     *
     * @param callback
     */
    public void callUpdate() {
        LOG.log(Level.INFO, "Calling REST-interface login.");
        manager.updateAccountData(updateUser, new AsyncCallback<DomUserFull>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
                Window.alert(t.getMessage());
            }

            @Override
            public void onSuccess(DomUserFull result) {
                //success and set all the data in the view
                    LOG.log(Level.INFO, "update was succesful.");
                    currentUser = result;
                    updateUser = currentUser.duplicate();
                    //update Globals otherwise can't loginUser in passwd change!
                    DwoGlobalVars.instance().setCurrentUser(currentUser);
                    //update rest authentication 
                    DwoGlobalVars.instance().getAuthenticator()
                            .setCredentials(currentUser.getUserName(), currentUser.getPassword());
                    view.init(currentUser);
                    view.getPopup().hide();
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
     * @param aCurrentUser
     */
    public void setCurrentUser(DomUserFull aCurrentUser) {
        currentUser = aCurrentUser;
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
