package fi.dwo.server.rest;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@Path("/secure/user/scoContext")
public class SecuredUserScoContextManager {
    private static final Logger LOG = Logger.getLogger(SecuredUserScoContextManager.class.getName());
	
	private String LIMITED = "l";
	
	/** get scos of a course.
	 * @param sc context
	 * @param rest a courseid and profileid
	 * @return list of DomScoContext
	 * @throws Dwo2Exception 
	*/
    @PUT
    @Path("/getScos")
    @Produces({"application/json"})
    public List<DomScoContext> getScos(@Context SecurityContext sc, RestCourse rest) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
		DomCourse domCourse = rest.getDomCourse();
// Context
        PersistentUser user = null;
        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }		
// Security 		
		long pid = MySQLPersistenceId.getNativeId(domDwoProfile);
		long cid = MySQLPersistenceId.getNativeId(domCourse);
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);

		PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
		PersistentCourse parent = CourseManager.findEntity(cid);
        PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
        PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(phr);
// match profile		
		if ( pid != parent.getDwoProfileID().longValue())
			return Collections.emptyList();
// match school
		if (parent.getSchoolID() != null) {
			if (parent.getSchoolID().longValue() != school.getSchoolID().longValue())
				return Collections.emptyList();
		} else {
			if (profile.getDwoProfileRights().contains(LIMITED)) {
				// assert school in profile database....
			}
		}
// userid must match
		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
			return Collections.emptyList();

		List<PersistentScoContext> list = ScoContextManager.findEntities(parent);
		return list.stream().map((s)->s.buildDomScoContext()).sorted(new DomScoContextComparator()).collect(Collectors.toList());    	
    }

    /**
     * get ScoContext from this scoid.
     * Check profile, must match, check school, must match hasrole
     * @param sc context
     * @param rest the scoid and profileid
     * @return a ScoContext
     * @throws Dwo2Exception
     */   
    @PUT
    @Path("/get")
    @Produces({"application/json"})
    public DomScoContext get(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		DomHasRole    domHasRole = rest.getRestContext().getDomHasRole();
		DomScoContext domScoContext = rest.getDomScoContext();	
// Context
        PersistentUser user = null;
        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
// Security:	
		long pid = MySQLPersistenceId.getNativeId(domDwoProfile);
		PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
		long sid = MySQLPersistenceId.getNativeId(domScoContext);
		PersistentScoContext scoContext = ScoContextManager.findEntity(sid);
		long cid = scoContext.getCourseID();
		PersistentCourse parent = CourseManager.findEntity(cid);
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
        PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
        PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(phr);
		
// profile match		
		if ( pid != parent.getDwoProfileID().longValue())
		{
            LOG.log(Level.WARNING, "profile mismatch " + sc.getUserPrincipal().getName() );		
			//return null;
		}
// schools matches
		if (parent.getSchoolID() != null) {
			if (parent.getSchoolID().longValue() != school.getSchoolID().longValue())
			{
	            LOG.log(Level.SEVERE, "school mismatch " + sc.getUserPrincipal().getName() );		
				return null;
			}
		} else {
			if (profile.getDwoProfileRights().contains(LIMITED)) {
				// assert school in profile database....
			}
		}
// userid must match
		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
		{
            LOG.log(Level.SEVERE, "user mismatch " + sc.getUserPrincipal().getName() );
			return null;
		}
		
		return scoContext.buildDomScoContext();    	
    }
    
}
