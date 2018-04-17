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
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AddTeacherToSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(AddTeacherToSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Display view;
    private DomSchoolClassFull schoolClass;
    private Map<String, DomTeacher> teachers = new HashMap();

    public interface Display {

        void clear();

        void init();

//        void setSchoolClass(DomSchoolClass schoolClass);
        
        void showTeachers(Map<String, DomTeacher> teachers);

        void setEmptyTableMessage();

        void setsetLoadingTableMessage();        
    }

    public AddTeacherToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
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
    public void FindTeachersInSchool(String username, String Firstname, String insertion, String familyName, String email){
        Promise<List<DomTeacher>> promise;
        promise = manager.getTeachersInSchool();
        // onSuccess update view
        promise.then(new Success<List<DomTeacher>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomTeacher>> resolved) throws Exception {
                Map<String, DomTeacher> teacherMap = new HashMap<>();
                resolved.getValue().forEach((k -> teacherMap.put(k.getId().getIdString(), k)));
                view.showTeachers(teacherMap);
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
    public void AddTeacherToSchoolClass(String teacherId){
                Promise<Boolean> promise;
                DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
                submit.setSchoolClass(schoolClass);
                submit.setTeacher(teachers.get(teacherId));
                promise = manager.submitTeacherToSchoolClass(submit);
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
