/*Copyrighted 2015. */
package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.RestClassType;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Returns a list of existing Roles.
 *
 * It sole purpose is to be called from the DwoHelper. Otherwise it should not
 * be called.
 *
 * @author G.A.J. van der Plas
 */
public class RoleManager {

    private static final Logger LOG = Logger.getLogger(RoleManager.class.getName());

    /**
     * Returns the user data if properly logged in. The information is extracted
     * from the security context.
     *
     * @return Returns null if there was an error.
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     * @throws fi.dwo.dwojapplet.domain.rest.RestException
     */
    public static List<PersistentRole> getRoles() throws Dwo2Exception {
        //login to rest service
        List<PersistentRole> roles;
//        GenericType<ArrayList<PersistentRole>> oClass = new GenericType<ArrayList<PersistentRole>>() {};
//        roles =  StoredRestManager.getWebTargetRest().path("/rest/public/roles/get/json").request().get(oClass);
//        LOG.log(Level.FINER, "Fetched {0} roles.", new Object[]{roles.size()});
//        return roles;
        roles = StoredRestManager.getInstance().getList("/rest/public/roles/get", RestClassType.PersistentRole);
        return roles;

    
    }
}
