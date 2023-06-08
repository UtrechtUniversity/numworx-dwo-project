package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.util.StringFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceTeacher;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
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
    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final PersonsService manager;
    private Display view;
    private DomSchoolClassFull schoolClass;
    final RoleType role;
    final Failure FAILURE;

    public interface Display extends BasicDisplay {

        void init();

        void showSchoolClass(DomSchoolClassFull schoolClass);

        void setEmptyStudentTableMessage();

        void setLoadingStudentTableMessage();

        void showStudents(List<DomStudent> students);

        void setEmptyTeacherTableMessage();

        void setLoadingTeacherTableMessage();

        void showTeachers(List<DomTeacher> teachers);

        void setEmptyModulesTableMessage();

        void setLoadingModulesTableMessage();

        void showModules(List<DomCourse> modules);
    }

    @Inject EditSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        manager = m;
        role = dwoGlobalVars.getRole();
        FAILURE = new LoggingFailure(LOG, anEventBus);

    }
//    public EditSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
//      this(anEventBus, aDwoGlobalVars, new PersonsServiceTeacher(aDwoGlobalVars));
//    }

    public void init(DomSchoolClass aSchoolClass) {
        view.clear();
        view.init();
        updateViewData(aSchoolClass);
    }

    private void updateViewData(DomSchoolClass aSchoolClass) {
        Promise<DomSchoolClassFull> promise;
        promise = manager.getFullSchoolClass(aSchoolClass);
        // onSuccess update view
        promise.then(new Success<DomSchoolClassFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomSchoolClassFull> resolved) throws Exception {
                //flip back to schoolclasses screen 
// Patch if null or ""
            	schoolClass = resolved.getValue();
                if (Boolean.FALSE.equals(schoolClass.getHasRegKey()))
                {
                	schoolClass.setRegistrationKey(""); // will set to true
                }
                if ("".equals(schoolClass.getRegistrationKey()))
                	schoolClass.setHasRegKey(Boolean.FALSE); // will set to false
                view.showSchoolClass(schoolClass);
                showStudents();
                showTeachers();
                showModules();

                return null;
            }

        }, FAILURE);
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#editSchoolclass"+role));
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
        fullSchoolClass.setRegistrationKey(hasRegKey ? regKey : null);
        promise = manager.updateSchoolClass(fullSchoolClass);
        // onSuccess calculate results and show.
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                if (resolved.getValue() == true) {
                    updateViewData(schoolClass);
                    eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_Class_ConfirmChangeCommited()));
                    return null;
                } else {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Rest request failed for unknown reasons.");
                }
            }
        }, FAILURE);
    }

    @JsMethod
    public void removeSchoolClass() {
        String msg = StringFormatter.format(DwoLocalesForGWT.instance.NUM_DLG_Class_ConfirmRemoveSchoolClass(), schoolClass.getSchoolClassName());
        AlertDialogWithConfirmCancelDeferred p = new AlertDialogWithConfirmCancelDeferred(msg);
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
        }, FAILURE
        );

        eventBus.fireEvent(new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, p));
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
                MessageDialogWithOKEvent evt = new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_Class_Removed());
                eventBus.fireEvent(evt);
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                return null;
            }

        }, FAILURE);
    }

    @JsMethod
    public void showTeachers() {
        Promise<List<DomTeacher>> promise;
        view.setLoadingTeacherTableMessage();
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
                view.setEmptyTeacherTableMessage();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                    //throw directly
                }
            }
        }
        );
    }

    @JsMethod
    public void showStudents() {
        Promise<List<DomStudent>> promise;
        view.setLoadingStudentTableMessage();
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
                view.setEmptyStudentTableMessage();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                    //throw directly
                }
            }
        }
        );
    }

    @JsMethod
    public void showModules() {
      if (role != RoleType.TEACHER) return;
        Promise<DomCoursesOfSchoolClass4Teacher> promise;
        DomCoursesOfSchoolClass4Teacher result;
        view.setLoadingModulesTableMessage();
        promise = this.getModules(schoolClass);
        // onSuccess update view
        promise.then(new Success<DomCoursesOfSchoolClass4Teacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomCoursesOfSchoolClass4Teacher> resolved) throws Exception {
                DomCoursesOfSchoolClass4Teacher moduleData = resolved.getValue();
                List<DomCourse> courseList = new ArrayList<>();
                Map<String, DomCourse> courseMap = new HashMap<>();
                moduleData.getCourses().forEach((k) -> courseMap.put(k.getKey().getIdString(), k.getValue()));
                Map<String, DomClassCourse4Teacher> ccMap = new HashMap<>();
                moduleData.getClassCourses().forEach((k) -> {
                    if (k.getValue().getViewState() == ViewState.studentsAndTeachers && courseMap.get(k.getValue().getCourseId().getIdString()) != null && !courseMap.get(k.getValue().getCourseId().getIdString()).getWithChildren()) {
                        courseList.add(courseMap.get(k.getValue().getCourseId().getIdString()));
                        LOG.log(Level.INFO, "Course attached to class: " + courseMap.get(k.getValue().getCourseId().getIdString()).getName());
                    }
                });
                view.showModules(courseList);
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.setEmptyModulesTableMessage();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
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
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.COPYORMOVESTUDENTTOSCHOOLCLASS, schoolClass));
    }

    @JsMethod
    public void connectTeachers() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDTEACHERTOSCHOOLCLASS, schoolClass));
    }

    @JsMethod
    public void editModules() {
      if (role == RoleType.TEACHER)
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITCOURSESOFSCHOOLCLASS, schoolClass));
    }
}
