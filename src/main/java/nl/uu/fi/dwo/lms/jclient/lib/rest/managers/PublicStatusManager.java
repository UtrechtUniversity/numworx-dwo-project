/* Copyrighted 2015. */
package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;

/**
 * Public status manager. Provides status information for the server. HeartBeat, health, roles,
 * versions and such.
 *
 * @author G.A.J. van der Plas
 */
public class PublicStatusManager {

  private static final Logger LOG = Logger.getLogger(PublicStatusManager.class.getName());

  /**
   * Returns serverHeartBeat information.
   *
   * @return DomHeartBeat
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static DomHeartBeat getHeartBeat() throws Dwo2Exception {
    // login to rest service
    DomHeartBeat result;
    result =
        StoredRestManager.getInstance().get("rest/public/status/getHeartBeat", DomHeartBeat.class);
    return result;
  }
}
