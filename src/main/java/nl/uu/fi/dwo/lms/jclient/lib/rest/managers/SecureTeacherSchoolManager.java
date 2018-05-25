package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SecureTeacherSchoolManager implements SchoolManager {
  private static final Logger LOG = Logger.getLogger(SecureTeacherSchoolManager.class.getName());

  public Boolean updateSchool(DomSchoolFull submit) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchoolFull(submit);
    Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/school/update", Boolean.class, rest);
    LOG.log(Level.FINE, "Updated data for school {1} by username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
}

}
