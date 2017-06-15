package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
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

    private List<DomSchoolClass> schoolclassList;
    private Map<String, ClassItem> viewData;
    private String[] tableHeaders = {"classname", "edit", "modules", "students", "teachers", "remove"};

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void addSchoolClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void updateSchoolClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void deleteSchoolClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void addTeacher() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void deleteTeacher() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void addStudent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void updateStudent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void deleteStudent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, SchoolclassesPresenter.ClassItem> data);
    }

    public class ClassItem {

        public String key; //unique
        public String schoolclassName;

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
    }

    public SchoolclassesPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public List<DomSchoolClass> getSchoolClasses() {
        return schoolclassList;
    }

    private void updateViewData() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        // onSuccess update view
        promise.then(new Success<List<DomSchoolClass>, Void>() {
            @Override
            public Promise<Void> call(Promise<List<DomSchoolClass>> resolved) throws Exception {
                //flip back to schoolclasses screen 
                schoolclassList = resolved.getValue();
                viewData = new HashMap(schoolclassList.size());
                for (DomSchoolClass sc : schoolclassList) {
                    viewData.put(sc.getId().getIdString(), new ClassItem(sc.getId().getIdString(), sc.getSchoolClassName()));
                }
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
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });

    }

    public void init() {
        view.init();
        updateViewData();
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    /**
     * @param row the course to set
     */
    public void selectItem(ClassItem item, int op) {

    }

}
