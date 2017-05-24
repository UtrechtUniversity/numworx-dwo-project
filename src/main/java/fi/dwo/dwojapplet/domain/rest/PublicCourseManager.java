package fi.dwo.dwojapplet.domain.rest;

import java.util.Collections;
import java.util.List;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicCourseManager {


	/**
	 * get public toplevel courses from a profile.
	 * Security: if profile is limited, only members of some schools are allowed.
	 * @param profile
	 * @return ordered list of courses
	 * @throws Dwo2Exception
	 */
	public static List<DomCourseStudent> getCourses(DomDwoProfile profile) throws Dwo2Exception {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		if(profile.getDwoProfileRights().contains("l")) return Collections.emptyList();

		// select * from tblCourse where parent = NULL, profile = %profile, school = NULL
		RestDwoProfile rest = new RestDwoProfile();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(new DomContext());
		List<DomCourseStudent> result = StoredRestManager.getInstance().getPutList("/public/course/getRoot", RestListClassTypes.DomCourseStudent, rest);
		
		return result;
		
	}
	/**
	 * get a course.
	 * Security: profile can be limited. The course can be an assessment. Wrong profile, Wrong school 
	 * @param course
	 * @param profile
	 * @return a course
	 * @throws Dwo2Exception
	 */
	public static DomCourseStudent getCourse(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(new DomContext());
		rest.setDomCourse(course);
		// select * from tblCourse where id = $%id, profile = %profile and school = NULL
		DomCourseStudent result = StoredRestManager.getInstance().put("/public/course/get", DomCourseStudent.class, rest);
		return result;
	}
	
	/**
	 * get children of a course. The course must have children.
	 * Security: profile can be limited, The course can be an assessment. Wrong profile, Wrong school
	 * @param course
	 * @param profile
	 * @return ordered children courses of a folder
	 * @throws Dwo2Exception 
	 */
	public static List<DomCourseStudent> getCourses(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		if(profile.getDwoProfileRights().contains("l")) return Collections.EMPTY_LIST;
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setDomCourse(course);
		rest.setRestContext(new DomContext());
		List<DomCourseStudent> result = StoredRestManager.getInstance().getPutList("/public/course/getChildren", RestListClassTypes.DomCourseStudent, rest);
		
		return result;
	}
	
}
