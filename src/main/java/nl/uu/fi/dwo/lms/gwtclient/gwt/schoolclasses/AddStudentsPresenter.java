package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class AddStudentsPresenter {

    private static final Logger LOG = Logger.getLogger(AddStudentsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();

    private String[] tableHeaders = {"givenname", "insertion", "familyname", "usercode", "password", "email", "remove"};
    private DomSchoolClass schoolClass;
//    private Map<String, DomSingleSchoolStudent> studentMap;
    private List<StudentItem> studentItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(List<StudentItem> data);

        void refreshView();
    }

    public class StudentItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;
        public String password;
        public String email;
        public boolean spare;

        public StudentItem(String aFirstName, String anInsertion, String aFamilyName, String aUsercode, String aPassword) {
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
            password = aPassword;
            spare = true;
        }
    }

    public AddStudentsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public void init(DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
        studentItems = new ArrayList<AddStudentsPresenter.StudentItem>(10);
        studentItems.add(new AddStudentsPresenter.StudentItem("", "", "", "", ""));
        view.updateView(studentItems);
    }

    private void updateViewData(DomSchoolClass sc) {
        view.updateView(studentItems);
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    void goBackToStudentsInSchoolclass() {
        SwitchViewEvent event = new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTSINSCHOOLCLASS, schoolClass);
        eventBus.fireEvent(event);
    }

    /**
     * Adds a student to a spare schoolclass and updates the view.
     *
     * @param classKey
     */
    public void addNewStudents() {
        for (AddStudentsPresenter.StudentItem item : studentItems) {
            if (item.spare == false) {
                LOG.log(Level.INFO, "Adding  " + item.usercode + " to targetSchoolClass " + schoolClass.getSchoolClassName());
                Promise<Boolean> promise;
                final AddStudentsPresenter.StudentItem fItem = item;

                DomSingleSchoolStudent student = new DomSingleSchoolStudent();
                student.setGivenName(item.givenName);
                student.setInsertion(item.insertion);
                student.setFamilyName(item.familyName);
                student.setPassword(item.password);
                student.setUserName(item.usercode);
                student.setEmail(item.email);
                student.setSingleSchool(true);

                DomNewSingleSchoolStudent newStudent = new DomNewSingleSchoolStudent();
                newStudent.setDomSingleSchoolStudent(student);
                newStudent.setDomSchoolClass(schoolClass);

                promise = manager.submitSingleSchoolStudent(newStudent);
                // onSuccess update view
                promise.then(new Success<Boolean, Void>() {
                    @Override
                    public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                        if (resolved.getValue().booleanValue() == true) {
//                        if (index % 10 == 0 || index == cnt) {
                            studentItems.remove(item);
                            updateViewData(schoolClass);
//                        }
                        } else {
//                        studentItems.get(fItem.key).spare = true;
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
    }

    public void addItem(AddStudentsPresenter.StudentItem item) {
        studentItems.get(studentItems.size() - 1).spare = false;
        studentItems.add(new StudentItem("", "", "", "", ""));
        view.refreshView();
    }

    public void loadData() {
        eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.LoadStudentFile));
    }

    void selectItem(AddStudentsPresenter.StudentItem studentItem, int column) {
        if (column == 6) {
            if (studentItem.spare == false) {
                studentItems.remove(studentItem);
            }
            view.updateView(studentItems);
        }
    }

}
