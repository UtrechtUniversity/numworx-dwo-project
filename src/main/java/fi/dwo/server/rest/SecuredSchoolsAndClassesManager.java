/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.rest.RestClassType;
import fi.dwo.commons.persistence.*;
import fi.dwo.commons.persistence.entities.*;
import fi.dwo.commons.rest.entities.*;
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
@Path("/secure/user/schoolsrolesandclasses")
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
    public SchoolsRolesAndClasses getSchoolsAndClasses(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        SchoolsRolesAndClasses sacs = new SchoolsRolesAndClasses();

        PersistentUser user = null;
        List<PersistentHasRole> hasRoleList = null;
        List<SchoolRoleAndClass> sacList = (List<SchoolRoleAndClass>) new ArrayList<SchoolRoleAndClass>();
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
            try {
                //Sample query
                //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
                q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, "
                        + "h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID "
                        + "from PersistentHasRole h where h.persistentHasRolePK.userID = :userID "
                        + " ");
                q.setParameter("userID", userId);
                List<Object[]> resultList = q.getResultList();
                log.log(Level.INFO, "Fetched {1} HasRoles for userId {0}.", new Object[]{userId, resultList.size()});
                SchoolRoleAndClass sac;
                for (Object[] oList : resultList) {
                    log.log(Level.INFO, "Fetched tuple <schoolID, schoolName, groupID, groupname, classID>: {0}, {1}, {2}, {3}, {4}.", new Object[]{oList[0], oList[1], oList[2], oList[3], oList[4]});
                    sac = new SchoolRoleAndClass();
                    sac.setSchoolId((Integer) oList[0]);
                    sac.setSchoolName((String) oList[1]);
                    sac.setRoleId((Integer) oList[2]);
                    sac.setRoleName((String) oList[3]);
                    if (oList[4] != null) {
                        sac.setSchoolClassId((Integer) oList[4]);
                        sac.setSchoolClassName((String) em.createQuery("select c.class1 from PersistentSchoolClass c where c.classID = :id ").setParameter("id", (Integer) oList[4]).getSingleResult());
                    } else {
                        sac.setSchoolClassId(-1);
                        sac.setSchoolClassName(null);
                    }
                    sacList.add(sac);
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Unexpected exception: {0}", new Object[]{e.getMessage()});
                return new SchoolsRolesAndClasses();
            }
        } finally {
            em.close();
        }
        SchoolRoleAndClass curSac = new SchoolRoleAndClass();
        sacs.setCurrentSchoolRoleAndClass(curSac);
        sacs.setSchoolsRolesAndClassesList(sacList);
        //TODO run query for this.
        return sacs;
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
    public PersistentUser updateCurrentUser(@Context SecurityContext sc, PersistentUser user
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
    public String info() {
        return this.getClass().getName();
    }
}
