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
import javax.ws.rs.core.UriInfo;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentImage;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ImageManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScoContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@Path("/secure/user/scoContext")
public class SecuredUserScoContextManager {
    private static final Logger LOG = Logger.getLogger(SecuredUserScoContextManager.class.getName());
    private static final String PUBLIC_SCO_GET_IMAGE = "../../../public/scoContext/getImage";

	/** get scos of a course.
	 * @param sc context
	 * @param rest a courseid and profileid
	 * @return list of DomScoContext
	 * @throws Dwo2Exception 
	*/
    @PUT
    @Path("/getScos")
    @Produces({"application/json"})
    public List<DomScoContext> getScos(@Context SecurityContext sc, RestCourse rest, @Context UriInfo info) throws Dwo2Exception {
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
		{
            LOG.log(Level.WARNING, "profile mismatch " + sc.getUserPrincipal().getName() );		
			//return Collections.emptyList();
		}
// match school
		if (parent.getSchoolID() != null) {
			if (parent.getSchoolID().longValue() != school.getSchoolID().longValue())
			{
	            LOG.log(Level.SEVERE, "school mismatch " + sc.getUserPrincipal().getName() );		
				//return Collections.emptyList();
			}
		} else {
			if (profile.isLimited()) {
				// assert school in profile database....
			}
		}
// userid must match
		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
		{
            LOG.log(Level.SEVERE, "user mismatch " + sc.getUserPrincipal().getName() );
			//return Collections.emptyList();
		}

		List<PersistentScoContext> list = ScoContextManager.findEntities(parent);
		String hasRoleId = phr.buildPersistenceId().getIdString();
		return list.stream().map((s)->builder(s,parent,info,hasRoleId)).sorted(new DomScoContextComparator()).collect(Collectors.toList());    	
    }

    
    @PUT
    @Path("/getTrashedScos")
    @Produces({"application/json"})
    public List<DomScoContext> getTrashedScos(@Context SecurityContext sc, RestCourse rest, @Context UriInfo info) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName()).setHasRole(rest.getRestContext().getDomHasRole());
    	state.buildSchoolAdminTeacher();
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		DomCourse domCourse = rest.getDomCourse();
		long pid = MySQLPersistenceId.getNativeId(domDwoProfile);
		long cid = MySQLPersistenceId.getNativeId(domCourse);
        PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
		PersistentCourse parent = CourseManager.findEntity(cid);
        PersistentHasRole phr = state.getHasRole();
        PersistentSchool school = state.getSchool();
     // match profile		
     		if ( pid != parent.getDwoProfileID().longValue())
     		{
                 LOG.log(Level.WARNING, "profile mismatch " + sc.getUserPrincipal().getName() );		
     			//return Collections.emptyList();
     		}
     // match school
     		if (parent.getSchoolID() != null) {
     			if (parent.getSchoolID().longValue() != school.getSchoolID().longValue())
     			{
     	            LOG.log(Level.SEVERE, "school mismatch " + sc.getUserPrincipal().getName() );		
     				//return Collections.emptyList();
     			}
     		} else {
     			if (profile.isLimited()) {
     				// assert school in profile database....
     			}
     		}
    		List<PersistentScoContext> list = ScoContextManager.findTrashedEntities(parent);
    		String hasRoleId = phr.buildPersistenceId().getIdString();
    		return list.stream().map((s)->builder(s,parent,info,hasRoleId)).sorted(new DomScoContextComparator()).collect(Collectors.toList());    	
    }
    
    private DomScoContext builder(PersistentScoContext s, PersistentCourse parent, UriInfo info, String hasRoleId) {
    	DomScoContext build = s.buildDomScoContext();
    	if(s.getTrashID() != 0) build.setSequencenr(s.getTrashID());
    	hasRoleId = "&hasRoleId=" + hasRoleId;
    	String pfx = info.getRequestUri().resolve(PUBLIC_SCO_GET_IMAGE).toString();
 
    	PersistentImage img = ImageManager.findEntity(s.getScoID());
    	
		if(img != null) {
			build.setImage(pfx + "?scoId=" + s.getScoID() + hasRoleId);
		}
		return build;
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
    public DomScoContext get(@Context SecurityContext sc, RestScoContext rest, @Context UriInfo info) throws Dwo2Exception {
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
				throw new Dwo2Exception(Dwo2ExceptionCode.Rest_LoginNeeded, "wrong credentials");
			}
			RoleType role = RoleType.values()[phr.getSchoolGroup().getGroupID()];
			switch (role) {
			case STUDENT: 
				LOG.severe("Check schoolclass/course?" ); // Not used!
				throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "login needed");
				//break;
			case TEACHER:
				if (school.accessControl()) {
					ACL acl = SecuredCommonScoDataManager.getACL(phr, parent);
					if (acl == ACL.NONE || acl == ACL.ACCESS) {
						throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "login needed");
					}
				}
			default:
			}
		} else {
			if (profile.isLimited()) {
				// assert school in profile database....
			}
		}
// userid must match
		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
		{
            LOG.log(Level.SEVERE, "user mismatch " + sc.getUserPrincipal().getName() );
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Hasrole mismatch " + sc.getUserPrincipal().getName() + " .");
		}
		String hasRoleId = phr.buildPersistenceId().getIdString();
		return builder(scoContext, parent, info, hasRoleId);    	
    }
    
    
    @PUT
    @Path("/getData")
    @Produces({"application/json"})
    public DomScoData getData(@Context SecurityContext sc, RestScoContextId rest) throws Dwo2Exception {
      UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName()).setHasRole(rest.getRestContext().getDomHasRole());
      Long id = MySQLPersistenceId.getNativeId(rest.getDomScoContext());
      PersistentScoContext sco = ScoContextManager.findEntity(id);
      Long pid = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
      if (!sco.getDwoProfileID().equals(pid)) 
        throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "wrong profile " + sc.getUserPrincipal().getName());
      switch(state.getRoleType()) {
        case ADMIN:
          break;
        default:
        case STUDENT:
          // FIXME SECURITY verify course in class or public, student in class, etc.
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "wrong role " + sc.getUserPrincipal().getName());
        case TEACHER:
        	PersistentSchool school = state.getSchool();
        	
        	if (sco.getSchoolID() != null && school.accessControl()) {
        		ACL acl = SecuredCommonScoDataManager.getACL(state.getHasRole(), CourseManager.findEntity(sco.getCourseID()));
        		if (acl == ACL.ACCESS|| acl == ACL.NONE) {
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "wrong access " + sc.getUserPrincipal().getName());
        		}
        	}
        
        
        case SCHOOLADMIN:
          if (sco.getSchoolID() != null) {
            Long sid = state.getSchool().getSchoolID();
            if (! sid.equals(sco.getSchoolID()))
              throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "wrong school " + sc.getUserPrincipal().getName());
          } else { 
            // FIXME limited profile, support here?
          }
      }
      PersistentScoData data = ScoDataManager.findEntity(id);
      DomScoData dom = data.buildDomScoData();
      return dom;
    }
}
