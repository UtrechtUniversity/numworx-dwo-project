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
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
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

    private String[] tableHeaders = {"usercode", "givenname", "insertion", "familyname", "edit", "select"};
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> studentMap;
    private Map<String, StudentsInSchoolclassPresenter.ClassItem> viewData;
    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void addASchoolClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, StudentsInSchoolclassPresenter.ClassItem> data);
    }

    public class ClassItem {

        public String key; //unique
        public String schoolclassName;

        public ClassItem(String aKey, String value) {
            key = aKey;
            schoolclassName = value;
        }
    }

    public StudentsInSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    private List<DomSchoolRoleAndClassV2> getTeacherRoles() {
        List<DomSchoolRoleAndClassV2> result = new ArrayList<DomSchoolRoleAndClassV2>();
        DomSchoolsRolesAndClassesV2 sl = dwoGlobalVars.getSchoolLogins();
        List<DomSchoolRoleAndClassV2> fullList = sl.getSchoolsRolesAndClassesList();
        for (DomSchoolRoleAndClassV2 hasRole : fullList) {
            if (hasRole.getRole().getRoleName().equals("TEACHER")) {
                result.add(hasRole);
            }
        }
        return result;
    }

    public void init(DomSchoolClass aSchoolClass) {
        view.init();
        updateViewData(aSchoolClass);
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
                viewData = new HashMap(studentMap.size());
                for (DomStudent sc : resolved.getValue()) {
                    studentMap.put(sc.getId().getIdString(), sc);
                    viewData.put(sc.getId().getIdString(), new ClassItem(sc.getId().getIdString(), sc.getUniqueDisplayName()));
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

    public String[] getTableHeaders() {
        return tableHeaders;
    }

}
