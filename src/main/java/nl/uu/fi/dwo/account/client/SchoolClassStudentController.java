package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.DwoGlobalVars;
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
    private List<DomSchoolClass> schoolClasses;
    private SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();

    SchoolClassStudentController(SchoolClassStudentPanel view, DomUserFull user) {
        this.view = view;
        init(user);
    }

    public void init(DomUserFull user) {
        currentUser = user;
        updateUser = currentUser.duplicate();
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

    public void getSchoolClasses() {
        manager.getStudentsSchoolClasses(new AsyncCallback<List<DomSchoolClass>>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
            }

            @Override
            public void onSuccess(List<DomSchoolClass> result) {
                //success and set all the data in the view
                LOG.log(Level.INFO, "Fetched students schoolclasses.");
                schoolClasses = result;
                //update Globals otherwise can't login in passwd change!
                DwoGlobalVars.instance().setSchoolClasses(schoolClasses);
                view.init(schoolClasses);
                view.getPopup().hide();
            }
        });
        do modal popup, no click outside allowed, without timeout. then success then close and relogin
                when fail then close and exit.
    }
}
