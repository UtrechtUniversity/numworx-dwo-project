package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.net.URLEncoder;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicProfileManager {

  public static DomDwoProfileFull get(String name) throws Dwo2Exception {
    name = URLEncoder.encode(name);
    name = name.replace("+", "%20");
    return StoredRestManager.getInstance().get("rest/public/profile/" + name,
        DomDwoProfileFull.class);
  }

  public static DomDwoProfileFull get(long id) throws Dwo2Exception {
    return get(Long.toString(id));
  }

  public static DomDwoProfileFull get(int id) throws Dwo2Exception {
    return get(Integer.toString(id));
  }

}
