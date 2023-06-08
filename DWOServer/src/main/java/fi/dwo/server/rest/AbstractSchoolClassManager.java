package fi.dwo.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;


/**
 *  Shared code between TeacherSchoolClassManager en SchoolAdminSchoolClassManager
 * @author wim
 *
 */
abstract class AbstractSchoolClassManager {
	public final Logger LOG = Logger.getLogger(getClass().getName());

	protected Boolean removeStudentFromSchoolClass(SecurityContext sc, PersistentSchool school,
			PersistentUser student, PersistentHasRole shr, PersistentSchoolClass schoolClass) {
				if (student != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
					return SchoolClassUtilManager.removeStudentFromSchoolClass(shr, schoolClass);					
			    } else {
			        LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a student from a schoolclass id {1} one or both do not exists or are not in the school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
			        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove the school class.");
			    }
			}


}
