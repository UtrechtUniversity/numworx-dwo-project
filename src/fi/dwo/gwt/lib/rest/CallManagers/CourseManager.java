package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;

import org.osgi.util.promise.Promise;

import com.google.gwt.json.client.JSONValue;

public interface CourseManager {
	
	Promise<List<DomCourse>> getAllCourses(DomDwoProfile profile, DomContext context);

	public Promise<List<DomCourseStudent>> getCourses(DomDwoProfile profile, DomContext context);

	public Promise<List<DomCourseStudent>> getCourses(DomCourse course,
			DomDwoProfile profile, DomContext context);

	public Promise<DomCourseStudent> getCourse(DomCourse course,
			DomDwoProfile profile, DomSchoolClassId schoolClass, DomContext context);

	public Promise<List<DomCourseStudent>> getCoursesSchool(DomDwoProfile profile, DomContext context);

	public Promise<JSONValue> getCourseDescription(DomCourse id,
			DomDwoProfile profile, DomContext context);

	Promise<JSONValue> getCourseDescription(DomCourse course, DomDwoProfile profile, DomContext context,
			DomSchoolClassId clsid);

}