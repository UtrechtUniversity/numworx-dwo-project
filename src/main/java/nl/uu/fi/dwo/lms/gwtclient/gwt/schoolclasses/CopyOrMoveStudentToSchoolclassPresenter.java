package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
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

    public interface Display extends BasicDisplay {

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
        view.clear();
        view.init();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#CopyOrMoveStudentToClass"));
        view.setEmptyTableMessageClasses();
        view.setEmptyTableMessageA();
        view.setEmptyTableMessageB();
        schoolClassA = aSchoolClass;
        view.SetClassA(aSchoolClass);
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
//                List<DomSchoolClass> classList = new ArrayList<>();
                resolved.getValue().forEach((k -> {
                    if (!k.getId().getIdString().equals(schoolClassA.getId().getIdString())) {
                        classMap.put(k.getId().getIdString(), k);
//                        resolved.getValue().remove(k);
                    }
                }));
                resolved.getValue().removeIf(k -> k.getId().getIdString().equals(schoolClassA.getId().getIdString()));
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

    /**
     * Tail recursion.
     *
     * @param idList
     * @param i
     * @param from
     * @param to
     */
    public void CopyStudent(String idList[], int i, DomSchoolClass from, DomSchoolClass to, Promise abortPromise) {
        Promise<Boolean> promise;
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassFrom(from);
        submit.setSchoolClassTo(to);
        DomStudent s;
        if (from.equals(schoolClassB)) {
            s = studentMapB.get(idList[i]);
        } else {
            s = studentMapA.get(idList[i]);
        }
        submit.setStudent(s);
        final int next = i + 1;
        promise = manager.submitStudentToSchoolClass(submit);
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                if (!abortPromise.isDone() && next < idList.length) {
                    LOG.log(Level.INFO, "Copying student " + idList[next] + ".");
                    LOG.log(Level.INFO, "Completed: " + 100.0 * next / idList.length + "%.");
                    double r = (100.0 * next / idList.length);
                    ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Update, (int) r, DwoLocalesForGWT.instance.NUM_DLG_Class_CopyingStudents(), null);
                    eventBus.fireEvent(e);
                    CopyStudent(idList, next, from, to, abortPromise);
                } else {
                    //hide progressbar.
                    ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Complete, (int) 100, DwoLocalesForGWT.instance.NUM_DLG_Class_CopyingStudentsCompleted(), null);
                    eventBus.fireEvent(e);
                    refreshViewData();
                }
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
    public void CopyStudentsToClassA(String idList[]) {
        ProgressDialogWithAbortDeferred deferred = new ProgressDialogWithAbortDeferred(DwoLocalesForGWT.instance.NUM_DLG_Class_CopyingStudentsTitle());
        ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Init, 0, DwoLocalesForGWT.instance.NUM_DLG_Class_StartingCopyStudents(), deferred);
        eventBus.fireEvent(e);
        CopyStudent(idList, 0, schoolClassB, schoolClassA, deferred.getPromise());
    }

    @JsMethod
    public void CopyStudentsToClassB(String idList[]) {
        ProgressDialogWithAbortDeferred deferred = new ProgressDialogWithAbortDeferred(DwoLocalesForGWT.instance.NUM_DLG_Class_CopyingStudentsTitle());
        ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Init, 0, DwoLocalesForGWT.instance.NUM_DLG_Class_StartingCopyStudents(), deferred);
        eventBus.fireEvent(e);
        CopyStudent(idList, 0, schoolClassA, schoolClassB, deferred.getPromise());
    }

    @JsMethod
    public void MoveStudentsToClassA(String idList[]) {
        //Add to Class A
        ProgressDialogWithAbortDeferred deferred = new ProgressDialogWithAbortDeferred(DwoLocalesForGWT.instance.NUM_DLG_Class_MovingStudentsTitle());
        ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Init, 0, DwoLocalesForGWT.instance.NUM_DLG_Class_StartingMovingStudents(), deferred);
        eventBus.fireEvent(e);
        MoveStudent(idList, 0, schoolClassB, schoolClassA, deferred.getPromise());

    }

    @JsMethod
    public void MoveStudentsToClassB(String idList[]) {
        //Add to Class B
        ProgressDialogWithAbortDeferred deferred = new ProgressDialogWithAbortDeferred(DwoLocalesForGWT.instance.NUM_DLG_Class_MovingStudentsTitle());
        ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Init, 0, DwoLocalesForGWT.instance.NUM_DLG_Class_StartingMovingStudents(), deferred);
        eventBus.fireEvent(e);
        MoveStudent(idList, 0, schoolClassA, schoolClassB, deferred.getPromise());
    }

    public void MoveStudent(String idList[], int i, DomSchoolClass from, DomSchoolClass to, Promise abortPromise) {
        Promise<Boolean> promise;
        DomMoveStudentToSchoolClass submit = new DomMoveStudentToSchoolClass();
        submit.setSchoolClassFrom(from);
        submit.setSchoolClassTo(to);
        DomStudent s;
        if (from.equals(schoolClassB)) {
            s = studentMapB.get(idList[i]);
        } else {
            s = studentMapA.get(idList[i]);
        }
        submit.setStudent(s);
        final int next = i + 1;
        promise = manager.moveStudentToSchoolClass(submit);
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                if (!abortPromise.isDone() && next < idList.length) {
                    LOG.log(Level.INFO, "Moving student " + idList[next] + ".");
                    LOG.log(Level.INFO, "Completed: " + 100.0 * next / idList.length + "%.");
                    double r = (100.0 * next / idList.length);
                    ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Update, (int) r, DwoLocalesForGWT.instance.NUM_DLG_Class_MovingStudents(), null);
                    eventBus.fireEvent(e);
                    MoveStudent(idList, next, from, to, abortPromise);
                } else {
                    //hide progressbar.
                    ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Complete, (int) 100, DwoLocalesForGWT.instance.NUM_DLG_Class_MovingStudentsCompleted(), null);
                    eventBus.fireEvent(e);
                    refreshViewData();
                }
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
                    ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Complete, (int) 100, DwoLocalesForGWT.instance.NUM_DLG_Class_MovingStudentsCompleted(), null);
                    eventBus.fireEvent(e);
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
