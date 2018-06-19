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
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
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
public class PersonsPresenter {

    private static final Logger LOG = Logger.getLogger(PersonsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Map<String, DomUser> personen;

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

    public interface Display extends BasicDisplay {

        void init();

//        void setSchoolClass(DomSchoolClassFull schoolClass);
        void showPersonen(Map<String, DomUser> personen);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }

    public PersonsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.setEmptyTableMessage();
    }

    @JsMethod
    public void showStudentList() {
        view.setLoadingTableMessage();
        Promise<List<DomStudent>> promise;
        promise = manager.getTeachersStudents();
        // onSuccess update view
        promise.then(new Success<List<DomStudent>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                personen = new HashMap<>(resolved.getValue().size());
                resolved.getValue().forEach((k -> personen.put(k.getId().getIdString(), k)));
                view.showPersonen(personen);
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
    public void showTeacherList() {
        view.setLoadingTableMessage();
        Promise<List<DomTeacher>> promise;
        promise = manager.getTeachersInSchool();
        // onSuccess update view
        promise.then(new Success<List<DomTeacher>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomTeacher>> resolved) throws Exception {
                personen = new HashMap<>(resolved.getValue().size());
                resolved.getValue().forEach((k -> personen.put(k.getId().getIdString(), k)));
                view.showPersonen(personen);

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
    public void editStudent(String id) {
        LOG.log(Level.INFO, "Editing person: " + id);
        DomUser person = personen.get(id);
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITSTUDENT, person));
    }

    @JsMethod
    public void editTeacher(String id) {
        LOG.log(Level.INFO, "Editing person: " + id);
        DomUser person = personen.get(id);
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITTEACHER, person));
    }

    @JsMethod
    public void addPerson() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDPERSON));
    }

   @JsMethod
    public boolean hasImportPersons(){
        return false;
    }
    
    @JsMethod
    public void importPersons() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.IMPORTPERSONS));
    }
}
