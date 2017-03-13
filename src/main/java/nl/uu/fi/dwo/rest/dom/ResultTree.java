package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * The information in the DomResultsPerTeacher class is inserted client-side
 into this simplified kd-range tree. The kd-tree has a search range of 1 and
 has a node type from resultTree to leave a sequence of: DomTeacher, DomSchoolClass,
 DomClassCourse referred DomCourse,DomCourse, ..., DomCourse. A leave of the
 kd-tree is by definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class ResultTree {

    private DomResultTeacher resultTree;
    private DomResultTeacher studentTree;

    public ResultTree(DomResultsPerTeacher resultData) {
        //restData = resultData;
        buildResultTree(resultData);
        //reCalculateResults();
    }

    /**
     * Takes the data of a DomResultsPerTeacher object and builds two result trees.
     * The result trees can be crawled and data can be collected for result
     * viewing of studentsco data. The result trees are a mixed object type tree
     * where every tree path has the following object type sequence:
     *
     * resultTree:DomResultTeacher, DomResultSchoolClass, one or more DomCourse, DomSco,
     * DomStudentSco.
     * 
     * or
     * 
     * studentTree: DomResultTeacher, DomResultSchoolClass, DomStudent
     *
     * @param resultData
     */
    private void buildResultTree(DomResultsPerTeacher resultData) {
        /* build tree from results collection
           create the treeroot a DomResultTeacher, Attach the DomResultSchoolclass
           objects, create the subtrees of the DomCourse objects, attach them 
           to the DomResultSchoolClasses, add the Students per schoolclass

        */
        
        //set the resultTree teacher
        setResultTree(new DomResultTeacher(resultData.getTeacher()));
        setStudentTree(new DomResultTeacher(resultData.getTeacher()));
        //set the schoolclasses of the teacher
        Map<PersistenceId, DomResultSchoolClass> schoolClasses = new HashMap<PersistenceId, DomResultSchoolClass>(resultData.getSchoolClasses().size());
        Map<PersistenceId, DomResultSchoolClass> studentClasses = new HashMap<PersistenceId, DomResultSchoolClass>(resultData.getSchoolClasses().size());
        getResultTree().setChildren(schoolClasses);
        for (PersistenceId key : resultData.getSchoolClasses().keySet()) {
            DomResultSchoolClass resultValue = new DomResultSchoolClass(resultData.getSchoolClasses().get(key));
            DomResultSchoolClass classValue = new DomResultSchoolClass(resultData.getSchoolClasses().get(key));
            resultValue.setParent(getResultTree());
            classValue.setParent(getStudentTree());
            schoolClasses.put(key, resultValue);
            studentClasses.put(key, resultValue);
        }
        
        //add students to studenttree
        for (PersistenceId key : resultData.getStudentsOfClasses().keySet()) {
            DomStudentOfClass soc = resultData.getStudentsOfClasses().get(key);
            studentClasses.get(soc.getClassId()).getChildren().put(key, new DomResultStudent(resultData.getStudents().get(soc.getStudentId())));
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
     * @return the resultTree
     */
    public DomResultTeacher getResultTree() {
        return resultTree;
    }

    /**
     * @param resultTree the resultTree to set
     */
    public void setResultTree(DomResultTeacher resultTree) {
        this.resultTree = resultTree;
    }

    /**
     * @return the studentTree
     */
    public DomResultTeacher getStudentTree() {
        return studentTree;
    }

    /**
     * @param studentTree the studentTree to set
     */
    public void setStudentTree(DomResultTeacher studentTree) {
        this.studentTree = studentTree;
    }

}
