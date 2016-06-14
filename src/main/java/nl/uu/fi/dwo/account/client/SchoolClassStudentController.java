package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.ArrayList;
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
    private List<DomSchoolClass> schoolClasses = new ArrayList<DomSchoolClass>();    
    private SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();

    SchoolClassStudentController(SchoolClassStudentPanel view, DomUserFull user) {
        this.view = view;
        init(user);
    }

    public void init(DomUserFull user) {
        currentUser = user;
        LOG.log(Level.INFO,""+manager);
        manager.getStudentsSchoolClasses(new AsyncCallback<List<DomSchoolClass>>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
                LOG.log(Level.INFO,t.getMessage());
                DwoViewer.showMessage(Dwo2ExceptionCode.Rest_ConnectionTimeout);
            }

            @Override
            public void onSuccess(List<DomSchoolClass> result) {
                //success and set all the data in the view
                LOG.log(Level.INFO, "Fetched students schoolclasses.");
                schoolClasses = result;
                view.setSchoolClasses(schoolClasses);                
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

    public List<DomSchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    public void setActiveSchoolClass(DomSchoolClass submit,AsyncCallback<Boolean> callBack) {
        manager.setActiveSchoolClass(submit, callBack);
    }
    
    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack){
        manager.getActiveSchoolClass(callBack);
    }
    public void removeSchoolClass(DomSchoolClass submit, AsyncCallback<Boolean> callBack) {
        manager.removeSchoolClass(submit, callBack);
    }
}
