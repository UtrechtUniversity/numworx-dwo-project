package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    private Map<PersistenceId, DomStudent> students;
    private Map<PersistenceId, DomStudentOfClass> studentsOfClasses;
    private Map<PersistenceId, DomSchoolClass> schoolClasses;
    private Map<PersistenceId, DomClassCourse> classCourses;
    private Map<PersistenceId, DomCourse> courses;
    private Map<PersistenceId, DomScoContext> scoContexts;
    private Map<PersistenceId, DomStudentScoContext> studentScoContexts;

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
    public Set<Map.Entry<PersistenceId, DomStudent>> getStudents() {
        return students.entrySet();
    }

    /**
     * @param students the students to set
     */
    public void setStudents(Set<Map.Entry<PersistenceId, DomStudent>> students) {
        HashMap<PersistenceId, DomStudent> map = new HashMap<PersistenceId, DomStudent>(students.size());
        for (Map.Entry<PersistenceId, DomStudent> student : students) {
            map.put(student.getKey(), student.getValue());
        }
        this.students = map;
    }

    /**
     * Returns all the classes to which the teacher is registered as a member in
     * his school. The hash is the PersistenceId of a school class.
     *
     * @return the schoolClasses
     */
    public Set<Map.Entry<PersistenceId, DomSchoolClass>> getSchoolClasses() {
        return schoolClasses.entrySet();
    }

    /**
     * @param schoolClasses the schoolClasses to set
     */
    public void setSchoolClasses(Set<Map.Entry<PersistenceId, DomSchoolClass>> schoolClasses) {
        HashMap<PersistenceId, DomSchoolClass> map = new HashMap<PersistenceId, DomSchoolClass>(schoolClasses.size());
        for (Map.Entry<PersistenceId, DomSchoolClass> schoolClass : schoolClasses) {
            map.put(schoolClass.getKey(), schoolClass.getValue());
        }
        this.schoolClasses = map;
    }

    /**
     * Returns the scoContext data of which any course used by any school class
     * of the teacher in his school.The hash is the PersistenceId of a scoId.
     *
     * @return the scoContexts
     */
    public Set<Map.Entry<PersistenceId, DomScoContext>> getScoContexts() {
        return this.scoContexts.entrySet();
    }

    /**
     * @param scoContexts the scoContexts to set
     */
    public void setScoContexts(Set<Map.Entry<PersistenceId, DomScoContext>> scoContexts) {
        HashMap<PersistenceId, DomScoContext> map = new HashMap<PersistenceId, DomScoContext>(scoContexts.size());
        for (Map.Entry<PersistenceId, DomScoContext> scoContext : scoContexts) {
            map.put(scoContext.getKey(), scoContext.getValue());
        }
        this.scoContexts = map;
    }

    /**
     * Returns the studentSco data belonging to any course assigned to any
     * school class of the teacher in his school.
     *
     * @return the studentScoContexts
     */
    public Set<Map.Entry<PersistenceId, DomStudentScoContext>> getStudentScoContexts() {
        return studentScoContexts.entrySet();
    }

    /**
     * @param studentScoContexts the studentScoContexts to set
     */
    public void setStudentScoContexts(Set<Map.Entry<PersistenceId, DomStudentScoContext>> studentScoContexts) {
        HashMap<PersistenceId, DomStudentScoContext> map = new HashMap<PersistenceId, DomStudentScoContext>(studentScoContexts.size());
        for (Map.Entry<PersistenceId, DomStudentScoContext> studentScoContext : studentScoContexts) {
            map.put(studentScoContext.getKey(), studentScoContext.getValue());
        }
        this.studentScoContexts = map;
    }

    /**
     * Returns the course data assigned to any school class of the teacher in
     * his school.
     *
     * @return the courses
     */
    public Set<Map.Entry<PersistenceId, DomCourse>> getCourses() {
        return courses.entrySet();
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(Set<Map.Entry<PersistenceId, DomCourse>> courses) {
        HashMap<PersistenceId, DomCourse> map = new HashMap<PersistenceId, DomCourse>(courses.size());
        for (Map.Entry<PersistenceId, DomCourse> course : courses) {
            map.put(course.getKey(), course.getValue());
        }
        this.courses = map;
    }

    /**
     * Returns the ClassCourses that bind course subtrees to a school class of
     * the teacher in his school.
     *
     * @return the classCourses
     */
    public Set<Map.Entry<PersistenceId, DomClassCourse>> getClassCourses() {
        return classCourses.entrySet();
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(Set<Map.Entry<PersistenceId, DomClassCourse>> classCourses) {
        HashMap<PersistenceId, DomClassCourse> map = new HashMap<PersistenceId, DomClassCourse>(classCourses.size());
        for (Map.Entry<PersistenceId, DomClassCourse> cc : classCourses) {
            map.put(cc.getKey(), cc.getValue());
        }
        this.classCourses = map;
    }

    /**
     * Returns the StudentOfClass data that tells which student links to which
     * school class.
     *
     * @return the studentsOfClasses
     */
    public Set<Map.Entry<PersistenceId, DomStudentOfClass>> getStudentsOfClasses() {
        return studentsOfClasses.entrySet();
    }

    /**
     * @param studentsOfClasses the studentsOfClasses to set
     */
    public void setStudentsOfClasses(Set<Map.Entry<PersistenceId, DomStudentOfClass>> studentsOfClasses) {
        HashMap<PersistenceId, DomStudentOfClass> map = new HashMap<PersistenceId, DomStudentOfClass>(studentsOfClasses.size());
        for (Map.Entry<PersistenceId, DomStudentOfClass> soc : studentsOfClasses) {
            map.put(soc.getKey(), soc.getValue());
        }
        this.studentsOfClasses = map;
    }

}
