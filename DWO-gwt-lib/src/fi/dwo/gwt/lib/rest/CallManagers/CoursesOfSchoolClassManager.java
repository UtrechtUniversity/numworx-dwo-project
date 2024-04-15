package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

public interface CoursesOfSchoolClassManager {
	Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile);
	Promise<DomCoursesOfSchoolClass> getCourseClass(DomContext context, DomSchoolClass schoolClass, DomCourse course, DomDwoProfile profile);
	Promise<DomCoursesOfSchoolClass> getScoContextClass(DomContext context, DomSchoolClass schoolClass, DomScoContext course, DomDwoProfile profile);
    Promise<DomCoursesOfSchoolClass> getClassCourse(DomContext context, DomClassCourse classcourse, DomDwoProfile profile);
    Promise<String> getClassCourseURL(DomContext context, DomClassCourse classcourse, String base);    
}
