package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

/**
 * Manages the users in the DWO.
 *
 * @author G.A.J. van der Plas
 */
public class SecureDwoAdminUserManager {

    private static final Logger LOG = Logger.getLogger(SecureDwoAdminUserManager.class.getName());

    public static List<DomUser> getUserList() throws Dwo2Exception {
        List<DomUser> src;
        src = StoredRestManager.getInstance().getList("rest/secure/dwoadmin/user/getList", RestListClassTypes.DomUser);
        LOG.log(Level.FINE, "Retrieved list of schoolsfor the dwoadmin with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

}
