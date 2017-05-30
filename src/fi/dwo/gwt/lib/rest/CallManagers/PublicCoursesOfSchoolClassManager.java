package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public class PublicCoursesOfSchoolClassManager implements CoursesOfSchoolClassManager {

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile) {
		return Promises.failed(new IllegalArgumentException());
	}

}
