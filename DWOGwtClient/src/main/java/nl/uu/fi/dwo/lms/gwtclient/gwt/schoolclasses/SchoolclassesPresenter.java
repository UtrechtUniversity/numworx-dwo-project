package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceTeacher;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
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
    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final PersonsService manager;
    private final LoggingFailure FAILURE;

    private Map<String, DomSchoolClass> schoolClassMap;
    private Map<String, ClassItem> viewData;
    private String[] tableHeaders = {"classname", "edit", "modules", "students", "teachers", "remove"};

    private Display view;

    public interface Display extends BasicDisplay {

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

    @Inject SchoolclassesPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        manager = m;
        FAILURE = new LoggingFailure(LOG, anEventBus);
    }
    
//    public SchoolclassesPresenter(EventBus bus, DwoGlobalVars vars) {
//      this(bus, vars, new PersonsServiceTeacher(vars));
//    }
 
    public void init() {
        view.clear();
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
        view.setHelp(dwoGlobalVars.buildHelpUrl("#schoolclasses"+dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName()));
    }

    private void updateViewData() {
        view.setLoadingTableMessage();
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                schoolClassMap = new HashMap<String, DomSchoolClass>();
                viewData = new HashMap(schoolClassMap.size());
                for (DomSchoolClass sc : resolved.getValue()) {
                    schoolClassMap.put(sc.getId().getIdString(), sc);
                    viewData.put(sc.getId().getIdString(), new ClassItem(sc.getId().getIdString(), sc.getSchoolClassName()));
                }
                view.updateView(viewData);
                if(viewData.isEmpty())
                  view.setEmptyTableMessage();
                return null;
            }

        }, FAILURE)
        .recover((p) -> { view.setEmptyTableMessage(); return null; });

        ;
    }
    
    /** Adds a SchoolClass to the school and refreshes the panel */
    @JsMethod
    public void AddSchoolClass(String name, Boolean showTree, Boolean hasRegKey, String regKey) {
        Promise<Boolean> promise;
        DomSchoolClassFull schoolClass = new DomSchoolClassFull();
        schoolClass.setSchoolClassName(name);
        schoolClass.setIconizer(showTree);
        schoolClass.setHasRegKey(hasRegKey);
        schoolClass.setRegistrationKey(hasRegKey ? regKey : null);
        promise = manager.submitSchoolClass(schoolClass);
        // onSuccess calculate results and show.
        promise.then(new Success<Boolean,Void> () {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                if (resolved.getValue() == true) {
                    init();
                    return null;
                } else {
                    Dwo2Exception ex = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Rest request failed for unknown reasons.");
                    eventBus.fireEvent(new AlertDialogWithOKEvent(ex));
                    throw ex;
                }
            }
        }, FAILURE );
    }

    @JsMethod
    public void editSchoolClass(String key) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.EDITSCHOOLCLASS, schoolClassMap.get(key)));
    }
}
