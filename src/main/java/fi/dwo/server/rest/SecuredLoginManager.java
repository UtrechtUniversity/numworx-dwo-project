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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author G.A.J. van der Plas
 */
@Path("/secure/gui/panels/login/get")
public class SecuredLoginManager {
    private static final Logger log = Logger.getLogger(SecuredLoginManager.class.getName());
    
/**
     * Returns the user data if properly logged in.  The information is extracted from the security
     * context.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/get/json")
    public PersistentUser login(@Context SecurityContext sc){
        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentUser user;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            log.log(Level.INFO, "Login accepted for user with username {0}", new Object[]{userName});
        } finally {
            em.close();
        }
        return user;
    }
}
