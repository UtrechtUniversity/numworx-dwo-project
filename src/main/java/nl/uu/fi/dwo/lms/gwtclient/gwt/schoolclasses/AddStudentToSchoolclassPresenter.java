package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AddStudentToSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(AddStudentToSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Display view;
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> students = new HashMap();
    private List<DomStudent> studentsInClass;

    public interface Display extends BasicDisplay {
//        void setSchoolClass(DomSchoolClassFull schoolClass);
        void showStudents(Map<String, DomStudent> students);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

        void setSchoolClass(DomSchoolClass schoolClass);
    }

    public AddStudentToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;        
    }

    public void init(DomSchoolClass aSchoolClass) {
        view.clear();
        view.init();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#addStudentToClass"));
        view.setEmptyTableMessage();
        schoolClass = aSchoolClass;
        view.setSchoolClass(schoolClass);
        updateViewData();
    }

    private void updateViewData() {
        view.setLoadingTableMessage();
        Promise<List<DomStudent>> promise;
        promise = manager.getTeachersStudents();
        promise.then(new Success<List<DomStudent>, List<DomStudent>>() {
            @Override
            public Promise<List<DomStudent>> call(Promise<List<DomStudent>> resolved) throws Exception {
                students = new HashMap<>(resolved.getValue().size());
                resolved.getValue().forEach((k -> students.put(k.getId().getIdString(), k)));
                return manager.getStudentsInSchoolClass(schoolClass);
            }
        }).then(new Success<List<DomStudent>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                List<DomStudent> cantAdd = resolved.getValue();

                cantAdd.forEach((v) -> {
                    if (students.containsKey(v.getId().getIdString())) {
                        students.remove(v.getId().getIdString());
                    }
                });
                view.showStudents(students);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
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
//
//    @JsMethod
//    public void FindStudentsOfTeacher(String username, String Firstname, String insertion, String familyName, String email) {
//        Promise<List<DomStudent>> promise;
//        promise = manager.getTeachersStudents();
//        //TODO add get TeachersStudents() in gwt-lib;
//        // onSuccess update view
//        promise.then(new Success<List<DomStudent>, Void>() {
//            @Override
//            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
//                Map<String, DomStudent> studentMap = new HashMap<>();
//                resolved.getValue().forEach((k -> studentMap.put(k.getId().getIdString(), k)));
//                view.showStudents(studentMap);
//                return null;
//            }
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                Throwable fail = resolved.getFailure();
//                if (fail instanceof Dwo2Exception) {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    eventBus.fireEvent(new MessageDialogWithOKEvent((Dwo2Exception) fail));
//                } else {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    eventBus.fireEvent(new MessageDialogWithOKEvent(fail.getMessage()));
//                    //throw directly
//                }
//            }
//        }
//        );
//    }

    @JsMethod
    public void AddStudentToSchoolClass(String studentId) {
        LOG.log(Level.INFO, "Adding student " + studentId + " to schoolclass" + schoolClass.getId().getIdString());
        Promise<Boolean> promise;
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassFrom(schoolClass);
        submit.setSchoolClassTo(schoolClass);
        submit.setStudent(students.get(studentId));
        promise = manager.submitStudentToSchoolClass(submit);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                eventBus.fireEvent(new MessageDialogWithOKEvent("Success"));
                updateViewData();
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }
}
