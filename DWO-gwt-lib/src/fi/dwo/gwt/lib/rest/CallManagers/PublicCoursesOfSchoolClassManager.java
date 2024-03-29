package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

public class PublicCoursesOfSchoolClassManager implements CoursesOfSchoolClassManager {

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCourseClass(DomContext context, DomSchoolClass schoolClass,
			DomCourse course, DomDwoProfile profile) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getScoContextClass(DomContext context, DomSchoolClass schoolClass,
			DomScoContext course, DomDwoProfile profile) {
		return Promises.failed(new IllegalArgumentException());
	}

    @Override
    public Promise<DomCoursesOfSchoolClass> getClassCourse(DomContext context,
        DomClassCourse classcourse, DomDwoProfile profile) {
      return Promises.failed(new IllegalArgumentException());
    }

	@Override
	public Promise<String> getClassCourseURL(DomContext context, DomClassCourse classcourse, String base) {
		return Promises.failed(new IllegalArgumentException());
	}

}
