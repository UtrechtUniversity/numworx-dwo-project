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
    private Map<String, StudentsInSchoolclassPresenter.StudentItem> viewData;
    private Display view;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, StudentsInSchoolclassPresenter.StudentItem> data);
    }

    public class StudentItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;

        public StudentItem(String aKey, String aFirstName,String anInsertion,String aFamilyName, String aUsercode) {
            key = aKey;
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
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
                    viewData.put(sc.getId().getIdString(), 
                            new StudentItem(sc.getId().getIdString(), 
                                    sc.getGivenName(),
                                    sc.getInsertion(),
                                    sc.getFamilyName(),
                                    sc.getUserName()
                                    ));
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
    /**
     * @param item
     * @param op
     */
    public void selectItem(StudentsInSchoolclassPresenter.StudentItem item, int op) {
        switch (op) {
            default:
                throw new UnsupportedOperationException("Not supported yet."); 
        }
    }
}
