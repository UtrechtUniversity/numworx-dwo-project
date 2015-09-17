/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentEntityManagers.UserManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * User login obtained through validation by the tomcat configuration. 
 * 
 * @author G.A.J. van der Plas
 */
@Path("/secure/user/login")
public class SecuredLoginManager {
//TODO rewrite this for SAML stuff or session passwords.
    
    private static final Logger LOG = Logger.getLogger(SecuredLoginManager.class.getName());

    /**
     * Returns the user data if properly logged in. The information is extracted
     * from the security context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/get/json")
    @Deprecated
    public PersistentUser login(@Context SecurityContext sc) {
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        PersistentUser user = UserManager.findByUserName(userName);
        LOG.log(Level.INFO, "Username {0}: Login accepted.", new Object[]{user.getUsername()});
        return user;
    }
}
