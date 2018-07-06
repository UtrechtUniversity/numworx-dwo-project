package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter.LOG;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * 
 *
 * @author G.A.J. van der Plas
 */
public class AddStudentPresenter extends AddPersonPresenter {

    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Map<String, TaggedDomSchoolClass> taggedSchoolClasses;


    //    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#addStudent"));
        view.init(RoleType.TEACHER.name()); //role of client user.
        view.setEmptyTableMessage();
        updateSchoolClasses();
    }

    public AddStudentPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }


    /**
     * Retrieves a list of all school classes and displays the stuff
     */
    private void updateSchoolClasses() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                taggedSchoolClasses = new HashMap<>();
                for (DomSchoolClass sc : resolved.getValue()) {
                    TaggedDomSchoolClass tsc = new TaggedDomSchoolClass();
                    tsc.setSchoolClass(sc);
                    tsc.setTag(false);
                    taggedSchoolClasses.put(sc.getId().getIdString(), tsc);
                }
                view.showSchoolClasses(taggedSchoolClasses);
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
    public void submitSingleSchoolStudent(String schoolClassId, String username, String givenName, String insertion, String familyName, String eMail, String password) { 
        DomSingleSchoolStudent student = new DomSingleSchoolStudent();
        student.setEmail(eMail);
        student.setFamilyName(familyName);
        student.setGivenName(givenName);
        student.setInsertion(insertion);
        student.setPassword(password);
        student.setUserName(username);
        DomSchoolClass schoolClass = taggedSchoolClasses.get(schoolClassId).getSchoolClass();
        submitSingleSchoolStudent(schoolClass, student);
    }   
    
    private void submitSingleSchoolStudent(DomSchoolClass schoolClass, DomSingleSchoolStudent student) {
        Promise<Boolean> promise;
        DomNewSingleSchoolStudent newStudent = new DomNewSingleSchoolStudent();
        newStudent.setDomSingleSchoolStudent(student);
        newStudent.setDomSchoolClass(schoolClass);
        promise = manager.submitSingleSchoolStudent(newStudent);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                eventBus.fireEvent(new DialogEvent("Added"));
                view.clear();
                view.init(RoleType.STUDENT.name());
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
