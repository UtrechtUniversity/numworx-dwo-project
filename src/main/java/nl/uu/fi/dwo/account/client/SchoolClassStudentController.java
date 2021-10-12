package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
class SchoolClassStudentController {

    private static final Logger LOG = Logger.getLogger(SchoolClassStudentController.class.getName());

    private final SchoolClassStudentPanel view;
    private DomUserFull currentUser;
    private List<DomSchoolClass> schoolClasses = new ArrayList<DomSchoolClass>();
    private SecuredStudentSchoolClassManager manager = new SecuredStudentSchoolClassManager();
    private SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
    private AddSchoolClassStudentPanel addSchoolClassView;

	private final DomContext context;

	private Failure failure;

    /**
     *
     * @param view
     * @param user
     */
    SchoolClassStudentController(SchoolClassStudentPanel view, DomUserFull user, DomContext context, Failure fail) {
        this.view = view;
        this.context = context;
        this.failure = fail;
        init(user);
    }

    /**
     *
     * @param user
     */
    public void init(DomUserFull user) {
        currentUser = user;
        LOG.log(Level.INFO, "" + manager);
        updateStudentsSchoolClassesInView();
    }

//    /**
//     *
//     * @param callBack
//     * @deprecated
//     */
//    public void getCurrentSchoolRoleAndClass(AsyncCallback<DomSchoolsRolesAndClassesV2> callBack) {
//        loginManager.getSchoolLoginsV2(callBack);
//    };
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
     */
  public void updateStudentsSchoolClassesInView() {
    manager.getStudentsSchoolClasses(context).then(p -> {
      LOG.log(Level.INFO, "Fetched students schoolclasses.");
      schoolClasses = p.getValue();
      view.setSchoolClasses(schoolClasses);
      return p;
    }, failure);

  }

    /**
     *
     */
    public void updateSchoolClassesAddSchoolClassView() {
        manager.getSchoolsClasses(context)
        .then(p->{
          LOG.log(Level.INFO, "Fetched schoolclasses in students school.");
          List<DomSchoolClass> unregisteredClasses = new ArrayList<>(p.getValue().size() - schoolClasses.size());
          for (DomSchoolClass c : p.getValue()) {
              boolean flag = true; //add teacher to result list
              for (DomSchoolClass sc : schoolClasses) {
                  if (sc.getId().equals(c.getId())) {
                      flag = false;
                      break;
                  }
              }
              if (flag) {
                  unregisteredClasses.add(c);
              }
          }
          LOG.log(Level.INFO, "Updating unregistered schoolclasses add schoolclass panel.");
          addSchoolClassView.setSchoolClasses(unregisteredClasses);
          return p;}, failure);
    }

    /**
     *
     * @param submit
     * @param callBack
     * @return 
     */
    public Promise<Boolean> setActiveSchoolClass(DomSchoolClass submit) {
        return manager.setActiveSchoolClass(context, submit);
    }

    /**
     *
     * @param callBack
     * @deprecated
     */
    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack) {
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

    /**
     * @return the addSchoolClassView
     */
    public AddSchoolClassStudentPanel getAddSchoolClassView() {
        return addSchoolClassView;
    }

    /**
     * @param addSchoolClassView the addSchoolClassView to set
     */
    public void setAddSchoolClassView(AddSchoolClassStudentPanel addSchoolClassView) {
        this.addSchoolClassView = addSchoolClassView;
    }
}
