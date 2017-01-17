package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * The information in the DomResultsPerTeacher class is inserted client-side
 * into this simplified kd-range tree. The kd-tree has a search range of 1 and
 * has a node type from root to leave a sequence of: DomTeacher, DomSchoolClass,
 * DomClassCourse referred DomCourse,DomCourse, ..., DomCourse. A leave of the
 * kd-tree is by definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class ResultTree {

    private DomResultTeacher root;
    private DomResultsPerTeacher restData;

    public ResultTree(DomResultsPerTeacher resultData) {
        restData = resultData;
        buildResultTree(restData);
        //reCalculateResults();
    }

    /**
     * Takes the data of a DomResultsPerTeacher object and builds a result tree.
     * The result tree can be crawled and data can be collected for result
     * viewing of studentsco data. The result tree is a mixed object type tree
     * where every tree path has the following object type sequence:
     *
     * DomResultTeacher, DomResultSchoolClass, one or more DomCourse,
     * DomStudent, DomSco,DomStudentSco.
     *
     * @param resultData
     */
    private void buildResultTree(DomResultsPerTeacher resultData) {
        //build tree from results collection

        //set the root teacher
        setRoot(new DomResultTeacher(resultData.getTeacher()));
        //set the schoolclasses of the teacher
        Map<PersistenceId, DomResultSchoolClass> schoolClasses = new HashMap<PersistenceId, DomResultSchoolClass>(resultData.getSchoolClasses().size());
        getRoot().setChildren(schoolClasses);
        for (PersistenceId key : resultData.getSchoolClasses().keySet()) {
            DomResultSchoolClass value = new DomResultSchoolClass(resultData.getSchoolClasses().get(key));
            value.setParent(getRoot());
            schoolClasses.put(key, value);

        }

        //Scan all DomCourses and map them into a DomResultCourse map
        Map<PersistenceId, DomResultCourse> resultCourseMap = new HashMap<PersistenceId, DomResultCourse>(resultData.getCourses().size());
        for (PersistenceId id : resultData.getCourses().keySet()) {
            resultCourseMap.put(id, new DomResultCourse(resultData.getCourses().get(id)));
        }

        //Scan all DomResultCourses and build them into a tree
        for (PersistenceId id : resultCourseMap.keySet()) {
            if (resultCourseMap.get(id).getCourse().getParentID() != null) {
                DomResultCourse parentCourse = resultCourseMap.get(resultCourseMap.get(id).getCourse().getParentID());
                if (parentCourse != null) {
                    parentCourse.getChildren().put(id, resultCourseMap.get(id).getCourse());
                }
                    //connect the sco later
//                if (!resultCourseMap.get(id).getCourse().getWithChildren()) {                    
//                }
            }
        }

        //for each of the schoolclasses set the courses
        for (PersistenceId key : resultData.getClassCourses().keySet()) {
            DomClassCourse cc = resultData.getClassCourses().get(key);
            DomResultCourse resultCourse = resultCourseMap.get(cc.getCourseId());
            schoolClasses.get(cc.getClassId()).getChildren().put(resultCourse.getCourse().getId(), resultCourse);
        }
        
        Map<PersistenceId, DomResultScoContext> scoContextMap = new HashMap<PersistenceId, DomResultScoContext>(resultData.getScoContexts().size());
        //Connect all DomResultScoContext with all leave DomResultCourses
        //for each of the schoolclasses set the courses
        for (PersistenceId key : resultData.getScoContexts().keySet()) {
            DomResultScoContext scoContext = new DomResultScoContext(resultData.getScoContexts().get(key));
            //fill map for connecting studentScoContext data
            scoContextMap.put(key, scoContext);
            DomResultCourse resultCourse = resultCourseMap.get(resultData.getScoContexts().get(key).getCourseId());
            resultCourse.getChildren().put(resultCourse.getCourse().getId(), scoContext);
         }
        
        for (PersistenceId id : resultData.getStudentScoContexts().keySet()) {
            DomStudent student = resultData.getStudents().get(resultData.getStudentScoContexts().get(id).getUserID());
            DomResultStudentSco studentSco = new DomResultStudentSco(resultData.getStudentScoContexts().get(id),student);
            DomResultScoContext scoContext = scoContextMap.get(resultData.getStudentScoContexts().get(id).getId());
            scoContext.getChildren().put(id, studentSco);
        }
   }

    /**
     * @return the root
     */
    public DomResultTeacher getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(DomResultTeacher root) {
        this.root = root;
    }

    /**
     * @return the restData
     */
    public DomResultsPerTeacher getRestData() {
        return restData;
    }

    /**
     * @param restData the restData to set
     */
    public void setRestData(DomResultsPerTeacher restData) {
        this.restData = restData;
    }
}
