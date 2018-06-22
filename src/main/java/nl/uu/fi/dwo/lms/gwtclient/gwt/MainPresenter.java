package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.util.StringFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
public class MainPresenter {

    private static final DwoLocalesForGWT rb = GWT.create(DwoLocalesForGWT.class);
    private static final Logger LOG = Logger.getLogger(MainPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    public interface Display {

        public boolean isMenuVisible();

        public void setSchoolName(String schoolName);

        public void setUserRole(String userRole);

        public void setPresentationName(String presentationName);

        public void showWelcomeView();

        public void showAccountView();

        public void showLoginView();

        public void showResultsView();

        public void showSelectedResultsView();

        public void showStudentResultsView();

        public void showSelectStudentResultsView();

        public void showStudentScoResultView();

        public void showSchoolclassesView(); // has AddSchoolClass function

        public void showEditSchoolclassView();

        public void showAddStudentToSchoolClassView();

        public void showCopyOrMoveStudentToSchoolClassView();

        public void showAddTeacherToSchoolClassView();

        public void showEditCoursesOfSchoolClassView();

//        public void showStudentsInSchoolclassView();
//
//        public void showTeachersInSchoolclassView();
//
//        public void showCoursesOfSchoolClassView();
        public void setCurrentPanelName(String panel);

        public void showPersonsView();

        public void showAddPersonView();

        public void showEditPersonView();

        public void showImportPersonsView();

        public void showModulesView();

        public void showOrganisationView();
    }

    private MainPresenter.Display display;

    MainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
//                setDWO(this);
//        eventBus.addHandler(SwitchViewEvent.TYPE, this);
//        eventBus.addHandler(LoginEvent.TYPE, this);
    }

    public void init() {

    }

    /**
     * @param display the display to set
     */
    public void setView(MainPresenter.Display display) {
        this.display = display;
    }

    @JsMethod
    public void selectView(String selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.valueOf(selectedView)));
    }

    public void selectView(SwitchViewEvent.SelectedView selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(selectedView));
    }

    @JsMethod
    public void logout() {
        LOG.log(Level.INFO, "Logging out");
        Promise<Boolean> p = Promises.resolved(true); //empty promise
        p.then(new Success<Boolean, Boolean>() {
            @Override
            //Are you sure?
            public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {//do dialog check
                AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(DwoLocalesForGWT.instance.GUI_Dialog_User_ConfirmLogout());
                AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
                eventBus.fireEvent(event);
                return dialogPromise.getPromise();
            }
        }).then(new Success<Boolean, Void>() {
            @Override
            //Are you sure?
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue()) {
                    eventBus.fireEvent(new LoginEvent(LoginEvent.State.LOGOUT));
                } else {
                }
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
}
