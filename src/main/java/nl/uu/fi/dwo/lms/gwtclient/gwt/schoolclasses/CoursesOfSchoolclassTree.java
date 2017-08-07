package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * The information in the DomResultsPerTeacher class is inserted client-side
 * into this simplified kd-range tree. The kd-tree has a search range of 1 and
 * has a node type from courseTree to leave a sequence of: DomTeacher,
 * DomSchoolClass, DomClassCourse referred DomCourse,DomCourse, ..., DomCourse.
 * A leave of the kd-tree is by definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class CoursesOfSchoolclassTree {
    
    private static final Logger LOG = Logger.getLogger(CoursesOfSchoolclassTree.class.getName());
    
    private DomTree<DomCourseOfClass> courseTree;
    private Map<String, DomTree> cocMap;
    
    public CoursesOfSchoolclassTree(DomCoursesOfSchoolClass4Teacher resultData) {
        //restData = resultData;
        LOG.log(Level.INFO, "Initializing a DomCoureTree.");
        courseTree = buildCourseTree(resultData);
        //reCalculateResults();
    }
    
    private DomTree<DomCourseOfClass> buildCourseTree(DomCoursesOfSchoolClass4Teacher resultData) {
        cocMap = new HashMap<String, DomTree>(resultData.getCourses().size());
        Map<String, DomClassCourse> classCourseMap = new HashMap<String, DomClassCourse>(resultData.getClassCourses().size());
        
        for (DomMapEntry<PersistenceId, DomCourse> courseEntry : resultData.getCourses()) {
            
            cocMap.put(courseEntry.getKey().getIdString(), new DomTree<DomCourseOfClass>(new DomCourseOfClass(courseEntry.getValue())));
        }
        
        for (DomMapEntry<PersistenceId, DomClassCourse> ccEntry : resultData.getClassCourses()) {
            classCourseMap.put(ccEntry.getValue().getCourseId().getIdString(), ccEntry.getValue());
        }

        //build tree
        DomTree<DomCourseOfClass> root = new DomTree<DomCourseOfClass>(new DomCourseOfClass());
        for (DomTree<DomCourseOfClass> n : cocMap.values()) {
            //attach classCourse if it exists
            if (classCourseMap.containsKey(n.getObject().getCourse().getId().getIdString())) {
                    n.getObject().setClassCourse(classCourseMap.get(n.getObject().getCourse().getId().getIdString()));
                }
            //build tree in O(n) time
            if (n.getObject().getCourse().getParentID() == null) {
                //if c exists in classCourses add to node
                if (classCourseMap.containsKey(n.getObject().getCourse().getId().getIdString())) {
                    n.getObject().setClassCourse(classCourseMap.get(n.getObject().getCourse().getId().getIdString()));
                }
                //add to root node
                root.getChildren().put(n.getObject().getCourse().getId().getIdString(), n);
            } else {
                //add to parent in tree
                //get course parent
                String stringIdParent = n.getObject().getCourse().getParentID().getIdString();
                cocMap.get(stringIdParent).getChildren().put(n.getObject().getCourse().getId().getIdString(), n);
            }
        }
        //dump tree to logging
        LOG.log(Level.FINE, "Dumping DomCourseTree (depth, name).");
        setCourseTree(root);
        DFSTreePrint(root);
        return root;
    }
    
    private void DFSTreePrint(DomTree<DomCourseOfClass> node) {
        DFSTreePrint(node, 0);
    }
    private void DFSTreePrint(DomTree<DomCourseOfClass> node, int depth) {
        // do depth first search       
        if(node.getChildren() != null && !node.getChildren().isEmpty()) {
                depth++;
            for (DomTree<DomCourseOfClass> coc : node.getChildren().values()) {
                LOG.log(Level.FINE, "(" + depth + ","+coc.getObject().getCourse().getName()+")");
                if (coc.getChildren() != null && !coc.getChildren().isEmpty()) {
                    for (DomTree<DomCourseOfClass> child : node.getChildren().values()) {
                        DFSTreePrint(child, depth);
                    }
                }
            }
            depth--;
        }
        
    }
    
    public DomTree<DomCourseOfClass> getNode(String key){
        return cocMap.get(key);
    }

    /**
     * @return the courseTree
     */
    public DomTree<DomCourseOfClass> getCourseTree() {
        return courseTree;
    }

    /**
     * @param courseTree the courseTree to set
     */
    public void setCourseTree(DomTree<DomCourseOfClass> courseTree) {
        this.courseTree = courseTree;
    }
}
