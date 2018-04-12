package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
class AddSchoolClassStudentController {

    private static final Logger LOG = Logger.getLogger(AddSchoolClassStudentController.class.getName());

    private final AddSchoolClassStudentPanel view;
    private final DomContext context;
    private DomUserFull currentUser;
    private List<DomSchoolClass> schoolClasses = new ArrayList<DomSchoolClass>();    
    private SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();

    /**
     *
     * @param view
     * @param user
     */
    AddSchoolClassStudentController(AddSchoolClassStudentPanel view, DomUserFull user, DomContext context) {
        this.view = view;
        this.context = context;
        init(user);
    }

    /**
     *
     * @param user
     */
    public void init(DomUserFull user) {
        currentUser = user;
        LOG.log(Level.INFO,""+manager);
        manager.getSchoolsClasses(context, new AsyncCallback<List<DomSchoolClass>>() {
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

    /**
     *
     * @return
     */
    public List<DomSchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    /**
     *
     * @param submit
     * @param callBack
     */
    public void setActiveSchoolClass(DomSchoolClass submit,AsyncCallback<Boolean> callBack) {
        manager.setActiveSchoolClass(context, submit, callBack);
    }
    
    /**
     *
     * @param callBack
     */
    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack){
        manager.getActiveSchoolClass(context, callBack);
    }
    
    /**
     *
     * @param submit
     * @param callBack
     */
    public void removeSchoolClass(DomSchoolClass submit, AsyncCallback<Boolean> callBack) {
        manager.removeSchoolClass(context, submit, callBack);
    }

    /**
     *
     * @param submit
     * @param callBack
     */
    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        manager.registerStudentForSchoolClass(context, submit, callBack);
    }
}
