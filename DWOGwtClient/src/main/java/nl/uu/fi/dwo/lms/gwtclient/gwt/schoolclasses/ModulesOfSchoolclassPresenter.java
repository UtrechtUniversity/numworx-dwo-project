package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import nl.uu.fi.dwo.rest.dom.DomCoursesOfSchoolclassTree;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
    private Failure FAILURE;

    private DomSchoolClass schoolClass;
    private Promise<DomCoursesOfSchoolclassTree> tree;
    private Display view;
    public interface Display extends BasicDisplay {

        public final static String LOCAL_TIME = "yyyy-MM-dd HH:mm"; // common met jsmodulesofSchool

//        void updateTable(List<ClassCourseItem> item);
        void setTree(DomTree<DomCourseOfClass> tree);

        void setEmptyTableMessageModules();

        void setLoadingTableMessageModules();

        void setEmptyTableMessageSelected();

        void setLoadingTableMessageSelected();

		void setSettings(String id);

		void setSchoolyearUI(boolean on);
    }

    @JsMethod
    void detachItemFromSchoolClass(String id) {
        Promise<Boolean> promise;
        promise = tree.then(p ->         		
        				service.detachCourseFromClass(schoolClass, p.getValue().getNode(id).getObject().getCourse()));
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                //updateViewData();
                LOG.info("Detached " + id);
               return null;
            }

        },FAILURE).onResolve(() -> tree = reloadTree());
    }

    @JsMethod
    void attachItemToSchoolClass(String id) {
        Promise<Boolean> promise;
        promise = tree.then( p -> 
        		service.attachCourseToClass(schoolClass, p.getValue().getNode(id).getObject().getCourse()));
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                //updateViewData();
                LOG.info("Attached " + id);
                return null;
            }

        }, FAILURE).onResolve(() -> tree = reloadTree());
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
        FAILURE = new LoggingFailure(LOG, anEventBus);
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
    
    
    private Promise<DomCoursesOfSchoolclassTree> reloadTree() {
        Promise<DomCoursesOfSchoolClass4Teacher> promise = service.getModules(schoolClass, false);
        return promise.map(
        		value -> 
        		{	
                    DomCoursesOfSchoolclassTree result = new DomCoursesOfSchoolclassTree(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool(), value);
// patch "public"
                    DomCourse course = result.getNode(DomCoursesOfSchoolclassTree.PUBLIC_ROOT).getObject().getCourse();
// welke smaak: profile of "standaard"
// in deze "then" weten we dat profile.getValue() geresolved en valid is.
                    course.setName(dwoGlobalVars.getProfile().getValue().getDwoProfileDescription());      		
 // prune
                    prune(result.getCourseTree().getChildren());
                    
                    return result;
        		}
        		
        		);
    }
    
    
    private boolean prune(Map<String, DomTree<DomCourseOfClass>> children) {
		Collection<DomTree<DomCourseOfClass>> collection = children.values();
		Iterator<DomTree<DomCourseOfClass>> iter = collection.iterator();
		while (iter.hasNext()) {
			DomTree<DomCourseOfClass> domTree = iter.next();
			if (domTree.getObject().getCourse().getWithChildren() && prune(domTree.getChildren())) {
				iter.remove();
			}		
		}
		return collection.isEmpty();
	}

	private Promise<Object> updateViewData() {
        view.setLoadingTableMessageModules();
        tree = reloadTree();
        Promise<DomCoursesOfSchoolclassTree> promise = tree;
        // onSuccess update view
        return promise.then(new Success<DomCoursesOfSchoolclassTree, Void>() {
            @Override
            public Promise<Void> call(Promise<DomCoursesOfSchoolclassTree> resolved) throws Exception {
                //flip back to schoolclasses screen 
            	DomCoursesOfSchoolclassTree
                tree = resolved.getValue();
                view.setTree(tree.getCourseTree());
                return null;
            }

        },
            (resolved) -> {
                view.setEmptyTableMessageModules();
                view.setEmptyTableMessageSelected();
             }
        ).then(null, FAILURE);
    }

    void goBackToSchoolClasses() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

    @JsMethod
    void openSettings(String key, String typeString, String fromData, String toData, String accessKey) {
    	Promise<String> url = 
    			update(key, typeString, fromData, toData, accessKey)
    			.then(p -> service.openSettingsUI(p.getValue()));
    	
    	url = url.then(s -> {
    		Window.open(s.getValue(), "settingsUI", "");
    		return s;
    	});
    	url.then( s -> updateViewData(), FAILURE).onResolve(() -> view.setSettings(key));
    }
    
    @JsMethod
    void openDashboard(String key, String typeString, String fromData, String toData, String accessKey) {
    	Promise<String> url = 
    			update(key, typeString, fromData, toData, accessKey)
    			.then(p -> service.openDashboardUI(p.getValue()));
    	
    	url = url.then(s -> {
    		Window.open(s.getValue(), "dashboardUI", "");
    		return s;
    	});
    	url.then( s -> updateViewData(), FAILURE).onResolve(() -> view.setSettings(key));
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
        CourseType type ;
        if (typeString != null) {
            try {
                type = CourseType.valueOf(typeString);
            } catch (Exception e) {
            	type = CourseType.normal;
                eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "Unknown course type submitted.")));
            }
        } else {
        	type = CourseType.normal;
        }
        final CourseType ftype = type;
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

        p = tree.then(pp -> 
        		service.addCourseToClass(schoolClass, pp.getValue().getNode(key).getObject().getCourse(), ftype, from, to, accessKey));

        p.then(new Success<Boolean, Object>() {
            @Override
            public Promise<Object> call(Promise<Boolean> resolved) throws Exception {
                return updateViewData();
            }
        }, FAILURE);
    }

    @JsMethod
    void setModuleSettings(String key, String typeString, String fromData, String toData, String accessKey) {
    	Promise<DomClassCourseFull> f = update(key, typeString, fromData, toData, accessKey);

    	f.then((resolved) -> {
            return updateViewData();
        }, FAILURE);
    	
    }

	private Promise<DomClassCourseFull> update(String key, String typeString, String fromData, String toData,
			String accessKey) {
		Promise<DomClassCourseFull> f = 
    	tree.then( (Promise<DomCoursesOfSchoolclassTree>p) -> 
    	{
    		DomCoursesOfSchoolclassTree t = p.getValue();
    		DomCourseOfClass object = t.getNode(key).getObject();
			DomClassCourse4Teacher classCourse = object.getClassCourse();
			PersistenceId id = classCourse.getId();
    		DomCourse course = object.getCourse();			
			CourseType type = CourseType.valueOf(typeString);
			Date from = fromData.isEmpty() ? null : DateTimeFormat.getFormat(Display.LOCAL_TIME).parse(fromData);
			Date to = toData.isEmpty() ? null : DateTimeFormat.getFormat(Display.LOCAL_TIME).parse(toData);

			Date now = new Date();
// Als een assesment gestart is, mag je de eindtijd niet terugdraaien!
// geld ook voor Coursetype.kiosk
			if ( isAssesment(type) && isAssesment(classCourse.getCourseType()) &&					
				(classCourse.getNotBefore() == null || classCourse.getNotBefore().before(now))
				&& (to != null && classCourse.getNotAfter() != null && classCourse.getNotAfter().after(to))	
				&& classCourse.getNotAfter().after(now)
			   ) {
				view.setSettings(key);
				throw new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateValue, "Date " + toData + " is too early");
			}
// geen after < before
			if (from != null && to != null && from.after(to)) {
				throw new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "after " + to + " < before " + from);///
			}
			
			return service.setClassCourse(id, schoolClass, course, type, accessKey, from, to)
					.then(x -> {object.setClassCourse(x.getValue());return x; });
    	});
		return f;
	}

	private boolean isAssesment(CourseType type) {
		return type == CourseType.assesment || type == CourseType.kiosk;
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
    void setModuleSettings1(String key, String typeString, String fromData, String toData, String accessKey) {
        if (key == null) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "Internal error, key not given.")));
            return;
        }
        Promise<DomCoursesOfSchoolclassTree> p = tree;
        if (typeString != null) {
            p = p.then((resolved) -> {
                Promise<Boolean> setCourseType = setCourseType(resolved.getValue(), key, typeString);
				return setCourseType.then(pp -> resolved);
            });
        }
        if (fromData != null) {
            p = p.then((resolved) -> {
                return setFromDate(resolved.getValue(), key, fromData).then(pp-> resolved);
            });
        }
        if (toData != null) {
            p = p.then((resolved) -> {
                return setToDate(resolved.getValue(), key, toData).then(pp-> resolved);
            });
        }
        if (accessKey != null) {
            p = p.then((resolved) -> {
                return setAccessKey(resolved.getValue(), key, accessKey).then(pp-> resolved);
            });
        }

        p.then((resolved) -> {
            return updateViewData();
        }, FAILURE);
    }

    private Promise<Boolean> setCourseType(DomCoursesOfSchoolclassTree tree, String key, String typeString) {
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

    private Promise<Boolean> setFromDate(DomCoursesOfSchoolclassTree tree,String key, String dateString) {
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

    private Promise<Boolean> setToDate(DomCoursesOfSchoolclassTree tree, String key, String dateString) {
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
        DomClassCourse4Teacher classCourse = c.getObject().getClassCourse();
		if (date == null || classCourse.getNotBefore() == null
                || (classCourse.getNotBefore() != null
                && classCourse.getNotBefore().before(date))) {

			if (date != null && classCourse.getNotAfter() != null && date.before(classCourse.getNotAfter())
				&& (classCourse.getNotBefore() == null || classCourse.getNotBefore().after(new Date()))	
					
					
			) {

				eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is too early.")));
	            return null;
				
			}
        	
        	
        	
        	return service.setToDateClassCourse(schoolClass, c.getObject().getCourse(), date);
        } else {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.User_NotAValidDateString, "dateString " + dateString + " is not a valid dateString.")));
            return null;
        }
    }

    private Promise<Boolean> setAccessKey(DomCoursesOfSchoolclassTree tree, String key, String accessKey) {
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
    
    @JsMethod
    public boolean hasKiosk() {
      return dwoGlobalVars.isKiosk() && dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().hasKiosk();
    }

}
