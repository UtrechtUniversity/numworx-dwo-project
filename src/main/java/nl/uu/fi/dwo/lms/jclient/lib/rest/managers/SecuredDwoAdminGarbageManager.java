package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SecuredDwoAdminGarbageManager {

  private final StoredRestManager manager;

  public SecuredDwoAdminGarbageManager(StoredRestManager manager) {
    this.manager = manager;
  }
  
  public List<DomUserFullwLoginContext> getUsers(Integer amount, Long since) throws Dwo2Exception {
    List<DomUserFullwLoginContext> result = null;
    String query = "";
    if (amount != null) {
      query = "?limit=" + amount;
    }
    String path = "rest/secure/dwoadmin/garbage/user/get" + query;
    RestListClassTypes type = RestListClassTypes.DomUserFullwLoginContext;
    result = manager.getList(path, type);    
    return result;
  }
  
  public Boolean removeUser(DomUser user) throws Dwo2Exception {
    RestUser rest = new RestUser();
    rest.setDomUser(user);
    rest.setRestContext(manager.getAuthenticator().getContext());
    Boolean result;
    result = manager.put("rest/secure/dwoadmin/garbage/user/remove", Boolean.class, rest);
    return result;
  }
}
