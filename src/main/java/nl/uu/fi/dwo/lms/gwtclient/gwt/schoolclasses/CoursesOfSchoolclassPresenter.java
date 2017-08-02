package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import org.osgi.util.promise.Promise;

/**
 * Handler for for Login actions. Fetches courses in classcourses of
 * schoolclass, dynamically fetches other courses and shows black bar.
 *
 * @author Gert van der Plas
 */
public class CoursesOfSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(CoursesOfSchoolclassPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private CoursesOfSchoolclassService service = new CoursesOfSchoolclassService();

    private String[] tableHeaders = {"Module name", "studentdata", "type", "from", "to"};
    private DomSchoolClass schoolClass;
    private DomCoursesOfSchoolClass4Teacher moduleInfo;
    private Map<String, DomStudent> studentMap;
    private Map<String, CoursesOfSchoolclassPresenter.CourseItem> studentItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, CoursesOfSchoolclassPresenter.CourseItem> data);

    }

    public class CourseItem {
//        /"Module name", "studentdata", "type", "from", "to"
        public String key; //unique
        public String name;
        public String hasStudentData;
        public String type;
        public Date from;
        public Date to;

        public CourseItem(String aKey, String aName, String hasData, String aType, Date aFrom, Date aTo) {
            key = aKey;
            name = aName;
            hasStudentData = hasData;
            type = aType;
            from = aFrom;
            to = aTo;
        }
    }

    public CoursesOfSchoolclassPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
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
        updateViewData(aSchoolClass);
    }

    private void updateViewData(DomSchoolClass sc) {
        Promise<DomCoursesOfSchoolClass4Teacher> promise;        
        promise = service.getModules(sc);
//        // onSuccess update view
//        promise.then(new Success<List<DomStudent>, Void>() {
//            @Override
//            public Promise<Void> call(Promise<List<DomStudent>> resolved) throws Exception {
//                //flip back to schoolclasses screen 
//                studentMap = new HashMap<String, DomStudent>();
//                Map<String, CoursesOfSchoolclassPresenter.CourseItem> oldStudentItems = studentItems;
//                studentItems = new HashMap(studentMap.size());
//                for (DomStudent sc : resolved.getValue()) {
//                    studentMap.put(sc.getId().getIdString(), sc);
//                    CourseItem item = new CourseItem(sc.getId().getIdString(),
//                            sc.getGivenName(),
//                            sc.getInsertion(),
//                            sc.getFamilyName(),
//                            sc.getUserName(),
//                            sc.getSingleSchool()
//                    );
//                    if (oldStudentItems != null
//                            && oldStudentItems.containsKey(sc.getId().getIdString())
//                            && oldStudentItems.get(sc.getId().getIdString()).selected) {
//                        item.selected = true;
//                    }
//                    studentItems.put(sc.getId().getIdString(), item);
//                }
//                view.updateView(studentItems);
//                return null;
//            }
//
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                Throwable fail = resolved.getFailure();
//                view.updateView(studentItems);
//                if (fail instanceof Dwo2Exception) {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                } else {
//                    LOG.log(Level.SEVERE, fail.getMessage());
//                    //throw directly
//                }
//            }
//        });

    }


    public String[] getTableHeaders() {
        return tableHeaders;
    }
//
//    /**
//     * @param item
//     * @param op
//     */
//    public void selectItem(CoursesOfSchoolclassPresenter.CourseItem item, int op) {
//        switch (op) {
//            case 4:
//                if (item.singleSchool) {
//                    LOG.log(Level.INFO, "editable item " + item.usercode);
//                    eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.EditStudent, studentMap.get(item.key), schoolClass));
//                }
//                break;
//            case 5:
//                item.selected = !item.selected;
//                LOG.log(Level.INFO, "item " + item.usercode + " state " + item.selected);
//                break;
//            default:
//                throw new UnsupportedOperationException("Not supported yet.");
//        }
//    }

    void goBackToSchoolClasses() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

}
