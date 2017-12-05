package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.entities.RestDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SecureDwoAdminProfileManager {
    private static final Logger LOG = Logger.getLogger(SecureDwoAdminProfileManager.class.getName());

    public static List<DomDwoProfileFull> getProfiles() throws Dwo2Exception {
        List<DomDwoProfileFull> src;
        src = StoredRestManager.getInstance().getList("rest/secure/dwoadmin/profile/getList", RestListClassTypes.DomDwoProfile);
        LOG.log(Level.FINE, "Retrieved list of profiles for the dwoadmin with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

    public static Boolean updateProfile(DomDwoProfileFull profile) throws Dwo2Exception {
    	Boolean result = Boolean.FALSE;
    	RestDwoProfileFull rest = new RestDwoProfileFull();
    	rest.setDomDwoProfile(profile);
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
    	result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/profile/update", Boolean.class, rest);
    	return result;
    }
    
    public static Boolean submitProfile(DomDwoProfileFull profile) throws Dwo2Exception {
    	Boolean result = Boolean.FALSE;
    	RestDwoProfileFull rest = new RestDwoProfileFull();
    	rest.setDomDwoProfile(profile);
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
    	result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/profile/submit", Boolean.class, rest);
    	return result;
    }
    
    
}
