package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolAndProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureTeacherConfigManager implements ConfigManager {
  private static final Logger LOG = Logger.getLogger(SecureTeacherConfigManager.class.getName());

  static DomContext getContext() {
    return RestAuthenticator.getInstance().getContext();
  }

  public List<DomAppletConfig> getConfigurations(Locale locale)  throws Dwo2Exception {
    RestSchoolAndProfile rest = new RestSchoolAndProfile();
    rest.setRestContext(getContext());
    RestListClassTypes type = RestListClassTypes.DomCourse;
    List<DomAppletConfig> result =
      StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/config/getList/"+locale, type, rest);
    return result;
  }
  
}
