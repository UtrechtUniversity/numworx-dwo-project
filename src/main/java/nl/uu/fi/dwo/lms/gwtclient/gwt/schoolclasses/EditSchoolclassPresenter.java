package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogEvent;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogPromise;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
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

        void showModules(List<DomCourse> modules);
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
    public void showTeachers() {
        Promise<List<DomTeacher>> promise;
        promise = manager.getTeachersInSchoolClass(schoolClass);
        // onSuccess update view
        promise.then(new Success<List<DomTeacher>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomTeacher>> resolved) throws Exception {
                view.showTeachers(resolved.getValue());
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
        }
        );
    }

    @JsMethod
    public void showStudents() {
        Promise<List<DomStudent>> promise;
        promise = manager.getStudentsInSchoolClass(schoolClass);
        // onSuccess update view
        promise.then(new Success<List<DomStudent>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                view.showStudents(resolved.getValue());
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
        }
        );
    }

    @JsMethod
    public void showModules() {
        Promise<DomCoursesOfSchoolClass4Teacher> promise;
        DomCoursesOfSchoolClass4Teacher result;
        promise = this.getModules(schoolClass);
        // onSuccess update view
        promise.then(new Success<DomCoursesOfSchoolClass4Teacher, Void > () {
            @Override
            public Promise<Void> call
            (Promise<DomCoursesOfSchoolClass4Teacher> resolved) throws Exception {
                List<DomCourse> courseList = new ArrayList<>();
                resolved.getValue().getCourses().forEach((k -> courseList.add(k.getValue())));
                view.showModules(courseList);
                return null;
            }
        },

            new
        Failure() {
            @Override
            public void fail
            (Promise<?> resolved) throws Exception {
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
    }

    private Promise<DomCoursesOfSchoolClass4Teacher> getModules(final DomSchoolClass sc) {
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
        return dwoGlobalVars.getProfile().then(new Success<DomDwoProfile, DomCoursesOfSchoolClass4Teacher>() {

            @Override
            public Promise<DomCoursesOfSchoolClass4Teacher> call(
                    Promise<DomDwoProfile> resolved) throws Exception {
                DomSchoolClassAndProfile sap = new DomSchoolClassAndProfile();
                sap.setDomDwoProfile(resolved.getValue());
                sap.setDomSchoolClass(sc);
                return manager.getModules(context, sap);
            }
        });
    }

    @JsMethod
    public void connectStudents() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDSTUDENTTOSCHOOLCLASS, schoolClass));
    }

    @JsMethod
    public void copyOrMoveStudents() {
    eventBus
        .fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES, schoolClass));
    }

    @JsMethod
    public void connectTeachers() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDTEACHERTOSCHOOLCLASS, schoolClass));
    }

    @JsMethod
    public void editModules() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITCOURSESOFSCHOOLCLASS, schoolClass));
    }
}
