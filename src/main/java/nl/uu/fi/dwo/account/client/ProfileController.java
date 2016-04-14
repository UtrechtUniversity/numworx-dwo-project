package nl.uu.fi.dwo.account.client;

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

    public void init(DomUserFull user) {
        currentUser = user;
    }

    /**
     * Update the currentUser.
     *
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public void update()  {
        LOG.log(Level.WARNING,"ProfileProperties.Update Implemented.");
//                Window.alert("init not implemented.");
        //user = SecureUserAccountManager.updateAccountData(updateUser);
        // update local Global storage.
        //DwoHelper.updateCurrentUser(currentUser);
            //TODO above method currently updates the login date, this should not occur for this function.
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
