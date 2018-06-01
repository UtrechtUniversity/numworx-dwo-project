package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

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
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class AddPersonPresenter {

    private static final Logger LOG = Logger.getLogger(AddPersonPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Map<String, DomSchoolClass> schoolClasses;

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
        void showSchoolClasses(Map<String, DomSchoolClass> schoolClasses);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }

    public AddPersonPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.setEmptyTableMessage();
        updateView();
    }

    @JsMethod
    public void updateView() {
        view.setLoadingTableMessage();
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                schoolClasses = new HashMap<>(resolved.getValue().size());
                resolved.getValue().forEach((k -> schoolClasses.put(k.getId().getIdString(), k)));
                view.showSchoolClasses(schoolClasses);
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
    
    @JsMethod
    public void addPerson() {
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
        // add using promise.
    }

}
