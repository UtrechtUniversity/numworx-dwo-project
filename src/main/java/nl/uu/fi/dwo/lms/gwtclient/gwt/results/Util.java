package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONValue;

/**
 * Utility pattern
 * 
 * @author peterboon
 *
 */
class Util {
  private static final Logger LOG = Logger.getLogger(Util.class.getName());

  private Util() {}

  Integer getAantalOpdrachten(JSONValue launch_data) {
    Integer aantalOpdrachten;
    try {
      aantalOpdrachten =
          Integer.valueOf(launch_data.isObject().get("aantalOpdrachten_1").isString().stringValue());
      LOG.log(Level.FINE, "aantalOpdrachten = " + aantalOpdrachten);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "aantalOpdrachten failed", e);
      aantalOpdrachten = 0;
    }
    return aantalOpdrachten;
  }

}
