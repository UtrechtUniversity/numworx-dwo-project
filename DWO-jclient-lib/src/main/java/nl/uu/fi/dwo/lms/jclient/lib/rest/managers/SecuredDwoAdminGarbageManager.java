package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredDwoAdminGarbageManager {

  private final StoredRestManager manager;

  public SecuredDwoAdminGarbageManager(StoredRestManager manager) {
    this.manager = manager;
  }
  
  public List<DomUserFullwLoginContext> getUsers(Integer amount, Long since) throws Dwo2Exception {
	  return getUsers(amount, since, null);
  }
  
  public List<DomUserFullwLoginContext> getUsers(Integer amount, Long since, Boolean single) throws Dwo2Exception {
    List<DomUserFullwLoginContext> result = null;
    String query = "";
    if (amount != null) {
      query = "?limit=" + amount;
    }
    if (since != null) {
      if (query.isEmpty()) query = "?"; else query += "&";
      query += "before=" + since;
    }
    if (single != null) {
        if (query.isEmpty()) query = "?"; else query += "&";
        query += "single=" + single;
    }
    	
    String path = "rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/user/get" + query;
    RestListClassTypes type = RestListClassTypes.DomUserFullwLoginContext;
    result = manager.getList(path, type);    
    return result;
  }

  public List<DomSchool4DwoAdmin> getSchools(Integer amount, Long since) throws Dwo2Exception {
	    List<DomSchool4DwoAdmin> result = null;
	    String query = "";
	    if (amount != null) {
	      query = "?limit=" + amount;
	    }
	    if (since != null) {
	      if (query.isEmpty()) query = "?"; else query += "&";
	      query += "before=" + since;
	    }
	    String path = "rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/school/get" + query;
	    RestListClassTypes type = RestListClassTypes.DomSchool4DwoAdmin;
	    result = manager.getList(path, type);    
	    return result;
	  }

  
  
  public Boolean removeUser(DomUser user) throws Dwo2Exception {
    RestUser rest = new RestUser();
    rest.setDomUser(user);
    rest.setRestContext(getContext());
    Boolean result;
    result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/user/remove", Boolean.class, rest);
    return result;
  }

  public List<DomLoginContext> getContexts(Integer amount) throws Dwo2Exception {
    List<DomLoginContext> result = null;
    String query = "";
    if (amount != null) {
      query = "?limit=" + amount;
    }
    String path = "rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/context/get" + query;
    RestListClassTypes type = RestListClassTypes.DomLoginContext;
    result = manager.getList(path, type);    
    return result;
  }
  
  public Boolean removeContext(DomLoginContext user) throws Dwo2Exception {
    RestLoginContext rest = new RestLoginContext();
    rest.setDomLoginContext(user);
    rest.setRestContext(getContext());
    Boolean result;
    result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/context/remove", Boolean.class, rest);
    return result;
  }

  public List<DomClassCourse> getClassCourses(Integer amount) throws Dwo2Exception {
	    List<DomClassCourse> result = null;
	    String query = "";
	    if (amount != null) {
	      query = "?limit=" + amount;
	    }
	    String path = "rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/classcourse/get" + query;
	    RestListClassTypes type = RestListClassTypes.DomClassCourse;
	    result = manager.getList(path, type);    
	    return result;

  }
  
  public Boolean removeClassCourse(DomClassCourse cc) throws Dwo2Exception {
	    RestClassCourse rest = new RestClassCourse();
	    rest.setDomClassCourse(cc);
	    rest.setRestContext(getContext());
	    Boolean result;
	    result = manager.put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/garbage/classcourse/remove", Boolean.class, rest);
	    return result;
	  }

  
  
  private DomContext getContext() {
    return manager.getContext();
  }
}
