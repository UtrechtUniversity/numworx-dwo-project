package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.event.Event.fireEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AddSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(AddSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Display view;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();
    }

    public AddSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init() {

    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public void AddAndBack(String name, Boolean showTree, Boolean hasRegKey, String regKey) {
        Promise<Boolean> promise;
        DomSchoolClassFull schoolClass = new DomSchoolClassFull();
        schoolClass.setSchoolClassName(name);
        schoolClass.setIconizer(showTree);
        schoolClass.setHasRegKey(hasRegKey);
        schoolClass.setRegistrationKey(regKey);
        promise = manager.submitSchoolClass(schoolClass);
        // onSuccess calculate results and show.
        promise.then(new Success<Boolean,Void> () {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                if (resolved.getValue() == true) {
                    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                    return null;
                } else {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Rest request failed for unknown reasons.");
                }
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
     * Go back to the schoolclasses presentation.
     */
    public void Back() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTSINSCHOOLCLASS));
    }
    
}
