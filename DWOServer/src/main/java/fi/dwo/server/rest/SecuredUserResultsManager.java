package fi.dwo.server.rest;

import java.util.ArrayList;
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
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@PermitAll
@Path("/secure/user/results")
public class SecuredUserResultsManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserResultsManager.class.getName());

    @PUT
    @Produces({"application/json"})
    @Path("/getCourseResults")
    public Response getCourseResults(@Context SecurityContext sc, RestCourse rest) throws Dwo2Exception {
        List<DomStudentScoContext> result = new ArrayList<DomStudentScoContext>();
// NPE
        DomContext context = rest.getRestContext();
        DomHasRole domHasRole = context.getDomHasRole();
        DomCourse domCourse = rest.getDomCourse();
        DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
// Context
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(domHasRole);
        PersistentUser user = null;
        try {
			user = state.getUser();
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
// Security 		
        long pid = MySQLPersistenceId.getNativeId(domDwoProfile);
        long cid = MySQLPersistenceId.getNativeId(domCourse);
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);

        PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
        PersistentCourse parent = CourseManager.findEntity(cid);
        PersistentHasRole phr = state.getHasRole();
        PersistentSchool school = state.getSchool();

        CacheControl cc = new CacheControl();
        cc.setMaxAge(0);
        cc.setNoCache(true);
        cc.setNoStore(true);
// match profile		
        if (parent == null || pid != parent.getDwoProfileID().longValue()) {
            return Response.ok(result, "application/json").cacheControl(cc).build();//List<DomStudentScoContext>
        }// match school
        if (parent.getSchoolID() != null) {
            if (parent.getSchoolID().longValue() != school.getSchoolID().longValue()) {
            return Response.ok(result, "application/json").cacheControl(cc).build();//List<DomStudentScoContext>
            }
        } else if (profile.isLimited()) {
            // assert school in profile database....
        }
// userid must match
        if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue()) {
            return Response.ok(result, "application/json").cacheControl(cc).build();//List<DomStudentScoContext>
        }// fetch results
        List<PersistentScoContext> list = ScoContextManager.findEntities(parent);

        for (PersistentScoContext scoContext : list) {
            List<PersistentStudentScoContext> lpssc = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
            if (lpssc.isEmpty()) {
                continue;
            }
            PersistentStudentScoContext pssc = lpssc.get(0); // assert 1 element
            DomStudentScoContext results = pssc.buildDomStudentScoContext();
            result.add(results);
        }
            return Response.ok(result, "application/json").cacheControl(cc).build();//List<DomStudentScoContext>
    }
}
