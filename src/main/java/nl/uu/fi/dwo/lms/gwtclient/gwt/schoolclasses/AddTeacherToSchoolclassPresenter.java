package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
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
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AddTeacherToSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(AddTeacherToSchoolclassPresenter.class.getName());
    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final PersonsService manager;
    private final LoggingFailure FAILURE;
    private Display view;
    private DomSchoolClass schoolClass;
    private Map<String, DomTeacher> teachers = new HashMap<>();

    public interface Display extends BasicDisplay {
        void setSchoolClass(DomSchoolClass schoolClass);
        void showTeachers(Map<String, DomTeacher> teachers);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }

    @Inject AddTeacherToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        manager = m;
        FAILURE = new LoggingFailure(LOG, anEventBus);
    }
//    public AddTeacherToSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
//      this(anEventBus, aDwoGlobalVars, new PersonsServiceTeacher(aDwoGlobalVars));
//    }

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
        Promise<List<DomTeacher>> promise;
        promise = manager.getTeachersInSchool();
        // onSuccess update view
         promise.then(new Success<List<DomTeacher>, List<DomTeacher>>() {
            @Override
            public Promise<List<DomTeacher>> call(Promise<List<DomTeacher>> resolved) throws Exception {
                teachers = new HashMap<>(resolved.getValue().size());
                resolved.getValue().forEach((k -> teachers.put(k.getId().getIdString(), k)));
                return manager.getTeachersInSchoolClass(schoolClass);
            }
        }).then(new Success<List<DomTeacher>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomTeacher>> resolved) throws Exception {
                List<DomTeacher> cantAdd = resolved.getValue();

                cantAdd.forEach((v) -> {
                    if (teachers.containsKey(v.getId().getIdString())) {
                        teachers.remove(v.getId().getIdString());
                    }
                });
                view.showTeachers(teachers);
                return null;
            }
        }, FAILURE);
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#addTeacherToClass"));
    }

    @JsMethod
    public void AddTeacherToSchoolClass(String teacherId) {
        LOG.log(Level.INFO,"Adding teacher "+teacherId+" to schoolclass"+schoolClass.getId().getIdString());
        LOG.log(Level.INFO,"Teachers: " + teachers + " getTeacher;" + teachers.get(teacherId));
        Promise<Boolean> promise;
        DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
        submit.setSchoolClass(schoolClass);
        submit.setTeacher(teachers.get(teacherId));
        promise = manager.submitTeacherToSchoolClass(submit);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                eventBus.fireEvent(new MessageDialogWithOKEvent("Success"));
                updateViewData();
                return null;
            }
        },
                FAILURE);
    }
}
