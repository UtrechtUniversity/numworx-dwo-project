/**
 * Copyrighted Aug 17, 2017
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 *
 * @author Gert van der Plas
 */
public class ClassCourseSecurityBuilder {

    private static final Logger LOG = Logger.getLogger(ClassCourseSecurityBuilder.class.getName());
    
    /**
     * Throws a Dwo2RestException in case there is no access for the method signature parameters.
     * 
     * @param sc
     * @param domHasRole
     * @param domProfile
     * @param domCourse
     * @param domSchoolClass
     * @throws Dwo2Exception 
     */
    public static ClassCourseRWAccessData HasRWAccessClassCourse(SecurityContext sc, DomHasRole domHasRole, DomDwoProfile domProfile, DomCourse domCourse, DomSchoolClass domSchoolClass) throws Dwo2Exception{
        //init return data        
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        PersistentCourse course = null;
        PersistentTeacherOfClass toc=null;       
        PersistentDwoProfile profile=null;

        //check if user has matching hasRole
        PersistentHasRolePK phrPK = MySQLPersistenceId.getNativeId(domHasRole);
        try {
            PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
            if (!u.getId().equals(phrPK.getUserID())) {
                throw new Dwo2Exception();
            }
            phr = HasRoleManager.findEntity(phrPK);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
            if (profile == null) {
                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        } catch (Exception e) {
            //in case use disappeared and such
            LOG.log(Level.WARNING, "Username {0}: Internal error.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Internal error.");
        }

        //fetch schoolclass from parameter
        Long classID = MySQLPersistenceId.getNativeId(domSchoolClass);
        schoolClass = SchoolClassManager.findEntity(classID);
        if (schoolClass == null) {
            String msg = MessageFormat.format("Username {0}: Given schoolclass with id {1} can not be found.", new Object[]{sc.getUserPrincipal().getName(), classID});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_SchoolclassDoesNotExist, msg);
        }
        //verify if user is in class
        PersistentTeacherOfClassPK tocPK = new PersistentTeacherOfClassPK();
        tocPK.setClassID(schoolClass.getClassID());
        tocPK.setSchoolGroupID(phr.getPersistentHasRolePK().getSchoolGroupID());
        tocPK.setUserID(phr.getPersistentHasRolePK().getUserID());
        toc = TeacherOfClassManager.findEntity(tocPK);
        if (toc == null) {
            String msg = MessageFormat.format("Username {0} is not a teacher of schoolclass {1}.", new Object[]{sc.getUserPrincipal().getName(), classID});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //verify if schoolClass is in school
        if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        Long courseId = MySQLPersistenceId.getNativeId(domCourse);
        course = CourseManager.findEntity(courseId);
        //verify if course is in school
        if (course == null || (course.getSchoolID() != null && !course.getSchoolID().equals(school.getSchoolID()))) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), (course != null) ? course.getCourseID() : null});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (course.isWithChildren()) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not a leaf in the course tree of school {1} for usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), (course != null) ? course.getCourseID() : null});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Internal error using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        if (course.getDwoProfileID()==null || !course.getDwoProfileID().equals(profile.getDwoProfileID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {1} is from a different profile than requested with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(),(course != null) ? course.getCourseID() : null});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        
        // end verification
        ClassCourseRWAccessData data = new ClassCourseRWAccessData(phr, school, schoolClass, course, toc, profile);
        return data;
    }
}
