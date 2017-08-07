package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

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
    private DomCourseTree tree;
    private Map<String, DomStudent> studentMap;
    private Map<String, CoursesOfSchoolclassPresenter.CourseClassItem> courseItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, CoursesOfSchoolclassPresenter.CourseClassItem> data);

    }

    public class CourseItem {
        public CourseItem parent;
        public CourseItem children;
        public String name;
    }

    
    public class CourseClassItem extends CourseItem{
//        /"Module name", "studentdata", "type", "from", "to"
        public String key; //unique
        public String hasStudentData;
        public String type;
        public Date from;
        public Date to;

        public CourseClassItem(String aKey, String aName, String hasData, String aType, Date aFrom, Date aTo) {
            key = aKey;
            name = aName;
            hasStudentData = hasData;
            type = aType;
            from = aFrom;
            to = aTo;
        }
    }

//    public CourseItem getRoot(){
//        CourseItem item = new CourseItem();
//        item.name = tree.getCourseTree().getObject().getCourse().getName();
//        item.parent = tree.getCourseTree().getObject().getCourse().getId().getIdString();
//        item.children = null;
//    }
    
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
        // onSuccess update view
        promise.then(new Success<DomCoursesOfSchoolClass4Teacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomCoursesOfSchoolClass4Teacher> resolved) throws Exception {                
                //flip back to schoolclasses screen 
                DomCoursesOfSchoolClass4Teacher value = resolved.getValue();
                tree = new DomCourseTree(value);
                //parse results into a tree.
                view.updateView(courseItems);
                return null;
            }

        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                view.updateView(courseItems);
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
//
//    /**
//     * @param item
//     * @param op
//     */
//    public void selectItem(CoursesOfSchoolclassPresenter.CourseClassItem item, int op) {
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
