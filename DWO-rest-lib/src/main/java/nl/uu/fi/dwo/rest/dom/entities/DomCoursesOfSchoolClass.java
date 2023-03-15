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
public class DomCoursesOfSchoolClass extends DomClassCoursesOfSchoolClass {

    private List<DomMapEntry<PersistenceId, DomCourseStudent>> courses;
    private List<DomMapEntry<PersistenceId, DomScoContext>> scoContexts;
    
    /**
     * @return the courses
     */
    public List<DomMapEntry<PersistenceId, DomCourseStudent>> getCourses() {
        return courses;
    }

    /**
     * @param courses the courses to set
     */
    public void setCourses(List<DomMapEntry<PersistenceId, DomCourseStudent>> courses) {
        this.courses = courses;
    }

	/**
	 * @return the scoContexts
	 */
	public List<DomMapEntry<PersistenceId, DomScoContext>> getScoContexts() {
		return scoContexts;
	}

	/**
	 * @param scoContexts the scoContexts to set
	 */
	public void setScoContexts(List<DomMapEntry<PersistenceId, DomScoContext>> scoContexts) {
		this.scoContexts = scoContexts;
	}

    
}
