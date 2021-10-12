package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;

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
    private Failure fail;
    /**
     *
     * @param view
     * @param user
     */
    AddSchoolClassStudentController(AddSchoolClassStudentPanel view, DomUserFull user, DomContext context, Failure fail) {
        this.view = view;
        this.context = context;
        this.fail = fail;
        init(user);
    }

    /**
     *
     * @param user
     */
    public void init(DomUserFull user) {
        currentUser = user;
        LOG.log(Level.INFO,""+manager);
        manager.getSchoolsClasses(context) .then(
          p-> {
            LOG.log(Level.INFO, "Fetched students schoolclasses.");
            schoolClasses = p.getValue();
            view.setSchoolClasses(schoolClasses);                
           return p;
          }).then(null, fail);
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
	 * @deprecated
     */
    public void setActiveSchoolClass(DomSchoolClass submit,AsyncCallback<Boolean> callBack) {
        manager.setActiveSchoolClass(context, submit).then(p -> {callBack.onSuccess(p.getValue());return p;}, p-> callBack.onFailure(p.getFailure()));
    }
    
    /**
     *
     * @param callBack
     * @deprecated
     */
    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack){
        manager.getActiveSchoolClass(context).then(p -> {callBack.onSuccess(p.getValue());return p;}, p-> callBack.onFailure(p.getFailure()));
    }
    
    /**
     *
     * @param submit
     * @param callBack
     * @deprecated
     */
    public void removeSchoolClass(DomSchoolClass submit, AsyncCallback<Boolean> callBack) {
        manager.removeSchoolClass(context, submit).then(p -> {callBack.onSuccess(p.getValue());return p;}, p-> callBack.onFailure(p.getFailure()));
    }

    /**
     *
     * @param submit
     * @param callBack
     * @deprecated
     */
    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        manager.registerStudentForSchoolClass(context, submit).then(p -> {callBack.onSuccess(p.getValue());return p;}, p-> callBack.onFailure(p.getFailure()));
    }
}
