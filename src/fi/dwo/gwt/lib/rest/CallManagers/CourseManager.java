package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;

import org.osgi.util.promise.Promise;

public interface CourseManager {

	public Promise<List<DomCourseStudent>> getCourses(DomDwoProfile profile);

	public Promise<List<DomCourseStudent>> getCourses(DomCourse course,
			DomDwoProfile profile);

	public Promise<DomCourseStudent> getCourse(DomCourse course,
			DomDwoProfile profile);

}