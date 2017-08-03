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
import nl.uu.fi.dwo.rest.dom.entities.DomTree;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * The information in the DomResultsPerTeacher class is inserted client-side
 into this simplified kd-range tree. The kd-tree has a search range of 1 and
 has a node type from courseTree to leave a sequence of: DomTeacher,
 DomSchoolClass, DomClassCourse referred DomCourse,DomCourse, ..., DomCourse.
 A leave of the kd-tree is by definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class DomCourseTree {

    private static final Logger LOG = Logger.getLogger(DomCourseTree.class.getName());

    private DomTree<DomCourseOfClass> courseTree;

    public DomCourseTree(DomCoursesOfSchoolClass4Teacher resultData) {
        //restData = resultData;
        LOG.log(Level.INFO, "Initializing a DomCoureTree.");
        courseTree = buildCourseTree(resultData);
        //reCalculateResults();
    }

    
    private DomTree<DomCourseOfClass> buildCourseTree(DomCoursesOfSchoolClass4Teacher resultData){
        Map<PersistenceId, DomTree> cocMap = new HashMap<PersistenceId, DomTree>(resultData.getCourses().size());
        Map<PersistenceId, DomClassCourse> classCourseMap = new HashMap<PersistenceId, DomClassCourse>(resultData.getClassCourses().size());
        
        for (DomMapEntry<PersistenceId, DomCourse> course : resultData.getCourses()) {
            
                cocMap.put(course.getKey(), new DomTree<DomCourseOfClass>(new DomCourseOfClass(course.getValue())));
            }

        for (DomMapEntry<PersistenceId, DomClassCourse> cc : resultData.getClassCourses()) {
                classCourseMap.put(cc.getValue().getCourseId(), cc.getValue());
            }
        
        //build tree
        DomTree<DomCourseOfClass> root = new DomTree<DomCourseOfClass>(new DomCourseOfClass());
        for(DomTree<DomCourseOfClass> n: cocMap.values()){
            if(n.getObject().getCourse().getParentID()==null) {                
                //if c exists in classCourses add to node
                if(classCourseMap.containsKey(n.getObject().getCourse().getId())){
                    n.getObject().setClassCourse(classCourseMap.get(n.getObject().getCourse().getId()));
                }
                //add to root node
                courseTree.getChildren().put(n.getObject().getCourse().getId(),n);
            }else{
                //add to parent in tree
                cocMap.get(n.getObject().getCourse().getId());
            }
        }        
        return root;
    }
}
