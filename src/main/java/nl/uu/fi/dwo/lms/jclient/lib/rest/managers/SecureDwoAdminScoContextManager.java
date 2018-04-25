package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull4DwoAdmin;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * CRUD for teachers on courses.
 * @author wim
 *
 */
public class SecureDwoAdminScoContextManager {
    private static final Logger LOG = Logger.getLogger(SecureDwoAdminScoContextManager.class.getName());

    /** Update a course. Not all fields are updatable!
     * @param edit the course
     * @return the edited course
    */
    public static DomScoContextFull update(DomScoContextFull edit, DomScoData data, DomDwoProfile dwoProfile) throws Dwo2Exception {
    	RestScoContextFull4DwoAdmin rest = new RestScoContextFull4DwoAdmin();
    	rest.setDomScoContext(edit);
    	rest.setDomScoData(data);
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
    	rest.setDomDwoProfile(dwoProfile);
    	rest.setDelete(Boolean.TRUE);
        DomScoContextFull result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/scoContext/update",DomScoContextFull.class, rest);
        LOG.log(Level.FINE, "Updated sco for the dwoadmin with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return result;
    }

    public static DomScoContextFull UNSAFEupdate(DomScoContextFull edit, DomScoData data, DomDwoProfile dwoProfile) throws Dwo2Exception {
      RestScoContextFull4DwoAdmin rest = new RestScoContextFull4DwoAdmin();
      rest.setDomScoContext(edit);
      rest.setDomScoData(data);
      rest.setRestContext(RestAuthenticator.getInstance().getContext());
      rest.setDomDwoProfile(dwoProfile);
      rest.setDelete(Boolean.FALSE);
      DomScoContextFull result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/scoContext/update",DomScoContextFull.class, rest);
      LOG.log(Level.FINE, "Updated sco for the dwoadmin with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
      return result;
  }

	public static DomScoContextFull add(DomScoContextFull edit, DomScoData data, DomDwoProfile dwoProfile) throws Dwo2Exception {
    	RestScoContextFull rest = new RestScoContextFull();
    	rest.setDomScoContext(edit);
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
    	rest.setDomScoData(data);
    	rest.setDomDwoProfile(dwoProfile);
        DomScoContextFull result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/scoContext/add",DomScoContextFull.class, rest);
        LOG.log(Level.FINE, "Added sco for the dwoadmin with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return result;
	}
}
