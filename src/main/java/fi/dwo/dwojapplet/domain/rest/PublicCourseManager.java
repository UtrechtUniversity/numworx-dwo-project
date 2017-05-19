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
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class PublicCourseManager implements CourseManager {

	private DomDwoProfile profile;
	
	public PublicCourseManager(DomDwoProfile profile) {
		super();
		this.profile = profile;
	}

	/**
	 * Return toplevel public courses of profile.
	 * @return domCourseStudents on top level (parent=nul,school=nul)
	 * @throws Dwo2Exception 
	 */
	@Override
	public List<DomCourseStudent> getCourses() throws Dwo2Exception {
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
	 * Return a public course. School=nul,profile is ok.
	 * @param id
	 * @return a public course
	 * @throws Dwo2Exception 
	 */
	@Override
	public DomCourseStudent getCourse(PersistenceId id) throws Dwo2Exception {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		DomCourse course = new DomCourse();
		course.setId(id);
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(new DomContext());
		rest.setDomCourse(course);
		
		DomCourseStudent result = StoredRestManager.getInstance().put("/public/course/get", DomCourseStudent.class, rest);

		// select * from tblCourse where id = $%id, profile = %profile and school = NULL
		return result;
	}
	
	/**
	 * 
	 * @param course
	 * @return
	 * @throws Dwo2Exception 
	 */
	@Override
	public List<DomCourseStudent> getCourses(DomCourse course) throws Dwo2Exception {
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
