package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;
import org.vectomatic.file.File;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class PersonsPresenter {

    private static final Logger LOG = Logger.getLogger(PersonsPresenter.class.getName());
    final DwoGlobalVars dwoGlobalVars;
    final EventBus eventBus;
    Display view;
    final PersonsService manager;
    Map<String, DomUser> personen;
    int stage=0;

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
        RootPanel root = RootPanel.get("personpager");
        root.clear();
    }

    public void setStage(int aStage) {
        stage = aStage;
    }

    public interface Display extends BasicDisplay {
        
//        void setSchoolClass(DomSchoolClassFull schoolClass);
        void showPersonen(Map<String, DomUser> personen);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }
    
    @Inject PersonsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService manager) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        this.manager = manager;
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.init();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#persons"));
        view.setEmptyTableMessage();
        showStudentList();
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
                if(personen.isEmpty())
                  view.setEmptyTableMessage();
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
        })
        .recover((p) -> { view.setEmptyTableMessage(); return null; });
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
                if(personen.isEmpty())
                  view.setEmptyTableMessage();
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
        })
        .recover((p) -> { view.setEmptyTableMessage(); return null; });
    }

    @JsMethod
    public void editStudent(String id) {
        LOG.log(Level.INFO, "Editing student: " + id);
        DomUser person = personen.get(id);
        eventBus.fireEvent(
                new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITSTUDENT, person));
    }

    @JsMethod
    public void editTeacher(String id) {
        LOG.log(Level.INFO, "Editing teacher: " + id);
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
        //return true;
      return dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid();
    }
    
    @JsMethod
    public void importPersons(JavaScriptObject file) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.IMPORTPERSONS, file));
    }
}
