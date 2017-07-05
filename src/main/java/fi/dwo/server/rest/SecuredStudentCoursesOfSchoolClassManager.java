package fi.dwo.server.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Path("/secure/student/coursesofschoolclass")
public class SecuredStudentCoursesOfSchoolClassManager {

	@PUT
    @Produces({"application/json"})
    @Path("/get")
	public DomCoursesOfSchoolClass get(@Context SecurityContext sc, RestSchoolClassAndProfile rest) throws Dwo2Exception {
// FIXME verify user is student of class
		
		
		DomCoursesOfSchoolClass result = new DomCoursesOfSchoolClass();

		Long profileID = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
		Long classID = MySQLPersistenceId.getNativeId(rest.getDomSchoolClass());
		
		
		PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(classID);
		
		List<PersistentClassCourse> listClassCourse = ClassCourseManager.findEntities(schoolClass);

		Map<PersistenceId, DomClassCourse> classCourseMap = new HashMap<>();
		Map<PersistenceId, DomCourseStudent> courseMap = new HashMap<>();
		
		listClassCourse.stream().forEach(
				(scc) -> {
					Long courseID = scc.getCourseID();
					PersistentCourse course = CourseManager.findEntity(courseID);
					if (profileID .equals( course.getDwoProfileID())) {
						DomClassCourse dcc = scc.buildDomClassCourse();
						classCourseMap.put(dcc.getId(), dcc);
						DomCourseStudent dcs = course.buildDomCourseStudent();
						courseMap.put(dcs.getId(), dcs);
					}
				});
		
		result.setSchoolClass(schoolClass.buildDomSchoolClass());
		result.setClassCourses(classCourseMap.entrySet()
				.stream()
				.map((e) -> new DomMapEntry<PersistenceId, DomClassCourse>(e))
				.collect(Collectors.toList()));
		result.setCourses(courseMap.entrySet()
				.stream()
				.map((e) -> new DomMapEntry<PersistenceId, DomCourseStudent>(e))
				.collect(Collectors.toList()));
		return result;
		
	}
	
}
