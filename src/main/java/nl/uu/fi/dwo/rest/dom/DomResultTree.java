package nl.uu.fi.dwo.rest.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * The information in the DomResultsPerTeacher class is inserted client-side
 * into this simplified kd-range tree. The kd-tree has a search range of 1 and
 * has a node type from resultTree to leave a sequence of: DomTeacher,
 * DomSchoolClass, DomClassCourse referred DomCourse,DomCourse, ..., DomCourse.
 * A leave of the kd-tree is by definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultTree {

    private static final Logger LOG = Logger.getLogger(DomResultTree.class.getName());

    private DomResultTeacher resultTree;
    private DomResultTeacher studentTree;

    public DomResultTree(DomResultsPerTeacher resultData) {

        //restData = resultData;
        LOG.log(Level.INFO, "Initializing a DomResultTree.");
        buildResultTree(new DomMappedResultsPerTeacher(resultData));
        //reCalculateResults();
    }

    /**
     * Takes the data of a DomResultsPerTeacher object and builds two result
     * trees. The result trees can be crawled and data can be collected for
     * result viewing of studentsco data. The result trees are a mixed object
     * type tree where every tree path has the following object type sequence:
     *
     * resultTree:DomResultTeacher, DomResultSchoolClass, one or more DomCourse,
     * DomSco, DomStudentSco.
     *
     * or
     *
     * studentTree: DomResultTeacher, DomResultSchoolClass, DomStudent
     *
     * @param resultData
     */
    private void buildResultTree(DomMappedResultsPerTeacher resultData) {
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
        getStudentTree().setChildren(studentClasses);
        //create schoolClass maps for studentTree and resultTree
        for (PersistenceId key : resultData.getSchoolClasses().keySet()) {
            DomResultSchoolClass resultValue = new DomResultSchoolClass(resultData.getSchoolClasses().get(key));
            DomResultSchoolClass classValue = new DomResultSchoolClass(resultData.getSchoolClasses().get(key));
            //set child to parent
            resultValue.setParent(getResultTree());
            classValue.setParent(getStudentTree());
            //add to parent
            schoolClasses.put(key, resultValue);
            studentClasses.put(key, classValue);
        }

        //add students to studentClasses
        for (PersistenceId key : resultData.getStudentsOfClasses().keySet()) {
            //for each student in a class
            DomStudentOfClass soc = resultData.getStudentsOfClasses().get(key);
            PersistenceId sId = soc.getStudentId();
            if (studentClasses.containsKey(soc.getClassId()) && !studentClasses.get(soc.getClassId()).getChildren().containsKey(sId)) {
                //if class exists and student does not yet exist, add student to that class.
                //fetch student
                DomStudent student = resultData.getStudents().get(sId);
                //add student to parent.
                studentClasses.get(soc.getClassId()).getChildren().put(sId, student);
            }
        }

        //Built an index for DomCourses with the same parentID. Memory efficient.
        Map<PersistenceId, List<DomScoContext>> scoParentIndex = new HashMap<PersistenceId, List<DomScoContext>>();
        for (DomScoContext sco : resultData.getScoContexts().values()) {
            if (resultData.getCourses().containsKey(sco.getCourseId())) { //ifit is an existing course
                if (!scoParentIndex.containsKey(sco.getCourseId())) {
                    scoParentIndex.put(sco.getCourseId(), new ArrayList<DomScoContext>());
                }
                scoParentIndex.get(sco.getCourseId()).add(sco);
            }
        }

        //Built an index for DomStudentScoContext's with the same parentID. Memory efficient.
        Map<PersistenceId, List<DomStudentScoContext>> ssParentIndex = new HashMap<PersistenceId, List<DomStudentScoContext>>();
        for (DomStudentScoContext ss : resultData.getStudentScoContexts().values()) {
            if (resultData.getScoContexts().containsKey(ss.getScoID())) { //ifit is an existing course
                if (!ssParentIndex.containsKey(ss.getScoID())) {
                    ssParentIndex.put(ss.getScoID(), new ArrayList<DomStudentScoContext>());
                }
                ssParentIndex.get(ss.getScoID()).add(ss);
            }
        }

        //scan the classcourses and add a root result course instance to every class
        //assume flat trees, therefore children are sco's.
        for (PersistenceId key : resultData.getClassCourses().keySet()) {
            //build the subtrees
            DomClassCourse cc = resultData.getClassCourses().get(key);
            DomResultCourse resultCourse = new DomResultCourse(resultData.getCourses().get(cc.getCourseId()));
            //attach to class
            schoolClasses.get(cc.getClassId()).getChildren().put(cc.getCourseId(), resultCourse); //add course to parent
            resultCourse.setParent(schoolClasses.get(cc.getClassId())); //add parent to course
            //attach sco-type children to it
            for (DomScoContext sco : scoParentIndex.get(resultCourse.getCourse().getId())) {
                DomResultScoContext resultSco = new DomResultScoContext(sco);
                resultCourse.getChildren().put(sco.getId(), resultSco);//add sco to parent
                resultSco.setParent(resultCourse);//set parent in sco
                //find studentsco's to sco if present in the same school class
                DomResultScore ancestor = resultSco;
                do {
                    ancestor = ancestor.getParent();
                } while (!(ancestor instanceof DomResultSchoolClass));

                DomResultSchoolClass curSchoolClass = (DomResultSchoolClass) ancestor;

                //add studentSco to Sco in subtree                
                if (ssParentIndex.containsKey(sco.getId())) {
                    for (DomStudentScoContext ss : ssParentIndex.get(sco.getId())) {
                        DomStudent student = resultData.getStudents().get(ss.getUserID());
                        if (student != null && studentClasses.containsKey(curSchoolClass.getSchoolClass().getId())
                                && studentClasses.get(curSchoolClass.getSchoolClass().getId()).getChildren().containsKey(student.getId())) {
                            resultSco.getChildren().put(ss.getId(), new DomResultStudentSco(ss, student));
                        }
                    }
                }
            }
        }
        for(DomResultSchoolClass sc : schoolClasses.values()){
            sc.calculateSumOfSubtreeScore(studentTree.getChildren().get(sc.getSchoolClass().getId()).getChildren().size());
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
