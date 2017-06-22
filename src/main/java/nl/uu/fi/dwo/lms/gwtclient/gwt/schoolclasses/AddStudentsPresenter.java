package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import org.osgi.util.promise.Promise;

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

    private String[] tableHeaders = {"givenname", "insertion", "familyname", "usercode", "password", "remove"};
    private DomSchoolClass schoolClass;
    private Map<String, DomSingleSchoolStudent> studentMap;
    private Map<String, AddStudentsPresenter.StudentItem> studentItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, AddStudentsPresenter.StudentItem> data);
    }

    public class StudentItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;
        public String password;
        public boolean success;

        public StudentItem(String aKey, String aFirstName, String anInsertion, String aFamilyName, String aUsercode, String aPassword) {
            key = aKey;
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
            password = aPassword;
            success = false;
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
        studentMap = new HashMap<String, DomSingleSchoolStudent>();
        studentItems = new HashMap<String, StudentItem>();
        studentItems.put("none", new StudentItem("none","","","","",""));
        view.updateView(studentItems);
    }

    private void updateViewData(DomSchoolClass sc) {
        studentItems = new HashMap(studentMap.size());
        for (DomSingleSchoolStudent s : studentMap.values()) {
            studentMap.put(s.getId().getIdString(), s);
            StudentItem item = new StudentItem(s.getId().getIdString(),
                    s.getGivenName(),
                    s.getInsertion(),
                    s.getFamilyName(),
                    s.getUserName(),
                    s.getPassword()
            );
            studentItems.put(s.getId().getIdString(), item);
        }
        view.updateView(studentItems);
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    void goBackToSchoolClasses() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

    /**
     * Adds a student to a success schoolclass and updates the view.
     *
     * @param classKey
     */
    public void addNewStudents() {
        for (StudentItem item : studentItems.values()) {
                LOG.log(Level.INFO, "Adding  " + item.usercode + " to targetSchoolClass "+schoolClass.getSchoolClassName());
                Promise<Boolean> promise;
                final StudentItem fItem = item;
                
                DomSingleSchoolStudent student = new DomSingleSchoolStudent();
                student.setGivenName(item.givenName);
                student.setInsertion(item.insertion);
                student.setFamilyName(item.familyName);
                student.setPassword(item.password);
                student.setUserName(item.usercode);
                student.setSingleSchool(true);

                DomNewSingleSchoolStudent newStudent = new DomNewSingleSchoolStudent();
                newStudent.setDomSingleSchoolStudent(student);
                newStudent.setDomSchoolClass(schoolClass);
                
//                promise = manager.submitSingleSchoolStudent(newStudent);
//                // onSuccess update view
//                promise.then(new Success<Boolean, Void>() {
//                    @Override
//                    public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
//                        if (resolved.getValue().booleanValue() == true) {
//                            if (index % 10 == 0 || index == cnt) {
//                                updateViewData(schoolClass);
//                            }
//                        }else{
//                            studentItems.get(fItem.key).success = true;
//                        }
//                        return null;
//                    }
//                },
//                        new Failure() {
//                    @Override
//                    public void fail(Promise<?> resolved) throws Exception {
//                        Throwable fail = resolved.getFailure();
//                        if (fail instanceof Dwo2Exception) {
//                            LOG.log(Level.SEVERE, fail.getMessage());
//                            eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
//                        } else {
//                            LOG.log(Level.SEVERE, fail.getMessage());
//                            eventBus.fireEvent(new DialogEvent(fail.getMessage()));
//                            //throw directly
//                        }
//                    }
//                });
//                item.success = false;
//            }
        }

    }

    public void removeRowFromTable() {

    }
 
    public void loadData() {
        //call dialog for parsing data
    }
    
}
