package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
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
    private DomSchoolClass schoolClassA;
    private DomSchoolClass schoolClassB;
    //private Map<String, DomStudent> students = new HashMap();
    
    public interface Display {

        void clear();

        void init();

        void showStudentsClassA(Map<String, DomStudent> students);
        
        void showStudentsClassB(Map<String, DomStudent> students);
        
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

    public void init() {
        view.init();
        view.setEmptyTableMessageA();
        view.setEmptyTableMessageB();
        refreshViewData();
    }
 
    private void refreshViewData() {
        Promise<DomSchoolClassFull> promise;
        promise = manager.getFullSchoolClass(schoolClassA);
        // onSuccess update view
        promise.then(new Success<DomSchoolClassFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomSchoolClassFull> resolved) throws Exception {
                //flip back to schoolclasses screen 
                schoolClassA = resolved.getValue();
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
    
    
}
