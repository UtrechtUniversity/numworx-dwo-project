package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;


import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class PublicServerStatusManager {
  private static final Logger LOG = Logger.getLogger(PublicServerStatusManager.class.getName());

  public static DomHeartBeat getHeartBeat() throws Dwo2Exception {
    DomHeartBeat result;
    result =
        StoredRestManager.getInstance().get("rest/public/status/getHeartBeat", DomHeartBeat.class);
    LOG.log(Level.FINE, "Retrieved PublicServerStatusHeartBeat, server unixTimeStamp {0}.",
        result.getServerTimeStamp());
    return result;
  }
}
