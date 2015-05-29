/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.rest.entities.NewUserRegistration;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.SchoolRoleAndClass;
import fi.dwo.commons.rest.entities.SchoolsRolesAndClasses;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Handles the public registration of a new user.
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */

@Path("/public/registration")
public class PublicRegistrationManager {
/**
     * Registers a new user.
     *
     * @param sc
     * @param newUserReg
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/registerUser/json")
    public boolean setActiveSchoolRoleAndClass(@Context SecurityContext sc, NewUserRegistration newUserReg) {
        EntityManager em = DwoEmfFactory.createEntityManager();

        PersistentUser user =new PersistentUser();
        user.setFirstname(newUserReg.);

        // fetch the authenticated user
        try {
            //User to update is logged as user.
            em.getTransaction().begin();
            // add user
            dfgsd
            em.getTransaction()
                    .commit();
            LOG.log(Level.INFO,
                    "Added new user to persistent store.", new Object[]{}
                    }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unexpected exception: {0}", new Object[]{e.getMessage()});
            return false;
        } finally {
            em.close();
        }
        return true;
    }        // Create all the tuples.  
}
