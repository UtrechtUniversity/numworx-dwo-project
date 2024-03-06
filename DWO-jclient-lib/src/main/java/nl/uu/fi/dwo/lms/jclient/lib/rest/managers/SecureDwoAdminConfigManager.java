package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestAppletConfig;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureDwoAdminConfigManager implements ConfigManager {
  private static final Logger LOG = Logger.getLogger(SecureDwoAdminConfigManager.class.getName());

  public List<DomAppletConfig> getConfigurations(Locale locale) throws Dwo2Exception {
    List<DomAppletConfig> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/config/getList/" + locale,
        RestListClassTypes.DomAppletConfig, rest);
    LOG.log(Level.FINE, "Retrieved list of appletconfigs for the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static Boolean updateConfig(DomAppletConfig config) throws Dwo2Exception {
    Boolean result = Boolean.FALSE;
    RestAppletConfig rest = new RestAppletConfig();
    rest.setDomAppletConfig(config);
    rest.setRestContext(getContext());
    result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/config/update",
        Boolean.class, rest);
    return result;
  }

  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public static Boolean submitConfig(DomAppletConfig profile) throws Dwo2Exception {
    Boolean result = Boolean.FALSE;
    RestAppletConfig rest = new RestAppletConfig();
    rest.setDomAppletConfig(profile);
    rest.setRestContext(getContext());
    result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/config/submit",
        Boolean.class, rest);
    return result;
  }

  public static Boolean removeConfig(DomAppletConfig profile) throws Dwo2Exception {
    Boolean result = Boolean.FALSE;
    RestAppletConfig rest = new RestAppletConfig();
    rest.setDomAppletConfig(profile);
    rest.setRestContext(getContext());
    result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/config/remove",
        Boolean.class, rest);
    return result;
  }

	@Override
	public List<DomAppletConfig> getConfigurations(Locale locale, DomDwoProfile profile) throws Dwo2Exception {
	List<DomAppletConfig> result = getConfigurations(locale);
	 // Filter profile
    Iterator<DomAppletConfig> iter = result.iterator();
    while (iter.hasNext()) {
      DomAppletConfig ac = iter.next();
      if (ac.getDwoProfileId() == null) continue; // Global
      if (ac.getDwoProfileId().getId().equals(profile.getId())) // Specifiek
        continue;
      iter.remove();
    }

	return result;
}


}
