package fi.dwo.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;


/**
 *  Shared code between TeacherSchoolClassManager en SchoolAdminSchoolClassManager
 * @author wim
 *
 */
abstract class AbstractSchoolClassManager {
	public final Logger LOG = Logger.getLogger(getClass().getName());

	protected Boolean removeStudentFromSchoolHelper(SecurityContext sc, PersistentSchool school,
			PersistentUser student, PersistentHasRole shr, PersistentSchoolClass schoolClass) {
				if (student != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
			        try {
			            PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID());
			            if (shr.getClassID()!=null && shr.getClassID().equals(socId.getClassID())) {
			                shr.setClassID(null);
			                HasRoleManager.edit(shr);
			            }
			            StudentOfClassManager.destroy(socId);
			
			        }
			        catch (PersistenceException e) {
			            return false;
			        }
			    } else {
			        LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a student from a schoolclass id {1} one or both do not exists or are not in the school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
			        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove the school class.");
			    }
			
			    return true;
			}


}
