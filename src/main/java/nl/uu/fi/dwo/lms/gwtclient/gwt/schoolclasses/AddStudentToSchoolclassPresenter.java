package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
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
    private DomSchoolClassFull schoolClass;
    private Map<String, DomStudent> students = new HashMap();

    public interface Display {

        void clear();

        void init();

//        void setSchoolClass(DomSchoolClassFull schoolClass);

        void showStudents(Map<String, DomStudent> students);
        
        void setEmptyTableMessage();

        void setsetLoadingTableMessage();
    }

    public AddStudentToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
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
//                view.setSchoolClass(schoolClass);
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

    @JsMethod
    public void FindStudentsOfTeacher(String username, String Firstname, String insertion, String familyName, String email) {
        Promise<List<DomStudent>> promise;
        promise = manager.getTeachersStudents();
        //TODO add get TeachersStudents() in gwt-lib;
        // onSuccess update view
        promise.then(new Success<List<DomStudent>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                Map<String, DomStudent> studentMap = new HashMap<>();
                resolved.getValue().forEach((k -> studentMap.put(k.getId().getIdString(), k)));
                view.showStudents(studentMap);
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
        public void AddStudentToSchoolClass(String studentId){
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
}
