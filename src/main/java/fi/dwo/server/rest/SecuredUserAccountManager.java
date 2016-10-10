package fi.dwo.server.rest;

import fi.dwo.commons.persistence.LogType;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentLogData;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentLoginDataPK;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.LoginDataManager;
import fi.dwo.server.PersistentDataManagers.core.RoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/user/account")
public class SecuredUserAccountManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountManager.class.getName());
//    @Context  //injected response proxy supporting multiple threads
//    private HttpServletResponse response;

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/get")
    public DomUserFull getCurrentUser(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentUser user = null;

        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
        finally {
            em.close();
        }
        return user.buildDomUserFull();
    }

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/getLoginContext")
    public DomLoginContext getLoginContext(@Context SecurityContext sc) {
        PersistentUser user = null;
        PersistentLoginContext loginContext = null;

        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            List<PersistentLoginContext> list = LoginContextManager.findEntities(user.getId());
            if (list.size() == 1) {
                loginContext = list.get(0);
            } else {
                loginContext = new PersistentLoginContext();
                loginContext.setUserId(user.getId());
                loginContext.setLastLogin(null);
                loginContext.setRegisterTimeStamp(user.getRegisterDate().getTime());
                LoginContextManager.create(loginContext);
            }
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
        return loginContext.buildDomLoginContext();
    }

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/login")
    public DomUserFullwLoginContext loginUser(@Context SecurityContext sc) {
        PersistentUser u;

        try {
            u = UserManager.findByUserName(sc.getUserPrincipal().getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), u.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }

        return createUserFullwLoginContext(u);
    }

	public static DomUserFullwLoginContext createUserFullwLoginContext(PersistentUser u) {
		//al ready retrieved and cached in getCurrentUser
        try {//LoginData may fail, but login should succeed.
            //register login action
            PersistentLogData loginData = new PersistentLogData();
            PersistentLoginDataPK ldKey = new PersistentLoginDataPK();
            ldKey.setUsername(u.getUsername());
            ldKey.setUtcTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            PersistentSchoolGroup sg = SchoolGroupManager.findEntity(u.getSchoolGroupId());
            PersistentRole g = RoleManager.findEntity((long) sg.getGroupID());

            loginData.setRole(g.getGroupname());
            loginData.setMessage(LogType.Login);
            loginData.setLogLevel(Level.INFO.toString());
            LoginDataManager.create(loginData);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

        //setting PersistentLoginContext
        DomLoginContext domLoginContext = null;
        try {
            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
            PersistentLoginContext loginContext = new PersistentLoginContext();
            switch (loginContextList.size()) {
                case 0:
                    //none yet
                    loginContext.setUserId(u.getId());
                    loginContext.setLastLogin(null);
                    loginContext.setRegisterTimeStamp(u.getRegisterDate().getTime());
                    loginContextList.add(loginContext);
                    LoginContextManager.create(loginContext);
                    break;
                case 1:
                    //update if exists
                    loginContext = loginContextList.get(0);
                    loginContext.setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                    LoginContextManager.edit(loginContext);
                    break;
                default:
            }
            //add or update
            domLoginContext = loginContext.buildDomLoginContext();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        DomUserFullwLoginContext result = new DomUserFullwLoginContext();
        result.setDomLoginContext(domLoginContext);
        result.setDomUserFull(u.buildDomUserFull());
        return result;
	}

    @PUT
    @Produces({"application/json"})
    @Path("/basicAuthLogout")
    public Response basicAuthLogout(@Context SecurityContext sc, @Context HttpServletRequest servletRequest, RestLoginContext loginContext) {
        logoutUser(sc, loginContext);
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "Logout for basic Authentication performed: " + userName + ".");
        Response r = Response.status(401).entity(e.getMessage()).build();
        if (servletRequest.getSession() != null) {
            servletRequest.getSession().invalidate();
        }
        return r;
    }

    /**
     * Returns the DomUserFull if the form parameter equals the security context
     * user name otherwise a 401.
     * Necessary for stubborn browsers that keep authorization to long in cache.
     * POST to relax jamon resources.
     * @param sc security context
     * @param user us
     * @return 
     */
    @POST
    @Path("/loginUser")
    public Response loginUserWithPOST(@Context SecurityContext sc, @FormParam("user") String user) {
    	return loginUser(sc, user);
    }
    
    /**
     * Returns the DomUserFull if the path parameter equals the security context
     * user name otherwise a 401.
     *
     * @param sc security context
     * @param user us
     * @return 
     */
    @Deprecated
    @GET
    @Path("/loginUser/{user}")
    public Response loginUser(@Context SecurityContext sc, @PathParam("user") String user) {
        Response result;
        
        DomUserFullwLoginContext domUser = loginUser(sc);
        String domUserName = domUser.getDomUserFull().getUserName();
        if (domUserName.equalsIgnoreCase(user)) {
            result = Response.ok().
                    type(MediaType.APPLICATION_JSON_TYPE).
                    entity(domUser).build();
        } else {
            result = Response.status(Response.Status.UNAUTHORIZED).
                    header("WWW-Authenticate", "Basic realm=\"DWO.nl\"").
                    build();
        }
        return result;
    }

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @param loginContext
     * @return Returns null if there was an error.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/logout")
    public Boolean logoutUser(@Context SecurityContext sc, RestLoginContext loginContext) {
        PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
        try {//LoginData may fail, but login should succeed.
            //register login action
            PersistentLogData loginData = new PersistentLogData();
            PersistentLoginDataPK ldKey = new PersistentLoginDataPK();
            ldKey.setUsername(u.getUsername());
            ldKey.setUtcTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            PersistentSchoolGroup sg = SchoolGroupManager.findEntity(u.getSchoolGroupId());
            PersistentRole g = RoleManager.findEntity((long) sg.getGroupID());

            loginData.setRole(g.getGroupname());
            loginData.setMessage(LogType.Logout);
            loginData.setLogLevel(Level.INFO.toString());
            LoginDataManager.create(loginData);

        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            //return false;
        }
        //erasing PersistentLoginContext only if proper setRegisterTimeStamp
        if (loginContext != null) {
            try {
                List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
                if (loginContextList.size() == 1) {
                    if (loginContext.getDomLoginContext().getRegisterTimeStamp().equals((loginContextList.get(0).getRegisterTimeStamp()))) {
                        loginContextList.get(0).setLastLogin(null);
                        LoginContextManager.edit(loginContextList.get(0));
                    }
                } else {
                    //logout while no login tried before.
                    LOG.log(Level.FINE, "Logging out by user {0} while user has never logged in.", u.getId());

                }
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
        return true;
    }

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data.
     *
     * @param sc
     * @param user
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public DomUserFull updateCurrentUser(@Context SecurityContext sc, RestUserFull user) {
        if (user == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        //passwords are already hashed.
        if (!ValidUserFieldsChecker.isValidEmail(user.getDomUserFull().getEmail())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
        if (!ValidUserFieldsChecker.isValidUserName(user.getDomUserFull().getUserName())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
        }

        if (user.getDomUserFull().getUserName().equals(sc.getUserPrincipal().getName())) {
            try {
                PersistentUser dbUser = UserManager.findByUserName(user.getDomUserFull().getUserName());
                dbUser.setGivenName(user.getDomUserFull().getGivenName());
                dbUser.setLastname(user.getDomUserFull().getFamilyName());
                dbUser.setInsertion(user.getDomUserFull().getInsertion());
                dbUser.setEmail(user.getDomUserFull().getEmail());
                dbUser.setPassword(user.getDomUserFull().getPassword());
                //User to update is logged in user.
                UserManager.edit(dbUser);
                PersistentUser pUser = UserManager.findByUserName(user.getDomUserFull().getUserName());
                return pUser.buildDomUserFull();
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update user id " + sc.getUserPrincipal().getName() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getDomUserFull().getUserName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getDomUserFull().getUserName() + ".");
        }
    }

    /**
     * Removes all the User data of the current user and returns true.
     * \texttt{StudentScoData},\texttt{StudentScoContext}, \texttt{StudentOf},
     * \texttt{TeacherOf\texttt{HasRole}, \texttt{SamlUser}, \texttt{User}.
     *
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeCurrentUser(@Context SecurityContext sc) {
        PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
        if (u == null) {
            return true;
        }
        List<PersistentHasRole> hrList = HasRoleManager.findEntities(u);
        for (PersistentHasRole hr : hrList) {
            List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
            for (PersistentStudentScoContext ssc : sscList) {
                StudentScoDataManager.destroy(ssc.getStudentSco());
                StudentScoContextManager.destroy(ssc.getStudentSco());
            }
            //Remove StudentOf and TeacherOf
            List<PersistentStudentOfClass> soList = StudentOfClassManager.findEntities(hr.getPersistentHasRolePK());
            for (PersistentStudentOfClass so : soList) {
                StudentOfClassManager.destroy(so.getPersistentStudentOfClassPK());
            }
            List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(hr.getPersistentHasRolePK());
            for (PersistentTeacherOfClass to : toList) {
                TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
            }
            //Ready to remove hasRoles
            HasRoleManager.destroy(hr.getPersistentHasRolePK());
        }
        //Ready to remove User
        UserManager.destroy(u.getId());
        return new Boolean(true);
    }
}
