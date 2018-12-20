package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * ClassCourse and Courses in the subtree of the ClassCourse Courses for a 
 * SchoolClass. This is a different use-case than the {@Link DomResultsPerTeacher}.
 * 
 * 
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
@XmlRootElement
public class DomCoursesOfSchoolClass4Teacher{
    private List<DomMapEntry<PersistenceId, DomCourse>> courses;

    private Long fetchTimeStamp;   
    private DomSchoolClass schoolClass;
    private List<DomMapEntry<PersistenceId,DomClassCourse4Teacher>> classCourses;

    /**
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
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    /**
     * @return the classCourses
     */
    public List<DomMapEntry<PersistenceId, DomClassCourse4Teacher>> getClassCourses() {
        return classCourses;
    }

    /**
     * @param classCourses the classCourses to set
     */
    public void setClassCourses(List<DomMapEntry<PersistenceId, DomClassCourse4Teacher>> classCourses) {
        this.classCourses = classCourses;
    }
    /**
     * @return the courses
     */
    public List<DomMapEntry<PersistenceId, DomCourse>> getCourses() {
        return courses;
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(List<DomMapEntry<PersistenceId, DomCourse>> courses) {
        this.courses = courses;
    }

    
}
