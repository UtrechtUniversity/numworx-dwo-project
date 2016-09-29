package fi.dwo.dwojapplet.domain.rest;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.RestListClassTypes;
import fi.dwo.rest.dom.entities.DomAppletConfig;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.entities.RestAppletConfig;
import fi.dwo.rest.exceptions.Dwo2Exception;

public class SecureDwoAdminConfigManager {
    private static final Logger LOG = Logger.getLogger(SecureDwoAdminConfigManager.class.getName());

    public static List<DomAppletConfig> getConfigurations(Locale locale) throws Dwo2Exception {
        List<DomAppletConfig> src;
        src = StoredRestManager.getInstance().getList("rest/secure/dwoadmin/config/getList/"+locale, RestListClassTypes.DomAppletConfig);
        LOG.log(Level.FINE, "Retrieved list of appletconfigs for the dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static Boolean updateConfig(DomAppletConfig config) throws Dwo2Exception {
    	Boolean result = Boolean.FALSE;
    	RestAppletConfig restProfile = new RestAppletConfig();
    	restProfile.setDomAppletConfig(config);
    	restProfile.setRestContext(new DomContext());
    	result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/config/update", Boolean.class, restProfile);
    	return result;
    }
    
    public static Boolean submitConfig(DomAppletConfig profile) throws Dwo2Exception {
    	Boolean result = Boolean.FALSE;
    	RestAppletConfig restProfile = new RestAppletConfig();
    	restProfile.setDomAppletConfig(profile);
    	restProfile.setRestContext(new DomContext());
    	result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/config/submit", Boolean.class, restProfile);
    	return result;
    }
    
    public static Boolean removeConfig(DomAppletConfig profile) throws Dwo2Exception {
    	Boolean result = Boolean.FALSE;
    	RestAppletConfig restConfig = new RestAppletConfig();
    	restConfig.setDomAppletConfig(profile);
    	restConfig.setRestContext(new DomContext());
    	result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/config/remove", Boolean.class, restConfig);
    	return result;
    }
 
    
}
