/*Copyrighted 2015. */
package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomRole;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.RestListClassTypes;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Returns a list of existing Roles. This function is deprecated.
 *
 * It sole purpose is to be called from the DwoHelper. Otherwise it should not
 * be called.
 *
 * @author G.A.J. van der Plas
 */
@Deprecated
public class PublicRoleManager {

    private static final Logger LOG = Logger.getLogger(PublicRoleManager.class.getName());

    /**
     * Returns the user data if properly logged in. The information is extracted
     * from the security context.
     *
     * @return Returns null if there was an error.
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public static List<DomRole> getRoles() throws Dwo2Exception {
        //login to rest service
        List<DomRole> roles;
        roles = StoredRestManager.getInstance().getList("/rest/public/roles/getlist", RestListClassTypes.DomRole);
        return roles;

    
    }
}
