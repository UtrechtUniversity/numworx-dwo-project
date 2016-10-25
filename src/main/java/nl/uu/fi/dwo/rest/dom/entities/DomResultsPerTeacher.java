package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Collected sparse results of Students that are in one or more SchoolClasses of a Teacher.
 * 
 * The information in this class is inserted client-side into a simplified kd-range 
 * tree. The kd-tree has a search range of 1 and has a node type from root to 
 * leave a sequence of: DomTeacher, DomSchoolClass, DomClassCourse referred 
 * DomCourse,DomCourse, ..., DomCourse. A leave of the kd-tree is by definition 
 * a course-leave. 
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
@XmlRootElement
public class DomResultsPerTeacher {
    private static final Logger LOG = Logger.getLogger(DomResultsPerTeacher.class.getName());
    
    private DomTeacher teacher;
    private Long fetchTimeStamp;   
    private Map<PersistenceId,DomStudent> students; 
    private Map<PersistenceId,DomStudentOfClass> studentOfClass;
    private Map<PersistenceId,DomSchoolClass> schoolClasses;
    private Map<PersistenceId,DomClassCourse> classCourses;
    private Map<PersistenceId,DomCourse> courses;
    private Map<PersistenceId,DomScoContext> scoContexts;
    private Map<PersistenceId,DomStudentScoContext> studentScoContexts;    

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
     * The timestamp defining when the result collection were started 
     * on the server.
     * 
     * @return the fetchTimeStamp
     */
    public Long getFetchTimeStamp() {
        return fetchTimeStamp;
    }

    /**
     * @param fetchTimeStamp the fetchTimeStamp to set
     */
    public void setFetchTimeStamp(Long fetchTimeStamp) {
        this.fetchTimeStamp = fetchTimeStamp;
    }

    /**
     * Returns a map of all the students of the teacher. The hash is the PersistenceId
     * of a student. A student of the teacher is a student that is registered to
     * in one or more of his classes.
     * 
     * @return the students
     */
    public Map<PersistenceId,DomStudent> getStudents() {
        return students;
    }

    /**
     * @param students the students to set
     */
    public void setStudents(Map<PersistenceId,DomStudent> students) {
        this.students = students;
    }

    /**
     * Returns all the classes to which the teacher is registered as a member in 
     * his school. The hash is the PersistenceId
     * of a school class. 
     * 
     * @return the schoolClasses
     */
    public Map<PersistenceId,DomSchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    /**
     * @param schoolClasses the schoolClasses to set
     */
    public void setSchoolClasses(Map<PersistenceId,DomSchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    /**
     * Returns the scoContext data of which any course used by any school class 
     * of the teacher in his school.The hash is the PersistenceId
     * of a scoId. 
     * 
     * @return the scoContexts
     */
    public Map<PersistenceId,DomScoContext> getScoContexts() {
        return scoContexts;
    }

    /**
     * @param scoContexts the scoContexts to set
     */
    public void setScoContexts(Map<PersistenceId,DomScoContext> scoContexts) {
        this.scoContexts = scoContexts;
    }

    /**
     * Returns the studentSco data belonging to any course assigned to any school class 
     * of the teacher in his school.
     * 
     * @return the studentScoContexts
     */
    public Map<PersistenceId,DomStudentScoContext> getStudentScoContexts() {
        return studentScoContexts;
    }

    /**
     * @param studentScoContexts the studentScoContexts to set
     */
    public void setStudentScoContexts(Map<PersistenceId,DomStudentScoContext> studentScoContexts) {
        this.studentScoContexts = studentScoContexts;
    }

    /**
     * Returns the course data assigned to any school class of the teacher in 
     * his school.
     * 
     * @return the courses
     */
    public Map<PersistenceId,DomCourse> getCourses() {
        return courses;
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(Map<PersistenceId,DomCourse> courses) {
        this.courses = courses;
    }

    /**
     * Returns the ClassCourses that bind course subtrees to a school class 
     * of the teacher in his school.
     * 
     * @return the classCourses
     */
    public Map<PersistenceId,DomClassCourse> getClassCourses() {
        return classCourses;
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(Map<PersistenceId,DomClassCourse> classCourses) {
        this.classCourses = classCourses;
    }

    /**
     * Returns the StudentOfClass data that tells which student links to which 
     * school class. 
     * 
     * @return the studentOfClass
     */
    public Map<PersistenceId,DomStudentOfClass> getStudentOfClass() {
        return studentOfClass;
    }

    /**
     * @param studentOfClass the studentOfClass to set
     */
    public void setStudentOfClass(Map<PersistenceId,DomStudentOfClass> studentOfClass) {
        this.studentOfClass = studentOfClass;
    }
    
}
