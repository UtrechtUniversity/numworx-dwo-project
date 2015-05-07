/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.*;
import fi.dwo.commons.persistence.entities.*;
import fi.dwo.commons.rest.entities.SchoolsAndClasses;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
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
@Path("/secure/user/schoolsandclasses")
public class SecuredSchoolsAndClassesManager {

    private static final Logger log = Logger.getLogger(SecuredSchoolsAndClassesManager.class.getName());
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
    @Path("/get/json")
    public List<SchoolsAndClasses> getSchoolsAndClasses(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.createEntityManager();

        PersistentUser user = null;
        List<PersistentHasRole> hasRoleList = null;
        List<SchoolsAndClasses> sacList = (List<SchoolsAndClasses>) new ArrayList<SchoolsAndClasses>();
        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            log.log(Level.INFO, "Fetched User with username {0}", new Object[]{name});
            log.log(Level.INFO, "And id is {0}", new Object[]{userId});
            // fetch a list of hasRole's for a user. Then create the tuples of all School, Role, classes
//            q = em.createNamedQuery("PersistentHasRole.findByUserID");
//            q.setParameter("userID", user.getUserID());
//            hasRoleList = (List<PersistentHasRole>) q.getResultList();

            q = em.createQuery("select s.schoolID, s.schoolName, g.role.groupID, g.role.groupname, h.classID, "
                    + "h.schoolClass.class1 from PersistentHasRole h, PersistentSchoolGroup g join "
                    + "g.school s where h.persistentHasRolePK.userID = :userID "
                    + "and h.persistentHasRolePK.schoolGroupID = g.schoolGroupID");
            q.setParameter("userID",userId);
            List<Object[]> resultList = q.getResultList();
            log.log(Level.INFO, "Fetched {0} HasRoles for userId {1}.", new Object[]{userId,resultList.size()});
//            SchoolsAndClasses sac;            
            SchoolsAndClasses sac;
            for (Object[] oList : resultList) {
                log.log(Level.INFO, "Fetched HasRole: {0}, {1}, {2}, {3}, {4}.", new Object[]{oList[0],oList[1],oList[2],oList[3],oList[4],oList[5]});
                sac = new SchoolsAndClasses();
                sac.setSchoolId(MySQLPersistenceId.createPersistenceId((Long) oList[0], PersistenceClassType.PersistentSchool));
                sac.setSchoolName((String) oList[1]);
                sac.setRoleId(MySQLPersistenceId.createPersistenceId((Long) oList[2], PersistenceClassType.PersistentRole));
                sac.setRoleName((String) oList[3]);
                sac.setSchoolClassId(MySQLPersistenceId.createPersistenceId((Long) oList[4], PersistenceClassType.PersistentSchoolClass));
                sac.setSchoolClassName((String) oList[5]);
                sacList.add(sac);
                TODO fix query so there are results.
            }

//            log.log(Level.INFO, "Fetched {0} HasRole's for userId {1}.", new Object[]{hasRoleList.size(), user.getUserID()});
//            SchoolsAndClasses sac;            
//            for (PersistentHasRole h : hasRoleList) {
//                log.log(Level.INFO, "Fetched HasRole: {0}, {1}, {2}, {3}, {4}.", new Object[]{h.getPersistentHasRolePK().getUserID(),
//                    h.getPersistentHasRolePK().getSchoolGroupID(), h.getRights(), h.getLastLogin()});
//                sac = new SchoolsAndClasses();    
//                sac.setSchoolId(MySQLPersistenceId.createPersistenceId(h.getSchoolGroup().getSchool().getSchoolID(),PersistenceClassType.PersistentSchool));
//                sac.setSchoolName(h.getSchoolGroup().getSchool().getSchoolName());
//                sac.setRoleId(MySQLPersistenceId.createPersistenceId(h.getSchoolGroup().getRole().getGroupID(),PersistenceClassType.PersistentRole));
//                sac.setRoleName(h.getSchoolGroup().getRole().getGroupname());
//                sac.setSchoolClassId(MySQLPersistenceId.createPersistenceId(h.getSchoolClass().getClassID(),PersistenceClassType.PersistentSchoolClass));
//                sac.setRoleName(h.getSchoolClass().getClass1());
//                sacList.add(sac);
//            }            
        } catch (Exception e) {
            log.log(Level.WARNING, "Unexpected exception: {0}", new Object[]{e.getMessage()});
        } finally {
            em.close();
        }
            return sacList;
        }        // Create all the tuples.

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
        @Path("/update/json")
        public PersistentUser updateCurrentUser
        (@Context
        SecurityContext sc, PersistentUser user
        
            ) {
        if (user.getUsername().compareTo(sc.getUserPrincipal().getName()) == 0) {
                //User to update is logged in user.
                EntityManager em = DwoEmfFactory.createEntityManager();
                try {
                    em.getTransaction().begin();
//if(true) { // beperkte update                
                    PersistentUser u = em.find(PersistentUser.class, user.getUserID());
                    u.setEmail(user.getEmail());
                    u.setFirstname(user.getFirstname());
                    u.setMiddlename(user.getMiddlename());
                    u.setLastname(user.getLastname());
                    u.setPasswd(user.getPasswd());
                    user = u;
//} else { //full update
//                user = em.merge(user);
//}
                    em.getTransaction().commit();
                    log.log(Level.FINE, "Updated User with username {0}", new Object[]{user.getUsername()});
                } finally {
                    em.close();
                }
                return user;
            } else {
                log.log(Level.WARNING, "ILLEGAL USER-OPERATION: Trying to update the user profile of {0} under user account {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
                throw new NotAuthorizedException("You Don't Have Permission to update usercode " + user.getUsername() + ".");
            }
        }

        @GET
        @Produces({"application/json"})
        @Path("/classinfo/json")
        public String info
        
            () {
        return this.getClass().getName();
        }
    }
