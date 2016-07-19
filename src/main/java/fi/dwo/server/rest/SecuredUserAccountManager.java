package fi.dwo.server.rest;

import fi.dwo.commons.persistence.LogType;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentLogData;
import fi.dwo.commons.persistence.entities.PersistentLoginDataPK;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import fi.dwo.rest.entities.RestUserFull;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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
    @Path("/login")
    public DomUserFull loginUser(@Context SecurityContext sc) {
        DomUserFull user = getCurrentUser(sc);
        //al ready retrieved and cached in getCurrentUser
        PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
        try {//LoginData may fail, but login should succeed.
            //register login action
            PersistentLogData loginData = new PersistentLogData();
            PersistentLoginDataPK ldKey = new PersistentLoginDataPK();
            ldKey.setUsername(user.getUserName());
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
        return user;
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
    @Path("/logout")
    public Boolean logoutUser(@Context SecurityContext sc) {
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
            return false;
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
        if(user==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }        
        if(!ValidUserFieldsChecker.isValidEmail(user.getDomUserFull().getEmail())){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Adres_Invalid, "The email address does not  conform with RFC 5322.");
        }
        if(!ValidUserFieldsChecker.isValidUserName(user.getDomUserFull().getUserName())){
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
