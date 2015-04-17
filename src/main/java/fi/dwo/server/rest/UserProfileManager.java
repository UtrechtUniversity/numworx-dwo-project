/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Path("/gui/panels/userprofile")
public class UserProfileManager {

    private static final Logger log = Logger.getLogger(UserProfileManager.class.getName());

    /**
     * Returns the currentUser
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/get/json")
    public PersistentUser getCurrentUser(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentUser user;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("p.userID", userName);
            user = (PersistentUser) q.getSingleResult();
            log.log(Level.FINE, "Fetched User with username {0}", new Object[]{userName});

        } finally {
            em.close();
        }
        return user;
    }

    /**
     * Updates the profile data of the current user and returns the updated
     * data.
     *
     * @param user
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update/json")
    public PersistentUser updateCurrentUser(PersistentUser user) {
        PersistentUser u=null;
        return u;
    }
    
    
    @GET
    @Produces({"application/json"})
    @Path("/info/json")
    public String info(@Context SecurityContext sc) {
        return ""+sc.getUserPrincipal();
    }

    
    @GET
    @Produces({"application/json"})
    @Path("/classinfo/json")
    public String info() {
        return this.getClass().getName();
    }
    
}
