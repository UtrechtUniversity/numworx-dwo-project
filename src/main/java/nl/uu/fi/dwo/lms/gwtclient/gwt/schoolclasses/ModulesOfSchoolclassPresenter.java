package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import nl.uu.fi.dwo.rest.dom.DomCoursesOfSchoolclassTree;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions. Fetches courses in classcourses of
 * schoolclass, dynamically fetches other courses and shows black bar.
 *
 * @author Gert van der Plas
 */
public class ModulesOfSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(ModulesOfSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private ModulesOfSchoolclassService service;

    private String[] tableHeaders = {"module name", "assigned", "type", "from [?]", "to [?]", "password"};
    private DomSchoolClass schoolClass;
    private DomCoursesOfSchoolclassTree tree;
    private Display view;
    private int requests = 0;

    public interface Display {

        void clear();

        void init();

        void updateTable(List<ClassCourseItem> item);

        void setTree(ClassCourseItem item);

        void setEmptyTableMessageModules();

        void setLoadingTableMessageModules();

        void setEmptyTableMessageSelected();

        void setLoadingTableMessageSelected();
    }

    void detachItemFromSchoolClass(ClassCourseItem classCourseItem) {
        Promise<Boolean> promise;
        promise = service.detachCourseFromClass(schoolClass, tree.getNode(classCourseItem.getKey()).getObject().getCourse());
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                updateViewData();
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
//                view.updateView(courseItems);
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });
    }

    void attachItemToSchoolClass(ClassCourseItem classCourseItem) {
        Promise<Boolean> promise;
        promise = service.attachCourseToClass(schoolClass, tree.getNode(classCourseItem.getKey()).getObject().getCourse());
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                updateViewData();
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
//                view.updateView(courseItems);
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });
    }

//    public CourseItem getRoot(){
//        CourseItem item = new CourseItem();
//        item.name = tree.getCourseTree().getObject().getCourse().getName();
//        item.parent = tree.getCourseTree().getObject().getCourse().getId().getIdString();
//        item.children = null;
//    }
    public ModulesOfSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        service = new ModulesOfSchoolclassService(dwoGlobalVars);
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public void init(DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
        view.init();
        view.setEmptyTableMessageModules();
        view.setEmptyTableMessageSelected();
        updateViewData();
    }

    private void updateViewData() {
        //MsgDialogPromise
        view.setLoadingTableMessageModules();
        Promise<DomCoursesOfSchoolClass4Teacher> promise = service.getModules(schoolClass);
//        Promise<DomCoursesOfSchoolClass4Teacher> p = service.getModules(schoolClass);
//        MsgDialogPromise<DomCoursesOfSchoolClass4Teacher> deferred = new MsgDialogPromise<DomCoursesOfSchoolClass4Teacher>(promise, "test");
//        Promise promise = deferred.getPromise();
        // onSuccess update view
        promise.then(new Success<DomCoursesOfSchoolClass4Teacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomCoursesOfSchoolClass4Teacher> resolved) throws Exception {
                //flip back to schoolclasses screen 
                DomCoursesOfSchoolClass4Teacher value = resolved.getValue();
                tree = new DomCoursesOfSchoolclassTree(dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool(), value);
                ClassCourseItem item = new ClassCourseItem(null, "root");
                //parse results into a tree.
                view.setTree(item);
                view.setEmptyTableMessageSelected();
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
//                view.updateView(courseItems);
                view.setEmptyTableMessageModules();
                view.setEmptyTableMessageSelected();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });

    }

    public ClassCourseItem getRootNode() {
        DomTree<DomCourseOfClass> c = tree.getCourseTree();
        if (c.getObject().getClassCourse() == null) {
            ClassCourseItem item = new ClassCourseItem(null, c.getObject().getCourse().getName());
            return item;
        } else {
//            /String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo
            ClassCourseItem item = new ClassCourseItem("root",
                    c.getObject().getCourse().getName(),
                    false,
                    c.getObject().getClassCourse().getCourseType().name(),
                    c.getObject().getClassCourse().getNotBefore(),
                    c.getObject().getClassCourse().getNotAfter(),
                    c.getObject().getClassCourse().getAccessKey()
            );
            return item;
        }

    }

    public ClassCourseItem getNode(String key) {
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (c.getObject().getClassCourse() == null) {
            ClassCourseItem item = new ClassCourseItem(null, c.getObject().getCourse().getName());
            item.setHasStudentData(false);
            if (c.getChildren() == null || c.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            return item;
        } else {
//            /String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo
            ClassCourseItem item = new ClassCourseItem(key,
                    c.getObject().getCourse().getName(),
                    (c.getObject().getClassCourse().getViewState() != ViewState.invisible),
                    c.getObject().getClassCourse().getCourseType().name(),
                    c.getObject().getClassCourse().getNotBefore(),
                    c.getObject().getClassCourse().getNotAfter(),
                    c.getObject().getClassCourse().getAccessKey()
            );
            if (c.getChildren() == null || c.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            return item;
        }
    }

    public List<ClassCourseItem> getNodeChildren(String key) {
        if (tree == null) {
            return new ArrayList<ClassCourseItem>();
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);

        List<ClassCourseItem> itemList = new ArrayList<ClassCourseItem>(c.getChildren().size());
        for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
            ClassCourseItem item = new ClassCourseItem(coc.getObject().getCourse().getId().getIdString(), coc.getObject().getCourse().getName());
            if (coc.getObject().getClassCourse() != null) {
                item.setHasStudentData((coc.getObject().getClassCourse().getViewState() != ViewState.invisible));
            } else {
                item.setHasStudentData(false);
            }
            if (!coc.getObject().getCourse().getWithChildren()) {
                item.setIsLeaf(true);
            }
            itemList.add(item);
        }

        return itemList;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    @JsMethod
    void setSelectedItem(ClassCourseItem item) {
        boolean isLeaf = false;
        DomTree<DomCourseOfClass> c = tree.getNode(item.getKey());
        for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
            if (coc.getChildren() == null || coc.getChildren().isEmpty()) {
                isLeaf = true;
                break;
            }
        }
        if (isLeaf) {
            //Show children in cellTable
            LOG.log(Level.INFO, "Going to show children in table");
            List<ClassCourseItem> ccList = new ArrayList<>(c.getChildren().size());
            for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
                //creat list for table
                if (!coc.getObject().getCourse().getWithChildren()
                        && (coc.getChildren() == null || coc.getChildren().isEmpty())) {
                    ClassCourseItem cc = new ClassCourseItem();
                    cc.setKey(coc.getObject().getCourse().getId().getIdString());
                    cc.setName(coc.getObject().getCourse().getName());
                    cc.setHasStudentData(false);
                    if (coc.getObject().getClassCourse() != null) {
                        cc.setType(coc.getObject().getClassCourse().getCourseType().name());
                        cc.setFrom(coc.getObject().getClassCourse().getNotBefore());
                        cc.setTo(coc.getObject().getClassCourse().getNotAfter());
                        cc.setAccessKey(coc.getObject().getClassCourse().getAccessKey());
                        cc.setHasStudentData(true);
                    }
                    cc.setIsLeaf(coc.getObject().getCourse().getWithChildren());
                    ccList.add(cc);
                }
            };
            view.updateTable(ccList);
        } else {
            List<ClassCourseItem> ccList = new ArrayList<>(c.getChildren().size());
            view.updateTable(ccList);
            LOG.log(Level.INFO, "Selected item " + item.getName());
        }
    }

    void goBackToSchoolClasses() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

    /**
     * Course parameters that are not null are updated with their values. A
     * valid key is required.
     *
     * @param key Must not be null.
     * @param typeString
     * @param fromData
     * @param toData
     */
    @JsMethod
    void setModuleSettings(String key, String typeString, String fromData, String toData, String accessKey) {
        if (key == null) {
            eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "Internal error, key not given.")));
            return;
        }
        Promise<Boolean> p = Promises.resolved(null);
        if (typeString != null) {
            p.then((resolved) -> {
                return setCourseType(key, typeString);
            });
        }
        if (fromData != null) {
            p.then((resolved) -> {
                return setFromDate(key, fromData);
            });
        }
        if (toData != null) {
            p.then((resolved) -> {
                return setToDate(key, toData);
            });
        }
        if (accessKey != null) {
            p.then((resolved) -> {
                return setAccessKey(key, accessKey);
            });
        }
        p.then(null,(failure) -> {eventBus.fireEvent(new DialogEvent(failure.getFailure().getMessage()));} );
    }

    private Promise<Boolean> setCourseType(String key, String typeString) {
        CourseType type = CourseType.normal;
        if (typeString != null) {
            try {
                type = CourseType.valueOf(typeString);
            } catch (Exception e) {
                eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "Unknown course type submitted.")));
            }
            DomTree<DomCourseOfClass> c = tree.getNode(key);
            return service.setTypeClassCourse(schoolClass, c.getObject().getCourse(), type);
        }
        return null;
    }

    private Promise<Boolean> setFromDate(String key, String dateString) {
        Date date;
        if (dateString.isEmpty()) {
            date = null;
        } else {
            try {
                date = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm").parse(dateString);
                LOG.log(Level.FINE, "Setting From-date to: " + DateTimeFormat.getFullDateTimeFormat().format(date));
            } catch (Exception e) {

                eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
                return null;
            }
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (date == null || (c.getObject().getClassCourse().getNotAfter() != null
                && c.getObject().getClassCourse().getNotAfter().after(date))
                || (c.getObject().getClassCourse().getNotAfter() == null)) {
            return service.setFromDateClassCourse(schoolClass, c.getObject().getCourse(), date);
        } else {
            eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
            return null;
        }
    }

    private Promise<Boolean> setToDate(String key, String dateString) {
        Date date;
        if (dateString.isEmpty()) {
            date = null;
        } else {
            try {
                date = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm").parse(dateString);
                LOG.log(Level.FINE, "Setting To-date to: " + DateTimeFormat.getFullDateTimeFormat().format(date));
            } catch (Exception e) {
                eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
                return null;
            }
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (date == null || c.getObject().getClassCourse().getNotBefore() == null
                || (c.getObject().getClassCourse().getNotBefore() != null
                && c.getObject().getClassCourse().getNotBefore().before(date))) {
            return service.setToDateClassCourse(schoolClass, c.getObject().getCourse(), date);
        } else {
            eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
            return null;
        }
    }

    private Promise<Boolean> setAccessKey(String key, String accessKey) {
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        DomClassCourse4Teacher cc = c.getObject().getClassCourse();
        if (accessKey == null) {
            accessKey = "";
        }

        if (!accessKey.equals(cc.getAccessKey())) {
            return service.setAccessKey(schoolClass, c.getObject().getCourse(), accessKey);
        }
        return null;

    }
}
