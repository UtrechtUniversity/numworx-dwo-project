package fi.dwo.server.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@PermitAll
@Path("/secure/teacher/scormValues")
public class SecuredTeacherScormValuesManager {
    private static final Logger LOG = Logger.getLogger(SecuredTeacherScormValuesManager.class.getName());

    @PUT
    @Produces({"application/json"})
    @Path("/get")
    DomTeacherScormValues get(@Context SecurityContext sc, RestTeacherScormValues rest) throws Dwo2Exception {
    	DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    	DomStudentScoContext ssc = rest.getDomTeacherScormValues().getStudentScoContext();
    	// Context
    	PersistentUser user = null;
    	try {
    		user = UserManager.findByUserName(sc.getUserPrincipal().getName());
    		LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
    	}
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
        PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
// TODO check if domHasRole is a teacher of same school as pssc
        // ...
        Long id = MySQLPersistenceId.getNativeId(ssc);
		List<DomMapEntry<String, String>> entryList = rest.getDomTeacherScormValues().getValues();
		PersistentStudentScoContext pssc = StudentScoContextManager.findEntity(id);
		SecuredUserScoDataManager.getScormValues(entryList, pssc);
    	return rest.getDomTeacherScormValues();
    }

    @PUT
    @Produces({"application/json"})
    @Path("/set")
    Boolean set(@Context SecurityContext sc, RestTeacherScormValues rest) {
    	return Boolean.FALSE;
    }

	
}
