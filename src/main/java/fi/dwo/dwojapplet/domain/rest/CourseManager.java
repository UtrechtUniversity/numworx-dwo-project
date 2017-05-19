package fi.dwo.dwojapplet.domain.rest;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public interface CourseManager {

	/**
	 * get public toplevel courses from a profile.
	 * Security: if profile is limited, only members of some schools are allowed.
	 * @return ordered list of courses
	 * @throws Dwo2Exception
	 */
	List<DomCourseStudent> getCourses() throws Dwo2Exception;

	/**
	 * get a course.
	 * Security: profile can be limited. The course can be an assessment. Wrong profile, Wrong school 
	 * @param id the ID of a course
	 * @return a course
	 * @throws Dwo2Exception
	 */
	DomCourseStudent getCourse(PersistenceId id) throws Dwo2Exception;

	/**
	 * get children of a course. The course must have children.
	 * Security: profile can be limited, The course can be an assessment. Wrong profile, Wrong school
	 * @param course
	 * @return ordered children courses of a folder
	 * @throws Dwo2Exception 
	 */
	List<DomCourseStudent> getCourses(DomCourse course) throws Dwo2Exception;

}
