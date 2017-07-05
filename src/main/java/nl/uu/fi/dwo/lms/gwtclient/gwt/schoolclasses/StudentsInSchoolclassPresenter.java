package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ConfirmDialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ConfirmDialogPromise;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class StudentsInSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();

    private String[] tableHeaders = {"givenname", "insertion", "familyname", "usercode", "edit", "select"};
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> studentMap;
    private Map<String, StudentsInSchoolclassPresenter.StudentItem> studentItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, StudentsInSchoolclassPresenter.StudentItem> data);

        void updateSchoolClassList(List<SchoolClassListBoxItem> data);
    }

    public class StudentItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;
        public boolean singleSchool;
        public boolean selected;

        public StudentItem(String aKey, String aFirstName, String anInsertion, String aFamilyName, String aUsercode, boolean aSingleSchool) {
            key = aKey;
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
            singleSchool = aSingleSchool;
            selected = false;
        }
    }

    public StudentsInSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

//    private List<DomSchoolRoleAndClassV2> getTeacherRoles() {
//        List<DomSchoolRoleAndClassV2> result = new ArrayList<DomSchoolRoleAndClassV2>();
//        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
//        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
//        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
//            if (hasRole.getRole().getRoleName().equals("TEACHER")) {
//                result.add(hasRole);
//            }
//        }
//        return result;
//    }
    public void init(DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
        view.init();
        updateViewData(aSchoolClass);
        updateSchoolClasses();
    }

    private void updateViewData(DomSchoolClass sc) {
        Promise<List<DomStudent>> promise;
        promise = manager.getStudentsInSchoolClass(sc);
        // onSuccess update view
        promise.then(new Success<List<DomStudent>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                studentMap = new HashMap<String, DomStudent>();
                Map<String, StudentsInSchoolclassPresenter.StudentItem> oldStudentItems = studentItems;
                studentItems = new HashMap(studentMap.size());
                for (DomStudent sc : resolved.getValue()) {
                    studentMap.put(sc.getId().getIdString(), sc);
                    StudentItem item = new StudentItem(sc.getId().getIdString(),
                            sc.getGivenName(),
                            sc.getInsertion(),
                            sc.getFamilyName(),
                            sc.getUserName(),
                            sc.getSingleSchool()
                    );
                    if (oldStudentItems != null
                            && oldStudentItems.containsKey(sc.getId().getIdString())
                            && oldStudentItems.get(sc.getId().getIdString()).selected) {
                        item.selected = true;
                    }
                    studentItems.put(sc.getId().getIdString(), item);
                }
                view.updateView(studentItems);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.updateView(studentItems);
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });

    }

    public void updateSchoolClasses() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                schoolClassMap = new HashMap<String, DomSchoolClass>(resolved.getValue().size());
                schoolClassItems = new ArrayList<SchoolClassListBoxItem>(resolved.getValue().size());
                for (DomSchoolClass sc : resolved.getValue()) {
                    if (!schoolClass.getId().equals(sc.getId())) {
                        schoolClassMap.put(sc.getId().getIdString(), sc);
                        schoolClassItems.add(new SchoolClassListBoxItem(sc.getId().getIdString(), sc.getSchoolClassName()));
                    }
                }
                view.updateSchoolClassList(schoolClassItems);
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

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    /**
     * @param item
     * @param op
     */
    public void selectItem(StudentsInSchoolclassPresenter.StudentItem item, int op) {
        switch (op) {
            case 4:
                if (item.singleSchool) {
                    LOG.log(Level.INFO, "editable item " + item.usercode);
                    eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.EditStudent, studentMap.get(item.key), schoolClass));
                }
                break;
            case 5:
                item.selected = !item.selected;
                LOG.log(Level.INFO, "item " + item.usercode + " state " + item.selected);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    void addStudents() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDSTUDENTS, schoolClass));
    }

    void goBackToSchoolClasses() {
   eventBus.fireEvent (new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

    /**
     * Adds a student to a selected schoolclass and updates the view.
     *
     * @param classKey
     */
    public void addSelectedToSchoolClass(String classKey) {
        DomSchoolClass targetSchoolClass = schoolClassMap.get(classKey);
        final int cnt;
        int tmp = 0;
        for (StudentItem item : studentItems.values()) {
            if (item.selected) {
                tmp++;
            }
        }
        cnt = tmp;
        tmp = 0;
        for (StudentItem item : studentItems.values()) {
            if (item.selected == true) {
                tmp++;
                final int index = tmp;
                LOG.log(Level.INFO, "Adding  " + item.usercode + " to targetSchoolClass<key,name> " + targetSchoolClass.getId().getIdString() + " " + targetSchoolClass.getSchoolClassName());
                //add to schoolclass and clear item to signal success

                Promise<Boolean> promise;
                final StudentItem fItem = item;
                DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
                submit.setSchoolClassFrom(schoolClass);
                submit.setSchoolClassTo(targetSchoolClass);
                submit.setStudent(studentMap.get(item.key));
                promise = manager.submitStudentToSchoolClass(submit);
                // onSuccess update view
                promise.then(new Success<Boolean, Void>() {
                    @Override
        public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                        if (resolved.getValue().booleanValue() == true) {
                            studentItems.get(fItem.key).selected = false;
                            if (index % 10 == 0 || index == cnt) {
                                updateViewData(schoolClass);
                            }
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
                item.selected = false;
            }
        }

    }

    public void removeSelectedFromSchoolClass() {
        DomSchoolClass targetSchoolClass = schoolClass;
        final int cnt;
        int tmp = 0;
        for (StudentItem item : studentItems.values()) {
            if (item.selected) {
                tmp++;
            }
        }
        cnt = tmp;
        tmp = 0;
//        LOG.log(Level.INFO, "targetSchoolClass<key,name> "+targetSchoolClass.getId().getIdString() + " "+targetSchoolClass.getSchoolClassName());
        for (StudentItem item : studentItems.values()) {
            if (item.selected == true) {
                tmp++;
                final int index = tmp;
                //remove from schoolclass and clear item to signal success                
                LOG.log(Level.INFO, "Removing " + item.usercode + " from targetSchoolClass<key,name> " + targetSchoolClass.getId().getIdString() + " " + targetSchoolClass.getSchoolClassName());
                Promise<Boolean> promise;
                DomRemoveStudentFromSchoolClass data = new DomRemoveStudentFromSchoolClass();
                data.setStudent(studentMap.get(item.key));
                data.setSchoolClass(schoolClass);
                promise = manager.removeStudentFromSchoolClass(data);
                final StudentItem fItem = item;
                // onSuccess update view
                promise.then(new Success<Boolean, Void>() {
                    @Override
        public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                        if (resolved.getValue().booleanValue() == true) {
                            studentItems.get(fItem.key).selected = false;
                            if (index % 10 == 0 || index == cnt) {
                                updateViewData(schoolClass);
                            }
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
                }
                );
            }
        }
    }
    
    public void addNewStudents(){
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDSTUDENTS,schoolClass));
//        https://svn.science.uu.nl/viewvc/project.fisme.java/StatistiekGWT/trunk/src/fi/statistiekgwt/client/StatTable.java?view=markup
//            above code for importing a file.
    }
}
