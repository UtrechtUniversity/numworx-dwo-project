package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.mfa.MFA;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureUserMFAManager {

	public static MFA create() throws Dwo2Exception {
		RestContext rest = new RestContext();
	    StoredRestManager restManager = StoredRestManager.getInstance();
	    DomContext context = restManager.getContext();
		rest.setRestContext(context);
	    MFA mfa = restManager.put("rest/sec:" + PathId.getId(context) + "/user/mfa/create", MFA.class, rest);

	    return mfa;
	}
}
