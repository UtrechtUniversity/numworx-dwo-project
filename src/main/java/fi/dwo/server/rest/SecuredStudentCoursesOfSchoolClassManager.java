package fi.dwo.server.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Path("/secure/student/coursesofschoolclass")
public class SecuredStudentCoursesOfSchoolClassManager {
    private static final Logger LOG = Logger.getLogger(SecuredStudentCoursesOfSchoolClassManager.class.getName());

	@PUT
    @Produces({"application/json"})
    @Path("/get")
	public DomCoursesOfSchoolClass get(@Context SecurityContext sc, RestSchoolClassAndProfile rest) throws Dwo2Exception {
// verify user is student of class
        PersistentHasRole phr = null;
        PersistentHasRolePK phrPK = MySQLPersistenceId.getNativeId(rest.getRestContext().getDomHasRole());
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        
        //check if user has matching hasRole
        try {
        	PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
        	if (! u.getId().equals(phrPK.getUserID()))
        		throw new Dwo2Exception();
        	phr = HasRoleManager.findEntity(phrPK);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        //fetch schoolclass from parameter
		Long classID = MySQLPersistenceId.getNativeId(rest.getDomSchoolClass());
		schoolClass = SchoolClassManager.findEntity(classID);

        //verify if user is in class
        PersistentStudentOfClassPK key = new PersistentStudentOfClassPK();
        key.setClassID(schoolClass.getClassID());
        key.setSchoolGroupID(phr.getPersistentHasRolePK().getSchoolGroupID());
        key.setUserID(phr.getPersistentHasRolePK().getUserID());
        PersistentStudentOfClass soc = StudentOfClassManager.findEntity(key);
        if (soc == null) {
            return null;
        }

        //verify if schoolClass is in school
        if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} from a different school that registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
        }
		
// end verification		
		DomCoursesOfSchoolClass result = new DomCoursesOfSchoolClass();

		Long profileID = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
		
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
		result.setFetchTimeStamp(Long.valueOf(System.currentTimeMillis()));
		return result;
		
	}
	
}
