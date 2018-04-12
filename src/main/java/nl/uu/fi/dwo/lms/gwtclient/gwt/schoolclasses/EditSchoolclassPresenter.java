package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogEvent;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogPromise;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class EditSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(EditSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Display view;
    private DomSchoolClassFull schoolClass;

    public interface Display {

        void clear();

        void init();

        void showSchoolClass(DomSchoolClassFull schoolClass);
        void showStudents(List<DomStudent> students);
        void showTeachers(List<DomTeacher> teachers);
        void showShowModels(List<DomCourse> modules);
    }

    public EditSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init() {
        view.init();
        updateViewData();
    }

    private void updateViewData() {
        Promise<DomSchoolClassFull> promise;
        promise = manager.getFullSchoolClass(schoolClass);
        // onSuccess update view
        promise.then(new Success<DomSchoolClassFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomSchoolClassFull> resolved) throws Exception {
                //flip back to schoolclasses screen 
                schoolClass = resolved.getValue();
                view.showSchoolClass(schoolClass);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    /**
     * Updates SchoolClass data.
     *
     * @param name
     * @param showTree
     * @param hasRegKey
     * @param regKey
     */
    @JsMethod
    public void updateAndRefresh(String name, Boolean showTree, Boolean hasRegKey, String regKey) {
        Promise<Boolean> promise;
        DomSchoolClassFull fullSchoolClass = new DomSchoolClassFull();
        fullSchoolClass.setId(schoolClass.getId());
        fullSchoolClass.setSchoolClassName(name);
        fullSchoolClass.setIconizer(showTree);
        fullSchoolClass.setHasRegKey(hasRegKey);
        fullSchoolClass.setRegistrationKey(regKey);
        promise = manager.updateSchoolClass(fullSchoolClass);
        // onSuccess calculate results and show.
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                if (resolved.getValue() == true) {
                    updateViewData();
                    //eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                    return null;
                } else {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Rest request failed for unknown reasons.");
                }
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    @JsMethod
    public void removeSchoolClass() {
        ConfirmDialogPromise p = new ConfirmDialogPromise("Are you sure you want to remove schoolclass" + schoolClass.getSchoolClassName() + ".");
        p.getPromise().then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                LOG.log(Level.INFO, "returned value" + resolved.getValue());
                if (resolved.getValue() == true) {
                    executeRemoveSchoolClass(schoolClass);
                } else {
                    //do nothing.
                }
                return null;
            }
        }, new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        }
        );

        eventBus.fireEvent(new ConfirmDialogEvent(ConfirmDialogEvent.EventType.ConfirmDialog, p));
    }

    private void executeRemoveSchoolClass(DomSchoolClass schoolClass) {
        Promise<Boolean> promise;
        promise = manager.removeSchoolClass(schoolClass);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                boolean result = resolved.getValue();
                if (result != true) {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "system error, try again please report.");
                }
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    @JsMethod
    public void showStudents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @JsMethod
    public void showTeachers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @JsMethod
    public void showModules() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
    
    @JsMethod
    public void connectStudents() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDSTUDENTTOSCHOOLCLASS, schoolClass));
    }
    
    @JsMethod
    public void copyOrMoveStudents() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTSINSCHOOLCLASS, schoolClass));
    }
    
    @JsMethod
    public void connectTeachers() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDTEACHERTOSCHOOLCLASS, schoolClass));
    }

    @JsMethod
    public void editModules() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.COURSESOFSCHOOLCLASS, schoolClass));
    }
}
