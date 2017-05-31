package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;

public class PublicUserResultsManager implements UserResultsManager {

	@Override
	public Promise<DomResultsPerStudentCourse> getCourseResults(DomContext context, DomCourse course, DomDwoProfile profile) {
		return Promises.failed(new IllegalArgumentException());
	}

}
