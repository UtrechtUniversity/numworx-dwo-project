package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceTeacher;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessage;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessages;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AddStudentToSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(AddStudentToSchoolclassPresenter.class.getName());
    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final PersonsService manager;
    private final LoggingFailure FAILURE;
    private Display view;
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> students = new HashMap<>();
    private List<DomStudent> studentsInClass;

    public interface Display extends BasicDisplay {
//        void setSchoolClass(DomSchoolClassFull schoolClass);
        void showStudents(Map<String, DomStudent> students);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

        void setSchoolClass(DomSchoolClass schoolClass);
    }

    @Inject AddStudentToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;        
        manager = m;
        FAILURE = new LoggingFailure(LOG, anEventBus);
        
    }

    public void init(DomSchoolClass aSchoolClass) {
        view.clear();
        view.init();
        view.setEmptyTableMessage();
        schoolClass = aSchoolClass;
        view.setSchoolClass(schoolClass);
        updateViewData();
    }

    private void updateViewData() {
        view.setLoadingTableMessage();
        Promise<List<DomStudent>> promise;
        promise = manager.getTeachersStudents();
        promise.then(new Success<List<DomStudent>, List<DomStudent>>() {
            @Override
            public Promise<List<DomStudent>> call(Promise<List<DomStudent>> resolved) throws Exception {
                students = new HashMap<>(resolved.getValue().size());
                resolved.getValue().forEach((k -> students.put(k.getId().getIdString(), k)));
                return manager.getStudentsInSchoolClass(schoolClass);
            }
        }).then(new Success<List<DomStudent>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                List<DomStudent> cantAdd = resolved.getValue();

                cantAdd.forEach((v) -> {
                    if (students.containsKey(v.getId().getIdString())) {
                        students.remove(v.getId().getIdString());
                    }
                });
                view.showStudents(students);
                return null;
            }

        },FAILURE);
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#addStudentToClass"));
    }

    @JsMethod
    public void AddStudentToSchoolClass(String studentId) {
        LOG.log(Level.INFO, "Adding student " + studentId + " to schoolclass" + schoolClass.getId().getIdString());
        Promise<Boolean> promise;
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassFrom(schoolClass);
        submit.setSchoolClassTo(schoolClass);
        submit.setStudent(students.get(studentId));
        promise = manager.submitStudentToSchoolClass(submit);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_Class_StudentsAdded()));
                updateViewData();
                return null;
            }
        },FAILURE);
    }
}
