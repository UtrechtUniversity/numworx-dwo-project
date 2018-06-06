package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFromTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolFromTo;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SecureTeacherFromToManager {
  private static final Logger LOG = Logger.getLogger(SecureTeacherFromToManager.class.getName());

  public static Boolean set(DomSchoolFromTo submit) throws Dwo2Exception {
    RestSchoolFromTo rest = new RestSchoolFromTo();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setSchoolFromTo(submit);
    Boolean result =
        StoredRestManager.getInstance().put("rest/secure/teacher/fromto/set", Boolean.class, rest);
    LOG.log(Level.FINE, "Updated fromto data by username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return result;
  }

}
