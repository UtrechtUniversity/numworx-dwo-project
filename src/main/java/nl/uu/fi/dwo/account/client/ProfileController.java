package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.client.RestRPCHandler;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.Map;
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
    private RestRPCHandler handler = new RestRPCHandler();

    public void init(DomUserFull user) {
        currentUser = user;
    }

    /**
     * Update the currentUser.
     *
     */
    public void update() {
        handler.login(currentUser.getUserName(), currentUser.getPassword(), new AsyncCallback<Map<String, Object>>() {

                        @Override
                        public void onFailure(Throwable t) {
                            LOG.log(Level.SEVERE, "failed!",t);
                        }

                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            LOG.log(Level.INFO, "success!");
                            for(String key : result.keySet()){
                                LOG.log(Level.INFO,result.get(key).toString());
                            }
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
