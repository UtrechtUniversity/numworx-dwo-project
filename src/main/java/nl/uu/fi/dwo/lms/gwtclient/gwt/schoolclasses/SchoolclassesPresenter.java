package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class SchoolclassesPresenter {

    private static final Logger LOG = Logger.getLogger(SchoolclassesPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();

    private Map<String, DomSchoolClass> schoolClassMap;
    private Map<String, ClassItem> viewData;
    private String[] tableHeaders = {"classname", "edit", "modules", "students", "teachers", "remove"};

    private Display view;

    public interface Display {

        void clear();

        void init();

        void updateView(Map<String, SchoolclassesPresenter.ClassItem> data);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }

    public class ClassItem {

        public String key; //unique
        private String schoolclassName;

        public ClassItem(String aKey, String value) {
            key = aKey;
            schoolclassName = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String aKey) {
            key = aKey;
        }

        /**
         * @return the schoolclassName
         */
        public String getSchoolclassName() {
            return schoolclassName;
        }

        /**
         * @param schoolclassName the schoolclassName to set
         */
        public void setSchoolclassName(String schoolclassName) {
            this.schoolclassName = schoolclassName;
        }
    }

//    //** should become part of the PresenterFactories ie. DWO.LoginPresenter.loginClicked.
//    private native static void setDWO(SchoolclassesPresenter q) /*-{
//
//    	var apis = {
//    			"addSchoolClass" : function() {
//    				return q.@nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter::addSchoolClass()()
//                        }
//    		};
//    	$wnd.DwoSchoolclassesPresenter = apis;
//    }-*/;
    public SchoolclassesPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
//        setDWO(this);
    }

    public void init() {
        view.init();
        updateViewData();
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    private void updateViewData() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                schoolClassMap = new HashMap<String, DomSchoolClass>();
                viewData = new HashMap(schoolClassMap.size());
//                JSONObject json = new JSONObject();
                for (DomSchoolClass sc : resolved.getValue()) {
                    schoolClassMap.put(sc.getId().getIdString(), sc);
                    viewData.put(sc.getId().getIdString(), new ClassItem(sc.getId().getIdString(), sc.getSchoolClassName()));
                    //                  json.put(sc.getId().getIdString(), new JSONString (sc.getSchoolClassName()));
                }
//                view.updateJSView(json.getJavaScriptObject());
                view.updateView(viewData);
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
    
    /** Adds a SchoolClass to the school and refreshes the panel */
    @JsMethod
    public void AddSchoolClass(String name, Boolean showTree, Boolean hasRegKey, String regKey) {
        Promise<Boolean> promise;
        DomSchoolClassFull schoolClass = new DomSchoolClassFull();
        schoolClass.setSchoolClassName(name);
        schoolClass.setIconizer(showTree);
        schoolClass.setHasRegKey(hasRegKey);
        schoolClass.setRegistrationKey(regKey);
        promise = manager.submitSchoolClass(schoolClass);
        // onSuccess calculate results and show.
        promise.then(new Success<Boolean,Void> () {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                if (resolved.getValue() == true) {
                    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                    return null;
                } else {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Rest request failed for unknown reasons.");
                }
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

    @JsMethod
    public void editSchoolClass(String key) {
        eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.EditSchoolClass, schoolClassMap.get(key)));
    }
//
//    @JsMethod
//    public void connectStudents(String key) {
//    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDSTUDENTTOSCHOOLCLASS,
//        schoolClassMap.get(key)));
//    }
//
//    @JsMethod
//    public void connectTeachers(String key) {
//    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ADDTEACHERTOSCHOOLCLASS,
//        schoolClassMap.get(key)));
//    }
//
//    @JsMethod
//    public void editModules(String key) {
//        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITCOURSESOFSCHOOLCLASS, schoolClassMap.get(key)));
//    }
//
//    @JsMethod
//    public void removeSchoolClass(String key) {
//        removeSchoolClass(schoolClassMap.get(key));
//    }
}
