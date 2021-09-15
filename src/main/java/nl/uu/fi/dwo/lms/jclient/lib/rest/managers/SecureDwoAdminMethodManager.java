package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureDwoAdminMethodManager {
	  private static final Logger LOG =
		      Logger.getLogger(SecureDwoAdminMethodManager.class.getName());

		  public static List<DomMethod> getList() throws Dwo2Exception {
		    RestContext rest = new RestContext();
		    rest.setRestContext(getContext());
		    List<DomMethod> src =
		        StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/method/getList",
		            RestListClassTypes.DomMethod, rest);
		    LOG.log(Level.FINE, "Retrieved list of methods of the teacher with username {0}.",
		        new Object[] {authenticator().getUsername()});
		    return src;
		  }  

		  static RestAuthenticator authenticator() {
			  return StoredRestManager.getInstance().getAuthenticator();
		  }
		  
		  static DomContext getContext() {
		    return authenticator().getContext();
		  }

}
