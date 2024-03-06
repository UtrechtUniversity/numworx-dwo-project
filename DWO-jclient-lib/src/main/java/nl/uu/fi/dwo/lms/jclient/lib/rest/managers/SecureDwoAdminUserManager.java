package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestUser;
import nl.uu.fi.dwo.rest.entities.RestUserFull;

/**
 * Manages the users in the DWO.
 *
 * @author G.A.J. van der Plas
 */
public class SecureDwoAdminUserManager {

  private static final Logger LOG = Logger.getLogger(SecureDwoAdminUserManager.class.getName());

  public static List<DomUserFull> getUserList() throws Dwo2Exception {
    List<DomUserFull> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/user/getList", 
        RestListClassTypes.DomUserFull, rest);
    LOG.log(Level.FINE, "Retrieved list of schoolsfor the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static DomUserFull get(DomUser user) throws Dwo2Exception {
    RestUser restUser = new RestUser();
    restUser.setRestContext(getContext());
    restUser.setDomUser(user);

    DomUserFull result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/user/get",
        DomUserFull.class, restUser);
    LOG.log(Level.FINE, "Retrieved userdata of user {1} for the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            restUser.getDomUser().getUserName()});
    return result;
  }

  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public static DomUserFull update(DomUserFull user) throws Dwo2Exception {
    RestUserFull restUser = new RestUserFull();
    restUser.setRestContext(getContext());
    restUser.setDomUserFull(user);

    DomUserFull result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/user/update",
        DomUserFull.class, restUser);
    LOG.log(Level.FINE, "Retrieved userdata of user {1} for the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            restUser.getDomUserFull().getUserName()});
    return result;
  }
}
