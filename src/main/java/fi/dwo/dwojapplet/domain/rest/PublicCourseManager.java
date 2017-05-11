package fi.dwo.dwojapplet.domain.rest;

import java.util.Collections;
import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
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
	 */
	@Override
	public List<DomCourseStudent> getCourses() {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		if(profile.getDwoProfileRights().contains("l")) return Collections.EMPTY_LIST;

		// select * from tblCourse where parent = NULL, profile = %profile, school = NULL
		
		
		return null;
		
	}
	/** 
	 * Return a public course. School=nul,profile is ok.
	 * @param id
	 * @return a public course
	 */
	@Override
	public DomCourseStudent getCourse(PersistenceId id) {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		
		// select * from tblCourse where id = $%id, profile = %profile and school = NULL
		return null;
	}
	
	/**
	 * 
	 * @param course
	 * @return
	 */
	@Override
	public List<DomCourseStudent> getCourses(DomCourse course) {
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		if(profile.getDwoProfileRights().contains("l")) return Collections.EMPTY_LIST;
		PersistenceId id = course.getId();
		// select * from tblCourse where parent = %id, profile = %profile, school = null;
		return null;		
	}
	
}
