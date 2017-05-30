package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public interface CoursesOfSchoolClassManager {
	Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile);
}
