package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import nl.uu.fi.dwo.rest.dom.DomCoursesOfSchoolclassTree;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
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

    private DomSchoolClass schoolClass;
    private DomCoursesOfSchoolclassTree tree;
    private Display view;
    public interface Display extends BasicDisplay {

        public final static String LOCAL_TIME = "yyyy-MM-dd HH:mm"; // common met jsmodulesofSchool

//        void updateTable(List<ClassCourseItem> item);
        void setTree(DomTree<DomCourseOfClass> tree);

        void setEmptyTableMessageModules();

        void setLoadingTableMessageModules();

        void setEmptyTableMessageSelected();

        void setLoadingTableMessageSelected();
    }

    @JsMethod
    void detachItemFromSchoolClass(String id) {
        Promise<Boolean> promise;
        promise = service.detachCourseFromClass(schoolClass, tree.getNode(id).getObject().getCourse());
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

    @JsMethod
    void attachItemToSchoolClass(String id) {
        Promise<Boolean> promise;
        promise = service.attachCourseToClass(schoolClass, tree.getNode(id).getObject().getCourse());
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
    @Inject ModulesOfSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, ModulesOfSchoolclassService aService) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        service = aService;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#schoolClassModuleManagement"));
    }

    public void init(DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
        view.clear();
        view.init();
        view.setEmptyTableMessageModules();
        view.setEmptyTableMessageSelected();
        updateViewData();
    }

    private GwtClientMessages rb = GWT.create(GwtClientMessages.class);
    
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
                tree = new DomCoursesOfSchoolclassTree(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool(), value);
 // patch "public"
                DomCourse course = tree.getNode(DomCoursesOfSchoolclassTree.PUBLIC_ROOT).getObject().getCourse();
 // welke smaak: profile of "standaard"
 // in deze "then" weten we dat profile.getValue() geresolved en valid is.
                course.setName(dwoGlobalVars.getProfile().getValue().getDwoProfileDescription()); 
                // course.setName(rb.standaardModules());
                
                //ClassCourseItem item = new ClassCourseItem(null, "root");
                //parse results into a tree.
                view.setTree(tree.getCourseTree());
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

    void goBackToSchoolClasses() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

    @JsMethod
    void addModule(String key, String typeString, String fromDate, String toDate, String accessKey) {
        //TODO FIX sloppy addModule implementation
        if (key == null) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "Internal error, key not given.")));
            return;
        }
        Promise<Boolean> p;

        //convert and test parameters.
        //type
        CourseType type = CourseType.normal;
        if (typeString != null) {
            try {
                type = CourseType.valueOf(typeString);
            } catch (Exception e) {
                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "Unknown course type submitted.")));
            }
        }

        //from
        Date from;
        if (fromDate.isEmpty()) {
            from = null;
        } else {
            try {
                from = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm").parse(fromDate);
                LOG.log(Level.FINE, "Setting From-date to: " + DateTimeFormat.getFullDateTimeFormat().format(from));
            } catch (Exception e) {

                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + fromDate + " is not a valid dateString.")));
                return;
            }
        }
        //to
        Date to;
        if (toDate.isEmpty()) {
            to = null;
        } else {
            try {
                to = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm").parse(toDate);
                LOG.log(Level.FINE, "Setting To-date to: " + DateTimeFormat.getFullDateTimeFormat().format(to));
            } catch (Exception e) {
                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + toDate + " is not a valid dateString.")));
                return;
            }
        }

        if (from != null && to != null && from.after(to)) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "date range is not a valid range.")));
        } else {
            ;
        }

        p = service.addCourseToClass(schoolClass, tree.getNode(key).getObject().getCourse(), type, from, to, accessKey);

        p.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                updateViewData();
                return null;
            }
        }, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
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
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "Internal error, key not given.")));
            return;
        }
        Promise<Boolean> p = Promises.resolved(null);
        if (typeString != null) {
            p = p.then((resolved) -> {
                return setCourseType(key, typeString);
            });
        }
        if (fromData != null) {
            p = p.then((resolved) -> {
                return setFromDate(key, fromData);
            });
        }
        if (toData != null) {
            p = p.then((resolved) -> {
                return setToDate(key, toData);
            });
        }
        if (accessKey != null) {
            p = p.then((resolved) -> {
                return setAccessKey(key, accessKey);
            });
        }
        p = p.then((resolved) -> {
            updateViewData();
            return resolved;
        });
        p = p.then(null, (failure) -> {
            eventBus.fireEvent(new AlertDialogWithOKEvent(failure.getFailure().getMessage()));
        });
    }

    private Promise<Boolean> setCourseType(String key, String typeString) {
        CourseType type = CourseType.normal;
        if (typeString != null) {
            try {
                type = CourseType.valueOf(typeString);
            } catch (Exception e) {
                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "Unknown course type submitted.")));
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

                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
                return null;
            }
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (date == null || (c.getObject().getClassCourse().getNotAfter() != null
                && c.getObject().getClassCourse().getNotAfter().after(date))
                || (c.getObject().getClassCourse().getNotAfter() == null)) {
            return service.setFromDateClassCourse(schoolClass, c.getObject().getCourse(), date);
        } else {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
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
                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
                return null;
            }
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (date == null || c.getObject().getClassCourse().getNotBefore() == null
                || (c.getObject().getClassCourse().getNotBefore() != null
                && c.getObject().getClassCourse().getNotBefore().before(date))) {
            return service.setToDateClassCourse(schoolClass, c.getObject().getCourse(), date);
        } else {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
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
    
    @JsMethod
    public boolean hasToets() {
      return dwoGlobalVars.isPremium();
    }
}
