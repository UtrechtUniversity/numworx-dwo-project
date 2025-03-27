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
	
	public static boolean verify(String code) throws Dwo2Exception {
		if (code == null || code.isEmpty()) return false;
		StoredRestManager restManager = StoredRestManager.getInstance();
		DomContext context = restManager.getContext();
		Boolean result = restManager.get("rest/sec:" + PathId.getId(context) + "/user/mfa/verify?mfa=" + code, Boolean.class);
		return result.booleanValue();
	}

	public static boolean remove() throws Dwo2Exception {
		RestContext rest = new RestContext();
	    StoredRestManager restManager = StoredRestManager.getInstance();
	    DomContext context = restManager.getContext();
		rest.setRestContext(context);
	    Boolean remove = restManager.put("rest/sec:" + PathId.getId(context) + "/user/mfa/disable", Boolean.class, rest);
	    return remove.booleanValue();
	}
}
