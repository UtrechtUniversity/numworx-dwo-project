package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureDwoAdminAppletManager {
  private static final Logger LOG = Logger.getLogger(SecureDwoAdminAppletManager.class.getName());

  public static List<DomAppletFull> getApplets() throws Dwo2Exception {
    List<DomAppletFull> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/applet/getList",
        RestListClassTypes.DomApplet, rest);
    LOG.log(Level.FINE, "Retrieved list of applet for the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  private static DomContext getContext() {
    return StoredRestManager.getInstance().getAuthenticator().getContext();
  }

}
