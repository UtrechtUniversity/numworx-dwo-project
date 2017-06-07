package nl.uu.fi.dwo.lms.gwtclient.gwt.graveyard;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoViewer;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

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
    private SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
    private AddSchoolClassStudentPanel addSchoolClassView;

    /**
     *
     * @param view
     * @param user
     */
    SchoolClassStudentController(SchoolClassStudentPanel view, DomUserFull user) {
        this.view = view;
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

    /**
     *
     * @param callBack
     */
    public void getCurrentSchoolRoleAndClass(AsyncCallback<DomSchoolsRolesAndClassesV2> callBack) {
        loginManager.getSchoolLoginsV2(callBack);
    };
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
        manager.getStudentsSchoolClasses(new AsyncCallback<List<DomSchoolClass>>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
                LOG.log(Level.INFO, t.getMessage());
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
     *
     */
    public void updateSchoolClassesAddSchoolClassView() {
        manager.getSchoolsClasses(new AsyncCallback<List<DomSchoolClass>>() {
            @Override
            public void onFailure(Throwable t) {
                //fail and reset all the data.
                LOG.log(Level.INFO, t.getMessage());
                DwoViewer.showMessage(Dwo2ExceptionCode.Rest_ConnectionTimeout);
            }

            @Override
            public void onSuccess(List<DomSchoolClass> result) {
                //success and set all the data in the view
                LOG.log(Level.INFO, "Fetched schoolclasses in students school.");
                List<DomSchoolClass> unregisteredClasses = new ArrayList<>(result.size() - schoolClasses.size());
                for (DomSchoolClass c : result) {
                    Boolean flag = true; //add teacher to result list
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
            }
        });
    }

    /**
     *
     * @param submit
     * @param callBack
     */
    public void setActiveSchoolClass(DomSchoolClass submit, AsyncCallback<Boolean> callBack) {
        manager.setActiveSchoolClass(submit, callBack);
    }

    /**
     *
     * @param callBack
     */
    public void getActiveSchoolClass(AsyncCallback<DomSchoolClass> callBack) {
        manager.getActiveSchoolClass(callBack);
    }

    /**
     *
     * @param submit
     * @param callBack
     */
    public void removeSchoolClass(DomSchoolClass submit, AsyncCallback<Boolean> callBack) {
        manager.removeSchoolClass(submit, callBack);
    }

    /**
     *
     * @param submit
     * @param callBack
     */
    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit, AsyncCallback<Boolean> callBack) {
        manager.registerStudentForSchoolClass(submit, callBack);
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
