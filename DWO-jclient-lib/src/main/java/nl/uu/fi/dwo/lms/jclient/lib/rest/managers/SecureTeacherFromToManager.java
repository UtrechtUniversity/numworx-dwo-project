package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFromTo;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolFromTo;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureTeacherFromToManager {
  private static final Logger LOG = Logger.getLogger(SecureTeacherFromToManager.class.getName());

  public static Boolean set(DomSchoolFromTo submit) throws Dwo2Exception {
    RestSchoolFromTo rest = new RestSchoolFromTo();
    rest.setRestContext(getContext());
    rest.setSchoolFromTo(submit);
    Boolean result =
        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/fromto/set", Boolean.class, rest);
    LOG.log(Level.FINE, "Updated fromto data by username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return result;
  }

  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public static List<DomCourse> getCourses(DomSchoolAndProfile dom)  throws Dwo2Exception {
    RestSchoolAndProfile rest = new RestSchoolAndProfile();
    rest.setRestContext(getContext());
    rest.setDomSchoolAndProfile(dom);
    RestListClassTypes type = RestListClassTypes.DomCourse;
    List<DomCourse> result =
      StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/fromto/getCourses", type, rest);
    return result;
  }
  
  public static DomSchoolFromTo get() throws Dwo2Exception {
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    DomSchoolFromTo result = 
        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/fromto/get", DomSchoolFromTo.class, rest);
    return result;
  }
  
  public static List<DomSchoolFrom> getExports() throws Dwo2Exception {
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    List<DomSchoolFrom> result = 
        StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/fromto/getExports", RestListClassTypes.DomSchoolFrom, rest);
    return result;
   
  }
  
}
