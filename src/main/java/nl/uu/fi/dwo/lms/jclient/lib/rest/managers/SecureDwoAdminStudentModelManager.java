package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureDwoAdminStudentModelManager implements SecureStudentModelManager {
	private static final Logger LOG =
		      Logger.getLogger(SecureDwoAdminStudentModelManager.class.getName());

	private final StoredRestManager restManager = StoredRestManager.getInstance();

	@Override
	public List<DomStudentModelContext> getReducedList() throws Dwo2Exception {
	    RestContext rest = new RestContext();
	    rest.setRestContext(getContext());
	    List<DomStudentModelContext> src =
	        restManager.getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/studentmodel/getReducedList",
	            RestListClassTypes.DomStudentModelContext, rest);
	    LOG.log(Level.FINE, "Retrieved list of studentmodels of the dwoadmin with username {0}.",
	        new Object[] {restManager.getAuthenticator().getUsername()});
	    return src;
	}

	private DomContext getContext() {
		    return restManager.getAuthenticator().getContext();
	}

}
