package nl.uu.fi.dwo.rest.dom;

import java.util.Collections;
import nl.uu.fi.dwo.rest.dom.entities.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Collected sparse results of Students that are in one or more SchoolClasses of
 * a Teacher.
 *
 * The information in this class is inserted client-side into a simplified
 * kd-range tree. The kd-tree has a search range of 1 and has a node type from
 * root to leave a sequence of: DomTeacher, DomSchoolClass, DomClassCourse
 * referred DomCourse,DomCourse, ..., DomCourse. A leave of the kd-tree is by
 * definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
@XmlRootElement
public class DomMappedResultsPerTeacher {

    private static final Logger LOG = Logger.getLogger(DomMappedResultsPerTeacher.class.getName());

    private DomTeacher teacher;
    private Long fetchTimeStamp;
    private Map<PersistenceId, DomStudent> students;
    private Map<PersistenceId, DomStudentOfClass> studentsOfClasses;
    private Map<PersistenceId, DomSchoolClass> schoolClasses;
    private Map<PersistenceId, DomClassCourse> classCourses;
    private Map<PersistenceId, DomCourse> courses;
    private Map<PersistenceId, DomScoContext> scoContexts;
    private Map<PersistenceId, DomStudentScoContext> studentScoContexts;

    DomMappedResultsPerTeacher(DomTeacher aTeacher) {
        teacher = aTeacher;
    }

    public DomMappedResultsPerTeacher(DomResultsPerTeacher results) {
        if (results == null) {
            return;
        }
        LOG.log(Level.FINE,"DomResultsPerTeacher.getStudents "+results.getStudents().size());
        LOG.log(Level.FINE,"DomResultsPerTeacher.getClassCourses "+results.getClassCourses().size());
        LOG.log(Level.FINE,"DomResultsPerTeacher.getCourses "+results.getCourses().size());
        LOG.log(Level.FINE,"DomResultsPerTeacher.getSchoolClasses "+results.getSchoolClasses().size());
        LOG.log(Level.FINE,"DomResultsPerTeacher.getStudentScoContexts "+results.getStudentScoContexts().size());
        LOG.log(Level.FINE,"DomResultsPerTeacher.getStudentsOfClasses "+results.getStudentsOfClasses().size());
        teacher = results.getTeacher();
        fetchTimeStamp = results.getFetchTimeStamp();
        //fill and ensure no null values;
        if (results.getStudents() != null) {
            students = new HashMap<PersistenceId, DomStudent>(results.getStudents().size());
            for (DomMapEntry<PersistenceId, DomStudent> student : results.getStudents()) {
                students.put(student.getKey(), student.getValue());
            }
        }else{
            students = new HashMap<PersistenceId, DomStudent>();
        }
        if (results.getStudentsOfClasses() != null && !results.getStudentsOfClasses().equals(Collections.EMPTY_MAP)) {
            studentsOfClasses = new HashMap<PersistenceId, DomStudentOfClass>(results.getStudentsOfClasses().size());
            for (DomMapEntry<PersistenceId, DomStudentOfClass> soc : results.getStudentsOfClasses()) {
                studentsOfClasses.put(soc.getKey(), soc.getValue());
            }
        }else{
            studentsOfClasses = new HashMap<PersistenceId, DomStudentOfClass>();
        }

        if (results.getSchoolClasses() != null) {
            schoolClasses = new HashMap<PersistenceId, DomSchoolClass>(results.getSchoolClasses().size());
            for (DomMapEntry<PersistenceId, DomSchoolClass> schoolClass : results.getSchoolClasses()) {
                schoolClasses.put(schoolClass.getKey(), schoolClass.getValue());
            }
        }else{
            schoolClasses = new HashMap<PersistenceId, DomSchoolClass>();
        }
        if (results.getClassCourses() != null) {
            classCourses = new HashMap<PersistenceId, DomClassCourse>(results.getClassCourses().size());
            for (DomMapEntry<PersistenceId, DomClassCourse> cc : results.getClassCourses()) {
                classCourses.put(cc.getKey(), cc.getValue());
            }
        }else{
            classCourses = new HashMap<PersistenceId, DomClassCourse>();
        }
        if (results.getCourses() != null) {
            courses = new HashMap<PersistenceId, DomCourse>(results.getCourses().size());
            for (DomMapEntry<PersistenceId, DomCourse> course : results.getCourses()) {
                courses.put(course.getKey(), course.getValue());
            }
        }
        if(results.getScoContexts()!=null){
        scoContexts = new HashMap<PersistenceId, DomScoContext>(results.getScoContexts().size());
        for (DomMapEntry<PersistenceId, DomScoContext> scoContext : results.getScoContexts()) {
            scoContexts.put(scoContext.getKey(), scoContext.getValue());
        }
        }else{
            scoContexts = new HashMap<PersistenceId, DomScoContext>();
        }
        if(results.getStudentScoContexts()!=null){
        studentScoContexts = new HashMap<PersistenceId, DomStudentScoContext>(results.getStudentScoContexts().size());
        for (DomMapEntry<PersistenceId, DomStudentScoContext> ssc : results.getStudentScoContexts()) {
            studentScoContexts.put(ssc.getKey(), ssc.getValue());
        }
        }else{
            studentScoContexts = new HashMap<PersistenceId, DomStudentScoContext>();
        }
    }

    /**
     * @return the teacher
     */
    public DomTeacher getTeacher() {
        return teacher;
    }

    /**
     * Teacher whose class results are collected.
     *
     * @param teacher the teacher to set
     */
    public void setTeacher(DomTeacher teacher) {
        this.teacher = teacher;
    }

    /**
     * The UT timestamp defining when the result collection was started on the
     * server.
     *
     * @return the fetchTimeStamp
     */
    public Long getFetchTimeStamp() {
        return fetchTimeStamp;
    }

    /**
     * Setting UT timestamp which defines when the result collection was started
     * on the server.
     *
     * @param fetchTimeStamp the fetchTimeStamp to set
     */
    public void setFetchTimeStamp(Long fetchTimeStamp) {
        this.fetchTimeStamp = fetchTimeStamp;
    }

    /**
     * Returns a map of all the students of the teacher. The hash is the
     * PersistenceId of a student. A student of the teacher is a student that is
     * registered to in one or more of his classes.
     *
     * @return the students
     */
    public Map<PersistenceId, DomStudent> getStudents() {
        return students;
    }

    /**
     * @param students the students to set
     */
    public void setStudents(Map<PersistenceId, DomStudent> students) {
        this.students = students;
    }

    /**
     * Returns all the classes to which the teacher is registered as a member in
     * his school. The hash is the PersistenceId of a school class.
     *
     * @return the schoolClasses
     */
    public Map<PersistenceId, DomSchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    /**
     * @param schoolClasses the schoolClasses to set
     */
    public void setSchoolClasses(Map<PersistenceId, DomSchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    /**
     * Returns the scoContext data of which any course used by any school class
     * of the teacher in his school.The hash is the PersistenceId of a scoId.
     *
     * @return the scoContexts
     */
    public Map<PersistenceId, DomScoContext> getScoContexts() {
        return scoContexts;
    }

    /**
     * @param scoContexts the scoContexts to set
     */
    public void setScoContexts(Map<PersistenceId, DomScoContext> scoContexts) {
        this.scoContexts = scoContexts;
    }

    /**
     * Returns the studentSco data belonging to any course assigned to any
     * school class of the teacher in his school.
     *
     * @return the studentScoContexts
     */
    public Map<PersistenceId, DomStudentScoContext> getStudentScoContexts() {
        return studentScoContexts;
    }

    /**
     * @param studentScoContexts the studentScoContexts to set
     */
    public void setStudentScoContexts(Map<PersistenceId, DomStudentScoContext> studentScoContexts) {
        this.studentScoContexts = studentScoContexts;
    }

    /**
     * Returns the course data assigned to any school class of the teacher in
     * his school.
     *
     * @return the courses
     */
    public Map<PersistenceId, DomCourse> getCourses() {
        return courses;
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(Map<PersistenceId, DomCourse> courses) {
        this.courses = courses;
    }

    /**
     * Returns the ClassCourses that bind course subtrees to a school class of
     * the teacher in his school.
     *
     * @return the classCourses
     */
    public Map<PersistenceId, DomClassCourse> getClassCourses() {
        return classCourses;
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(Map<PersistenceId, DomClassCourse> classCourses) {
        this.classCourses = classCourses;
    }

    /**
     * Returns the StudentOfClass data that tells which student links to which
     * school class.
     *
     * @return the studentsOfClasses
     */
    public Map<PersistenceId, DomStudentOfClass> getStudentsOfClasses() {
        return studentsOfClasses;
    }

    /**
     * @param studentsOfClasses the studentsOfClasses to set
     */
    public void setStudentsOfClasses(Map<PersistenceId, DomStudentOfClass> studentsOfClasses) {
        this.studentsOfClasses = studentsOfClasses;
    }

}
