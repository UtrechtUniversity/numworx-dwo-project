package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
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

    private String[] tableHeaders = {"module name", "assigned", "type", "from", "to"};
    private DomSchoolClass schoolClass;
//    private DomCoursesOfSchoolClass4Teacher moduleInfo;
    private CoursesOfSchoolclassTree tree;
//    private Map<String, DomStudent> studentMap;
//    private Map<String, ClassCourseItem> courseItems;
//    private Map<String, DomSchoolClass> schoolClassMap;
//    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;

    void detachItemFromSchoolClass(ClassCourseItem classCourseItem) {
        Promise<Boolean> promise;
        promise = service.detachCourseFromClass(schoolClass,tree.getNode(classCourseItem.getKey()).getObject().getCourse());
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                updateViewData();
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

    void attachItemToSchoolClass(ClassCourseItem classCourseItem) {
        Promise<Boolean> promise;
        promise = service.attachCourseToClass(schoolClass,tree.getNode(classCourseItem.getKey()).getObject().getCourse());
        // onSuccess update view
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                updateViewData();
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

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateTable(List<ClassCourseItem> item);

        void setTree(ClassCourseItem item);

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
        updateViewData();
    }

    private void updateViewData() {
        Promise<DomCoursesOfSchoolClass4Teacher> promise;
        promise = service.getModules(schoolClass);
        // onSuccess update view
        promise.then(new Success<DomCoursesOfSchoolClass4Teacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomCoursesOfSchoolClass4Teacher> resolved) throws Exception {
                //flip back to schoolclasses screen 
                DomCoursesOfSchoolClass4Teacher value = resolved.getValue();
                tree = new CoursesOfSchoolclassTree(value);
                ClassCourseItem item = new ClassCourseItem(null, "root");
                //parse results into a tree.
                view.setTree(item);
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

    public ClassCourseItem getRootNode() {
        DomTree<DomCourseOfClass> c = tree.getCourseTree();
        if (c.getObject().getClassCourse() == null) {
            ClassCourseItem item = new ClassCourseItem(null, c.getObject().getCourse().getName());
            return item;
        } else {
//            /String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo
            ClassCourseItem item = new ClassCourseItem("root",
                    c.getObject().getCourse().getName(),
                    false,
                    c.getObject().getClassCourse().getType().name(),
                    c.getObject().getClassCourse().getNotBefore(),
                    c.getObject().getClassCourse().getNotAfter()
            );
            return item;
        }

    }

    public ClassCourseItem getNode(String key) {
        DomTree<DomCourseOfClass> c = tree.getNode(key);
        if (c.getObject().getClassCourse() == null) {
            ClassCourseItem item = new ClassCourseItem(null, c.getObject().getCourse().getName());
            item.setHasStudentData(false);
                if (c.getChildren() == null || c.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            return item;
        } else {
//            /String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo
            ClassCourseItem item = new ClassCourseItem(key,
                    c.getObject().getCourse().getName(),
                    true,
                    c.getObject().getClassCourse().getType().name(),
                    c.getObject().getClassCourse().getNotBefore(),
                    c.getObject().getClassCourse().getNotAfter()
            );
            if (c.getChildren() == null || c.getChildren().size() == 0) {
                item.setIsLeaf(true);
            }
            return item;
        }

    }

    public List<ClassCourseItem> getNodeChildren(String key) {
        if (tree == null) {
            return new ArrayList<ClassCourseItem>();
        }
        DomTree<DomCourseOfClass> c = tree.getNode(key);

        List<ClassCourseItem> itemList = new ArrayList<ClassCourseItem>(c.getChildren().size());
        for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
            ClassCourseItem item = new ClassCourseItem(coc.getObject().getCourse().getId().getIdString(), coc.getObject().getCourse().getName());
            if(coc.getObject().getClassCourse()!=null){
                item.setHasStudentData(true);
            }else{
                item.setHasStudentData(false);
            }
            if (!coc.getObject().getCourse().getWithChildren()) {
                item.setIsLeaf(true);
            }
            itemList.add(item);
        }

        return itemList;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    void setSelectedItem(ClassCourseItem item) {
        boolean isLeaf = false;
        DomTree<DomCourseOfClass> c = tree.getNode(item.getKey());
        for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
            if (coc.getChildren()==null || coc.getChildren().isEmpty()) {
                isLeaf = true;
                break;
            }
        }
        if (isLeaf ) {
            //Show children in cellTable
            LOG.log(Level.INFO, "Going to show children in table");
            List<ClassCourseItem> ccList = new ArrayList<>(c.getChildren().size());
            for (DomTree<DomCourseOfClass> coc : c.getChildren().values()) {
                //creat list for table
                if(!coc.getObject().getCourse().getWithChildren() &&
                        (coc.getChildren()==null || coc.getChildren().isEmpty())){
                ClassCourseItem cc = new ClassCourseItem();
                    cc.setKey(coc.getObject().getCourse().getId().getIdString());
                    cc.setName(coc.getObject().getCourse().getName());
                    cc.setHasStudentData(false);
                if (coc.getObject().getClassCourse()!=null) {
                    cc.setType(coc.getObject().getClassCourse().getType().name());
                    cc.setFrom(coc.getObject().getClassCourse().getNotBefore());
                    cc.setTo(coc.getObject().getClassCourse().getNotAfter());
                    cc.setHasStudentData(true);
                }
                    cc.setIsLeaf(coc.getObject().getCourse().getWithChildren());
                    ccList.add(cc);
                }
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
