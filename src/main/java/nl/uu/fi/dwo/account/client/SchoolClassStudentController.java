package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.GWTGlobals;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
class SchoolClassStudentController {

    private static final Logger LOG = Logger.getLogger(SchoolClassStudentController.class.getName());

    private SchoolClassStudentPanel view;
    private DomUserFull currentUser;
    private DomUserFull updateUser;
    private SecuredUserAccountManager manager = new SecuredUserAccountManager();

    SchoolClassStudentController(SchoolClassStudentPanel view, DomUserFull user){
        this.view = view;
        init(user);
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
        manager.updateAccountData(updateUser, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
                view.init(currentUser);
            }

            @Override
            public void onSuccess(Boolean result) {
                //success and set all the data in the view
                if(result == true){
                currentUser = updateUser;
                updateUser = currentUser.duplicate();
                //update Globals otherwise can't login in passwd change!
                GWTGlobals.instance().setCurUser(currentUser);
                view.init(currentUser);
                }else{
                    updateUser = currentUser.duplicate();
                    view.init(currentUser);
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

    public List<DomSchoolClass> getSchoolClasses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
