package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

public class CopyOrMoveStudentToSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(CopyOrMoveStudentToSchoolclassPresenter.class.getName());

    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private CopyOrMoveStudentToSchoolclassPresenter.Display view;
    private Map<String, DomSchoolClass> classMap;
    private Map<String, DomStudent> studentMapA;
    private Map<String, DomStudent> studentMapB;
    private DomSchoolClass schoolClassA;
    private DomSchoolClass schoolClassB;
    //private Map<String, DomStudent> students = new HashMap();

    public interface Display {

        void clear();

        void init();

        void showStudentsClassA(Map<String, DomStudent> students);

        void showStudentsClassB(Map<String, DomStudent> students);

        void setEmptyTableMessageClasses();

        void setLoadingTableMessageClasses();

        void setEmptyTableMessageA();

        void setLoadingTableMessageA();

        void setEmptyTableMessageB();

        void setLoadingTableMessageB();

        void SetClassA(DomSchoolClass schoolClass);

        void SetClassB(DomSchoolClass schoolClass);

        void SetClassList(List<DomSchoolClass> classList);

    }

        /**
     * @param view the view to set
     */
    public void setView(CopyOrMoveStudentToSchoolclassPresenter.Display view) {
        this.view = view;
    }
    
    public CopyOrMoveStudentToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init(DomSchoolClass aSchoolClass) {
        view.init();
        view.setEmptyTableMessageClasses();
        view.setEmptyTableMessageA();
        view.setEmptyTableMessageB();
        schoolClassA = aSchoolClass;
        refreshViewData();
    }

    @JsMethod
    private void refreshViewData() {
        refreshClassList();
        refreshClassTableA();
        refreshClassTableB();
    }

    private void refreshClassList() {
        view.setLoadingTableMessageClasses();
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                classMap = new HashMap<>();
                resolved.getValue().forEach((k -> classMap.put(k.getId().getIdString(), k)));
                view.SetClassList(resolved.getValue());
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.setEmptyTableMessageClasses();
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

    private void refreshClassTableA() {
        if (schoolClassA != null) {
            view.setLoadingTableMessageA();
            Promise<List<DomStudent>> promiseA;
            promiseA = manager.getStudentsInSchoolClass(schoolClassA);
            promiseA.then(new Success<List<DomStudent>, Void>() {
                @Override
                public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                    //flip back to schoolclasses screen 
                    studentMapA = new HashMap<>();
                    resolved.getValue().forEach((k -> studentMapA.put(k.getId().getIdString(), k)));
                    view.showStudentsClassA(studentMapA);
//                view.setSchoolClass(schoolClass);
                    return null;
                }

            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    view.setEmptyTableMessageA();
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
        } else {
            view.setEmptyTableMessageA();
        }
    }

    private void refreshClassTableB() {
        if (schoolClassB != null) {
            view.setLoadingTableMessageB();
            Promise<List<DomStudent>> promiseB;
            promiseB = manager.getStudentsInSchoolClass(schoolClassB);
            promiseB.then(new Success<List<DomStudent>, Void>() {
                @Override
                public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                    //flip back to schoolclasses screen 
                    studentMapB = new HashMap<>();
                    resolved.getValue().forEach((k -> studentMapB.put(k.getId().getIdString(), k)));
                    view.showStudentsClassB(studentMapB);
//                view.setSchoolClass(schoolClass);
                    return null;
                }

            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    view.setEmptyTableMessageB();
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
        } else {
            view.setEmptyTableMessageB();
        }
    }

    @JsMethod
    public void SelectClassB(String classId) {
        schoolClassB = classMap.get(classId);
        if (schoolClassB != null) {
            view.SetClassB(schoolClassB);
            refreshClassTableB();
        }
    }

    @JsMethod
    public void CopyStudentsToClassA(String a) {
        LOG.log(Level.INFO,"input is "+a);
        List<String> idList=null;
        //show progress dialog
        CopyStudent(idList, 0, schoolClassB, schoolClassA);
        //close progress dialog
    }

    /**
     * Tail recursion.
     *
     * @param idList
     * @param i
     * @param from
     * @param to
     */
    public void CopyStudent(List<String> idList, int i, DomSchoolClass from, DomSchoolClass to) {
        Promise<Boolean> promise;
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassFrom(from);
        submit.setSchoolClassTo(to);
        DomStudent s;
        if (from.equals(schoolClassB)) {
            s = studentMapB.get(idList.get(i));
        } else {
            s = studentMapA.get(idList.get(i));
        }
        submit.setStudent(s);
        final int next = i + 1;
        promise = manager.submitStudentToSchoolClass(submit);
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                LOG.log(Level.INFO, "Copying student "+idList.get(next)+".");
                CopyStudent(idList, next, from, to);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                //close progress panel.
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

    @JsMethod
    public void CopyStudentsToClassB(List<String> idList) {
        CopyStudent(idList, 0, schoolClassA, schoolClassB);
    }

    @JsMethod
    public void MoveStudentsToClassA(List<String> idList) {
        //Add to Class A
        MoveStudent(idList, 0, schoolClassB, schoolClassA);
    }

    @JsMethod
    public void MoveStudentsToClassB(List<String> idList) {
        //Add to Class B
        MoveStudent(idList, 0, schoolClassA, schoolClassB);
    }

    public void MoveStudent(List<String> idList, int i, DomSchoolClass from, DomSchoolClass to) {
        Promise<Boolean> promise;
        DomMoveStudentToSchoolClass submit = new DomMoveStudentToSchoolClass();
        submit.setSchoolClassFrom(from);
        submit.setSchoolClassTo(to);
        DomStudent s;
        if (from.equals(schoolClassB)) {
            s = studentMapB.get(idList.get(i));
        } else {
            s = studentMapA.get(idList.get(i));
        }
        submit.setStudent(s);
        final int next = i + 1;
        promise = manager.moveStudentToSchoolClass(submit);
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                LOG.log(Level.INFO, "Moving student "+idList.get(next)+".");
                CopyStudent(idList, next, from, to);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                //close progress panel.
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
