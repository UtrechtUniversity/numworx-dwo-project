package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;

public interface UserResultsManager {
	Promise<DomResultsPerStudentCourse> getCourseResults(DomContext context, DomCourse course, DomDwoProfile profile);
}
