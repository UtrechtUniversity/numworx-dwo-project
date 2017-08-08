package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
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
    private CoursesOfSchoolclassTree tree;
    private Map<String, DomStudent> studentMap;
    private Map<String, ClassCourseItem> courseItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();
        void updateTable(List<ClassCourseItem> item);
        void updateTree(CourseItem item);

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
                tree = new CoursesOfSchoolclassTree(value);
                CourseItem item = new CourseItem("root", "root");
                //parse results into a tree.
                view.updateTree(item);
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

    public CourseItem getRootNode() {
        DomTree<DomCourseOfClass> c = tree.getCourseTree();
        if (c.getObject().getClassCourse() == null) {
            CourseItem item = new CourseItem(null, c.getObject().getCourse().getName());
            return item;
        } else {
//            /String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo
            ClassCourseItem item = new ClassCourseItem("root",
                    c.getObject().getCourse().getName(),
                    false,
                    c.getObject().getClassCourse().getType(),
                    c.getObject().getClassCourse().getNotBefore(),
                    c.getObject().getClassCourse().getNotAfter()
            );
            return item;
        }

    }

    public CourseItem getNode(String key) {
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (c.getObject().getClassCourse() == null) {
            CourseItem item = new CourseItem(null, c.getObject().getCourse().getName());
            if (c.getChildren() == null || c.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            return item;
        } else {
//            /String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo
            ClassCourseItem item = new ClassCourseItem(key,
                    c.getObject().getCourse().getName(),
                    false,
                    c.getObject().getClassCourse().getType(),
                    c.getObject().getClassCourse().getNotBefore(),
                    c.getObject().getClassCourse().getNotAfter()
            );
            if (c.getChildren() == null || c.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            return item;
        }

    }

    public List<CourseItem> getNodeChildren(String key) {
        if (tree == null) {
            return new ArrayList<CourseItem>();
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);

        List<CourseItem> itemList = new ArrayList<CourseItem>(c.getChildren().size());
        for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
            CourseItem item = new CourseItem(coc.getObject().getCourse().getId().getIdString(), coc.getObject().getCourse().getName());
            if (coc.getChildren() == null || coc.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            itemList.add(item);
        }

        return itemList;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    void setSelectedItem(CourseItem item) {
        boolean flag = false;
        DomTree<DomCourseOfClass> c = tree.getNode(item.getKey());
        for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
            if (coc.getChildren().size() == 0) {
                flag = true;
                break;
            }
        }
        if (flag) {
            //Show children in cellTable
            LOG.log(Level.INFO, "Going to show children in table");
            List<ClassCourseItem> ccList = new ArrayList<>(c.getChildren().size());
            for (DomTree<DomCourseOfClass> coc :c.getChildren().values()){
                //creat list for table
                ClassCourseItem cc = new ClassCourseItem();
                cc.setKey(coc.getObject().getCourse().getId().getIdString());
                cc.setName(coc.getObject().getCourse().getName());
                cc.setHasStudentData(false);
                cc.setFrom(coc.getObject().getClassCourse().getNotBefore());
                cc.setTo(coc.getObject().getClassCourse().getNotAfter());
//                ClassCourseItem cc = new ClassCourseItem(coc.getObject().getCourse().getId().getIdString(), 
//                        coc.getObject().getCourse().getName(), false, coc.getObject().getClassCourse().getType(), 
//                        coc.getObject().getClassCourse().getNotBefore(), 
//                        coc.getObject().getClassCourse().getNotAfter());
                ccList.add(cc);
            };
            view.updateTable(ccList);
        } else {
            List<ClassCourseItem> ccList = new ArrayList<>(c.getChildren().size());
            view.updateTable(ccList);
            LOG.log(Level.INFO, "Selected item " + item.getName());
        }
    }

    void goBackToSchoolClasses() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
    }

}
