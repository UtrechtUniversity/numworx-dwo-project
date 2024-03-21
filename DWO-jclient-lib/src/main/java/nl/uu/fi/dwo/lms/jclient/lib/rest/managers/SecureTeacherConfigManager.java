package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureTeacherConfigManager implements ConfigManager {
  private static final Logger LOG = Logger.getLogger(SecureTeacherConfigManager.class.getName());

  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public List<DomAppletConfig> getConfigurations(Locale locale, DomDwoProfile profile)  throws Dwo2Exception {
    RestDwoProfile rest = new RestDwoProfile(profile, getContext());
    RestListClassTypes type = RestListClassTypes.DomAppletConfig;
    List<DomAppletConfig> result =
      StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/config/getList/"+locale, type, rest);
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

	@Override
	public List<DomAppletConfig> getConfigurations(Locale l) {		
		return Collections.emptyList();
	}
  
}
