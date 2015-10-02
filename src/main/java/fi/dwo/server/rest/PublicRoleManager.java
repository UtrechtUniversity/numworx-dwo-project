
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.server.PersistentDataManagers.core.RoleManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Public access to the different DWO-roles available for users.
 * 
 * @author G.A.J. van der Plas
 */

@Path("/public/roles")
public class PublicRoleManager {
private static final Logger LOG = Logger.getLogger(SecuredSamlUserLoginManager.class.getName());
    
/**
     * Returns the user data if properly logged in.  The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/getlist")
    public List<PersistentRole> getRoles(@Context SecurityContext sc){
        List<PersistentRole> roles = null;
        try {
            roles = RoleManager.findEntities();
            LOG.log(Level.FINER, "Fetched all {0} user roles. ", new Object[]{roles.size()});
        } catch(Exception e){
            LOG.log(Level.WARNING,"Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the user roles.");
        }
        return roles;
    }    
}
