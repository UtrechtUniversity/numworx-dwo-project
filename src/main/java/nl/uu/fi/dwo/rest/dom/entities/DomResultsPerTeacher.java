package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.Map;
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
public class DomResultsPerTeacher {

    private static final Logger LOG = Logger.getLogger(DomResultsPerTeacher.class.getName());

    private DomTeacher teacher;
    private Long fetchTimeStamp;
    private List<Map.Entry<PersistenceId, DomStudent>> students;
    private List<Map.Entry<PersistenceId, DomStudentOfClass>> studentsOfClasses;
    private List<Map.Entry<PersistenceId, DomSchoolClass>> schoolClasses;
    private List<Map.Entry<PersistenceId, DomClassCourse>> classCourses;
    private List<Map.Entry<PersistenceId, DomCourse>> courses;
    private List<Map.Entry<PersistenceId, DomScoContext>> scoContexts;
    private List<Map.Entry<PersistenceId, DomStudentScoContext>> studentScoContexts;

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
    public List<Map.Entry<PersistenceId, DomStudent>> getStudents() {
        return students;
    }

    /**
     * @param aStudents the students to set
     */
    public void setStudents(List<Map.Entry<PersistenceId, DomStudent>> aStudents) {
        this.students = aStudents;
    }

    /**
     * Returns all the classes to which the teacher is registered as a member in
     * his school. The hash is the PersistenceId of a school class.
     *
     * @return the schoolClasses
     */
    public List<Map.Entry<PersistenceId, DomSchoolClass>> getSchoolClasses() {
        return schoolClasses;
    }

    /**
     * @param aSchoolClasses the schoolClasses to set
     */
    public void setSchoolClasses(List<Map.Entry<PersistenceId, DomSchoolClass>> aSchoolClasses) {
        this.schoolClasses = aSchoolClasses;
    }

    /**
     * Returns the scoContext data of which any course used by any school class
     * of the teacher in his school.The hash is the PersistenceId of a scoId.
     *
     * @return the scoContexts
     */
    public List<Map.Entry<PersistenceId, DomScoContext>> getScoContexts() {
        return this.scoContexts;
    }

    /**
     * @param aScoContexts the scoContexts to set
     */
    public void setScoContexts(List<Map.Entry<PersistenceId, DomScoContext>> aScoContexts) {
        this.scoContexts = aScoContexts;
    }

    /**
     * Returns the studentSco data belonging to any course assigned to any
     * school class of the teacher in his school.
     *
     * @return the studentScoContexts
     */
    public List<Map.Entry<PersistenceId, DomStudentScoContext>> getStudentScoContexts() {
        return studentScoContexts;
    }

    /**
     * @param studentScoContexts the studentScoContexts to set
     */
    public void setStudentScoContexts(List<Map.Entry<PersistenceId, DomStudentScoContext>> aStudentScoContexts) {
        this.studentScoContexts = aStudentScoContexts;
    }

    /**
     * Returns the course data assigned to any school class of the teacher in
     * his school.
     *
     * @return the courses
     */
    public List<Map.Entry<PersistenceId, DomCourse>> getCourses() {
        return courses;
    }

    /**
     * @param aCourses the courses to set
     */
    public void setCourses(List<Map.Entry<PersistenceId, DomCourse>> aCourses) {
        this.courses = aCourses;
    }

    /**
     * Returns the ClassCourses that bind course subtrees to a school class of
     * the teacher in his school.
     *
     * @return the classCourses
     */
    public List<Map.Entry<PersistenceId, DomClassCourse>> getClassCourses() {
        return classCourses;
    }

    /**
     * @param aClassCourses the classCourses to set
     */
    public void setClassCourses(List<Map.Entry<PersistenceId, DomClassCourse>> aClassCourses) {
        this.classCourses = aClassCourses;
    }

    /**
     * Returns the StudentOfClass data that tells which student links to which
     * school class.
     *
     * @return the studentsOfClasses
     */
    public List<Map.Entry<PersistenceId, DomStudentOfClass>> getStudentsOfClasses() {
        return studentsOfClasses;
    }

    /**
     * @param aStudentsOfClasses the studentsOfClasses to set
     */
    public void setStudentsOfClasses(List<Map.Entry<PersistenceId, DomStudentOfClass>> aStudentsOfClasses) {
        this.studentsOfClasses = aStudentsOfClasses;
    }

}
