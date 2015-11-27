/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
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
    public DomFullUser getCurrentUser(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentUser user = null;
        
        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(),user.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Username "+sc.getUserPrincipal().getName()+": Unexpected exception",e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
        finally {
            em.close();
        }
        return new DomFullUser(user);
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
    public PersistentUser updateCurrentUser(@Context SecurityContext sc, PersistentUser user) {
        if (user.getUsername().equals(sc.getUserPrincipal().getName())) {
            try {
                PersistentUser dbUser = UserManager.findByUserName(user.getUsername());
                dbUser.setFirstname(user.getFirstname());
                dbUser.setLastname(user.getLastname());
                dbUser.setMiddlename(user.getMiddlename());
                dbUser.setEmail(user.getEmail());
                dbUser.setPasswd(user.getPasswd());
                //User to update is logged in user.
                UserManager.edit(dbUser);
                return UserManager.findByUserName(user.getUsername());
            }
            catch (Exception e) {
            LOG.log(Level.SEVERE, "Username "+sc.getUserPrincipal().getName()+": Unexpected exception",e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update user id " + sc.getUserPrincipal().getName() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getUsername() + ".");
        }
    }

    /**
     * Removes all the User data of the current user and returns true.
     * \texttt{StudentScoData},\texttt{StudentScoContext}, \texttt{StudentOf}, \texttt{TeacherOf\texttt{HasRole}, \texttt{SamlUser}, \texttt{User}.

     *
     * @param sc
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeCurrentUser(@Context SecurityContext sc) {
        PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
        if(u==null) return true;
        List<PersistentHasRole> hrList = HasRoleManager.findEntities(u);
            for(PersistentHasRole hr : hrList){
                List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
                for(PersistentStudentScoContext ssc : sscList){
                    StudentScoDataManager.destroy(ssc.getStudentSco());
                    StudentScoContextManager.destroy(ssc.getStudentSco());
                }
                //Remove StudentOf and TeacherOf
                List<PersistentStudentOfClass> soList = StudentOfClassManager.findEntities(hr.getPersistentHasRolePK());
                for(PersistentStudentOfClass so : soList){
                    StudentOfClassManager.destroy(so.getPersistentStudentOfClassPK());
                }
                List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(hr.getPersistentHasRolePK());
                for(PersistentTeacherOfClass to : toList){
                    TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
                }
                //Ready to remove hasRoles
                HasRoleManager.destroy(hr.getPersistentHasRolePK());
            }
            //Ready to remove User
            UserManager.destroy(u.getUserID());
        return true;
    }    
    
}
