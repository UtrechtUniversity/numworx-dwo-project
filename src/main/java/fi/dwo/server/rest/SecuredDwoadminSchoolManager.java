/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentEntityManagers.SchoolManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
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
@Path("/secure/dwoadmin/school")
public class SecuredDwoadminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredDwoadminSchoolManager.class.getName());

    
    /**
     * Registers a new school.
     *
     * @param sc
     * @param school
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public PersistentSchool addSchool(@Context SecurityContext sc, PersistentSchool school) {
        if (RoleChecker.isInRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN)) {
            // do stuff
            PersistentSchool s = null;
            try {
                SchoolManager.create(school);
                SchoolManager.findBySchoolLogin(school.getSchoolLogin());
                return s;
            }
            catch (Exception e) {
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating school "+school.getSchoolName()+".");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
    
    /**
     * Returns a school from its persistent id.
     *
     * @param sc
     * @param pid
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/get")
    public PersistentSchool getSchool(@Context SecurityContext sc, PersistenceId pid) {
        if (RoleChecker.isInRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN)) {
            // do stuff
            PersistentSchool s = null;
            try {
                s = SchoolManager.findEntity((int) MySQLPersistenceId.getId(pid));
                LOG.log(Level.FINER, "Fetched school with id {0}. ", new Object[]{s.getSchoolID()});
                return s;
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "School " + pid + "Could not be found.", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the school.");

            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    };

/**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/getlist")
    public List<PersistentSchool> getSchools(@Context SecurityContext sc) {
        List<PersistentSchool> schools = null;
        try {
            schools = SchoolManager.findEntities();
            LOG.log(Level.FINER, "Fetched all {0} schools. ", new Object[]{schools.size()});
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schools.");
        }
        return schools;
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
    public PersistentSchool updateSchool(@Context SecurityContext sc, PersistentSchool school) {
//        if (school.getUsername().equals(sc.getUserPrincipal().getName())) {
//            //User to update is logged in user.
//            EntityManager em = DwoEmfFactory.getEntityManager();
//            try {
//                em.getTransaction().begin();
////if(true) { // beperkte update                
//                PersistentUser u = em.find(PersistentUser.class, user.getUserID());
//                u.setEmail(user.getEmail());
//                u.setFirstname(user.getFirstname());
//                u.setMiddlename(user.getMiddlename());
//                u.setLastname(user.getLastname());
//                u.setPasswd(user.getPasswd());
//                user = u;
////} else { //full update
////                user = em.merge(user);
////}
//                em.getTransaction().commit();
//                LOG.log(Level.FINE, "Username {0}: Updated User with username {0}", new Object[]{sc.getUserPrincipal().getName(),user.getUsername()});
//            }
//            finally {
//                em.close();
//            }
//            return user;
//        } else {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getUsername() + ".");
//
//        }
        return new PersistentSchool();
    }

    /**
     * Removes all the school data of the current school and returns true.
     *
     * @param sc
     * @param school
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchool(@Context SecurityContext sc, PersistentSchool school) {
//        if (school.getUsername().equals(sc.getUserPrincipal().getName())) {
        //User to update is logged in user.
//            EntityManager em = DwoEmfFactory.getEntityManager();
//            try {
//                em.getTransaction().begin();
////if(true) { // beperkte update                
//                PersistentUser u = em.find(PersistentUser.class, user.getUserID());
//                u.setEmail(user.getEmail());
//                u.setFirstname(user.getFirstname());
//                u.setMiddlename(user.getMiddlename());
//                u.setLastname(user.getLastname());
//                u.setPasswd(user.getPasswd());
//                user = u;
////} else { //full update
////                user = em.merge(user);
////}
//                em.getTransaction().commit();
//                LOG.log(Level.FINE, "Username {0}: Updated User with username {0}", new Object[]{sc.getUserPrincipal().getName(),user.getUsername()});
//            }
//            finally {
//                em.close();
//            }
//            return user;
//        } else {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getUsername() + ".");
//
//        }
        return true;
    }

}
