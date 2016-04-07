package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Window;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class ProfileProperties {
    private static final Logger LOG = Logger.getLogger(ProfileProperties.class.getName());    

    private DomUserFull user;

    public void init() throws Dwo2Exception {
        LOG.log(Level.WARNING,"ProfileProperties.Init Not Implemented.");

        //user = SecureUserAccountManager.getAccountData();
    }

    /**
     * Update the user.
     *
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public void Update() throws Dwo2Exception {
        LOG.log(Level.WARNING,"ProfileProperties.Update Implemented.");
//                Window.alert("init not implemented.");
        //user = SecureUserAccountManager.updateAccountData(user);
        // update local Global storage.
        //DwoHelper.updateCurrentUser(user);
            //TODO above method currently updates the login date, this should not occur for this function.
    }

    /**
     * @return the user
     */
    public DomUserFull getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DomUserFull user) {
        this.user = user;
    }
}
