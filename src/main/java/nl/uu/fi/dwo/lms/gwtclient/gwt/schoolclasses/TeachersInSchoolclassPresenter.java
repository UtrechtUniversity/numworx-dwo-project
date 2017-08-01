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
import nl.uu.fi.dwo.lms.gwtclient.gwt.ConfirmDialogPromise;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class TeachersInSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();

    private String[] tableHeaders = {"givenname", "insertion", "familyname", "usercode", "select"};
    private DomSchoolClass schoolClass;
    private Map<String, DomTeacher> teacherMap;
    private Map<String, TeachersInSchoolclassPresenter.TeacherItem> teacherItems;
    private Map<String, DomTeacher> listBoxMap;
    private List<TeacherListBoxItem> listBoxList;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, TeachersInSchoolclassPresenter.TeacherItem> data);

        void updateTeacherList(List<TeacherListBoxItem> data);
    }

    public class TeacherItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;
        public boolean selected;

        public TeacherItem(String aKey, String aFirstName, String anInsertion, String aFamilyName, String aUsercode) {
            key = aKey;
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
            selected = false;
        }
    }

    public TeachersInSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
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
        updateTeacherList();
    }

    private void updateViewData(DomSchoolClass sc) {
        Promise<List<DomTeacher>> promise;
        promise = manager.getTeachersInSchoolClass(sc);
        // onSuccess update view
        promise.then(new Success<List<DomTeacher>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomTeacher>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                teacherMap = new HashMap<String, DomTeacher>();
                Map<String, TeachersInSchoolclassPresenter.TeacherItem> oldTeacherItems = teacherItems;
                teacherItems = new HashMap(teacherMap.size());
                for (DomTeacher sc : resolved.getValue()) {
                    teacherMap.put(sc.getId().getIdString(), sc);
                    TeacherItem item = new TeacherItem(sc.getId().getIdString(),
                            sc.getGivenName(),
                            sc.getInsertion(),
                            sc.getFamilyName(),
                            sc.getUserName()
                    );
                    if (oldTeacherItems != null
                            && oldTeacherItems.containsKey(sc.getId().getIdString())
                            && oldTeacherItems.get(sc.getId().getIdString()).selected) {
                        item.selected = true;
                    }
                    teacherItems.put(sc.getId().getIdString(), item);
                }
                view.updateView(teacherItems);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.updateView(teacherItems);
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });

    }

    public void updateTeacherList() {
        Promise<List<DomTeacher>> promise;
        promise = manager.getTeachersInSchool();
        // onSuccess update view
        promise.then(new Success<List<DomTeacher>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomTeacher>> resolved) throws Exception {
                listBoxMap = new HashMap<String, DomTeacher>(resolved.getValue().size());
                listBoxList = new ArrayList<TeacherListBoxItem>(resolved.getValue().size());
                for (DomTeacher t : resolved.getValue()) {
                    if (!teacherMap.containsKey(t.getId().getIdString())) {
//                    if (!schoolClass.getId().equals(sc.getId())) {
                        listBoxMap.put(t.getId().getIdString(), t);
                        listBoxList.add(new TeacherListBoxItem(t.getId().getIdString(), t.getDisplayName()));
                    }
//                    }
                }
                view.updateTeacherList(listBoxList);
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
    public void selectItem(TeachersInSchoolclassPresenter.TeacherItem item, int op) {
        switch (op) {
//            case 3:
//                if (item.singleSchool) {
//                    LOG.log(Level.INFO, "editable item " + item.usercode);
//                    eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs., teacherMap.get(item.key), schoolClass));
//                }
//                break;
            case 4:
                item.selected = !item.selected;
//                LOG.log(Level.INFO, "item " + item.usercode + " state " + item.selected);
//                eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs., teacherMap.get(item.key), schoolClass));
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    void goBackToSchoolClasses() {
        if (teacherItems.size()!=0) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
        }else{
            ConfirmDialogPromise p = new ConfirmDialogPromise("Are you sure, there are no teachers in the class.");
            p.getPromise().then(new Success<Boolean, Void>() {
                @Override
                public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                    LOG.log(Level.INFO, "returned value" + resolved.getValue());
                    if (resolved.getValue() == true) {
                          eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                    } else {
                        //do nothing.
                    }
                    return null;
                }
            }, new Failure() {
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

            eventBus.fireEvent(new ConfirmDialogEvent(ConfirmDialogEvent.EventType.ConfirmDialog, p));
        }
    }

    public void removeSelectedFromSchoolClass() {
        DomSchoolClass targetSchoolClass = schoolClass;
        final int cnt;
        int tmp = 0;
        for (TeacherItem item : teacherItems.values()) {
            if (item.selected) {
                tmp++;
            }
        }
        cnt = tmp;
        tmp = 0;
//        LOG.log(Level.INFO, "targetSchoolClass<key,name> "+targetSchoolClass.getId().getIdString() + " "+targetSchoolClass.getSchoolClassName());
        for (TeacherItem item : teacherItems.values()) {
            if (item.selected == true && !item.usercode.equals(dwoGlobalVars.getCurrentUser().getUserName())) {
                //can't remove yourself.
                tmp++;
                final int index = tmp;
                //remove from schoolclass and clear item to signal success                
                LOG.log(Level.INFO, "Removing " + item.usercode + " from targetSchoolClass<key,name> " + targetSchoolClass.getId().getIdString() + " " + targetSchoolClass.getSchoolClassName());
                Promise<Boolean> promise;
                DomRemoveTeacherFromSchoolClass data = new DomRemoveTeacherFromSchoolClass();
                data.setTeacher(teacherMap.get(item.key));
                data.setSchoolClass(schoolClass);
                promise = manager.removeTeacherFromSchoolClass(data);
                final TeacherItem fItem = item;
                // onSuccess update view
                promise.then(new Success<Boolean, Void>() {
                    @Override
                    public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                        if (resolved.getValue().booleanValue() == true) {
                            teacherItems.get(fItem.key).selected = false;
                            if (index == cnt) {
                                updateViewData(schoolClass);
                                updateTeacherList();
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

    void addTeacherToSchoolClass(String key) {
        DomTeacher targetTeacher = listBoxMap.get(key);
        DomSchoolClass targetSchoolClass = schoolClass;
//        LOG.log(Level.INFO, "targetSchoolClass<key,name> "+targetSchoolClass.getId().getIdString() + " "+targetSchoolClass.getSchoolClassName());
        LOG.log(Level.INFO, "Submitting " + targetTeacher.getUniqueDisplayName() + " to " + targetSchoolClass.getSchoolClassName());
        Promise<Boolean> promise;
        DomSubmitTeacherToSchoolClass data = new DomSubmitTeacherToSchoolClass();
        data.setTeacher(targetTeacher);
        data.setSchoolClass(targetSchoolClass);
        promise = manager.submitTeacherToSchoolClass(data);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                if (resolved.getValue().booleanValue() == true) {
                    updateViewData(schoolClass);
                    updateTeacherList();
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
