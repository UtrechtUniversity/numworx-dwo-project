package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class EditTeacherPresenter {

    private static final Logger LOG = Logger.getLogger(EditTeacherPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Map<String, DomSchoolClass> schoolClassMap;
    private DomUser user;

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public interface Display {

        void clear();

        void init();

//        void setSchoolClass(DomSchoolClassFull schoolClass);
        void showPersonen(Map<String, DomUser> personen);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }

    public EditTeacherPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init(DomUser aUser) {
        user = aUser;
        view.clear();
        view.setEmptyTableMessage();
    }
    
    
    private void updateShoolClasses() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                schoolClassMap = new HashMap<String, DomSchoolClass>();
//                viewData = new HashMap(schoolClassMap.size());
                for (DomSchoolClass sc : resolved.getValue()) {
                    schoolClassMap.put(sc.getId().getIdString(), sc);
//                    viewData.put(sc.getId().getIdString(), new SchoolclassesPresenter.ClassItem(sc.getId().getIdString(), sc.getSchoolClassName()));
                }
//                view.updateView(viewData);
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
