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
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
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
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
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
                        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                    } else {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new DialogEvent(fail.getMessage()));
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
                        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                    } else {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new DialogEvent(fail.getMessage()));
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
        if(schoolClassB!=null){
            view.SetClassB(schoolClassB);
            refreshClassTableB();
        }
    }

    @JsMethod
    public void CopyStudentsToClassA(List<String> idList) {
        //Add to Class
        for(String id : idList){
                    DomStudent s =  studentMapB.get(id);
                    //chain some promises.
                    //doing stuff
        }
    }

    @JsMethod
    public void CopyStudentsToClassB(List<String> idList) {
        //Add to Class
    }

    @JsMethod
    public void MoveStudentsToClassA(List<String> idList) {
        //Add to Class A
        //remove from class B
    }

    @JsMethod
    public void MoveStudentsToClassB(List<String> studenidListtId) {
        //Add to Class B
        //remove from class A
    }

}
