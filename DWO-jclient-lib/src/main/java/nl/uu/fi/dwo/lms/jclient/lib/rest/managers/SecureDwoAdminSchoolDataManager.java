package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolDataFull;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.entities.RestSchoolDataFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureDwoAdminSchoolDataManager  {

  private static final Logger LOG = Logger.getLogger(SecureDwoAdminSchoolDataManager.class.getName());

  public static DomSchoolDataFull get(DomSchool submit) throws Dwo2Exception {
    RestSchool rest = new RestSchool();
    rest.setRestContext(getContext());
    rest.setDomSchool(submit);
    DomSchoolDataFull result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/schooldata/get",
        DomSchoolDataFull.class, rest);
    LOG.log(Level.FINE, "Retrieved full schooldata with login {1} for dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomSchool().getId()});
    return result;
  }
   
  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public static DomSchoolDataFull update(DomSchoolDataFull submit) throws Dwo2Exception {
    RestSchoolDataFull rest = new RestSchoolDataFull();
    rest.setRestContext(getContext());
    rest.setData(submit);
    DomSchoolDataFull result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/schooldata/update",
    		DomSchoolDataFull.class, rest);
    LOG.log(Level.FINE, "Updated data for school {1} by username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
  }

  public static Boolean remove(DomSchoolDataFull submit) throws Dwo2Exception {
    RestSchoolDataFull rest = new RestSchoolDataFull();
    rest.setRestContext(getContext());
    rest.setData(submit);

    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/schooldata/remove",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted school {1} for removal by user with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
  }

}
