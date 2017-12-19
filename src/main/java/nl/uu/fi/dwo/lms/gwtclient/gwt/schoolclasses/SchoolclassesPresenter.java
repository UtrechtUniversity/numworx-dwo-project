package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogEvent;
import fi.dwo.gwt.lib.rest.ui.ConfirmDialogPromise;
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

    private void removeSchoolClass(DomSchoolClass schoolClass) {
        ConfirmDialogPromise p = new ConfirmDialogPromise("Are you sure you want to remove schoolclass" + schoolClass.getSchoolClassName() + ".");
        p.getPromise().then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                LOG.log(Level.INFO, "returned value" + resolved.getValue());
                if (resolved.getValue() == true) {
                    executeRemoveSchoolClass(schoolClass);
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

    private void executeRemoveSchoolClass(DomSchoolClass schoolClass) {

        Promise<Boolean> promise;
        promise = manager.removeSchoolClass(schoolClass);
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                boolean result = resolved.getValue();
                if (result != true) {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "system error, try again please report.");
                }
                updateViewData();
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

    /**
     * @param item
     * @param op
     */
    public void selectItem(ClassItem item, int op) {
        switch (op) {
            case 1:
                editSchoolClass(item.key);
                break;
            case 2:
                editModules(item.key);
                break;
            case 3:
                editStudents(item.key);
                break;
            case 4:
                editTeachers(item.key);
                break;
            case 5:
                removeSchoolClass(item.key);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @JsMethod
    public void addSchoolClass() {
        eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.AddSchoolClass, new DomSchoolClass()));
    }

    @JsMethod
    public void editSchoolClass(String key) {
        eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.EditSchoolClass, schoolClassMap.get(key)));
    }

    @JsMethod
    public void editStudents(String key) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTSINSCHOOLCLASS, schoolClassMap.get(key)));
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @JsMethod
    public void editTeachers(String key) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.TEACHERSINSCHOOLCLASS, schoolClassMap.get(key)));
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @JsMethod
    public void editModules(String key) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.COURSESOFSCHOOLCLASS, schoolClassMap.get(key)));
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @JsMethod
    public void removeSchoolClass(String key) {
        removeSchoolClass(schoolClassMap.get(key));
    }
}
