package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.logging.Logger;
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
public class DomCoursesOfSchoolClass4Teacher extends DomClassCoursesOfSchoolClass {
    private static Logger LOG = Logger.getLogger(DomCoursesOfSchoolClass4Teacher.class.getName());

    private List<DomMapEntry<PersistenceId, DomCourse>> courses;

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
