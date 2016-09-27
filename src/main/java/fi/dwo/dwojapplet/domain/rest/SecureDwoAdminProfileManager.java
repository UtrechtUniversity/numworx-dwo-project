package fi.dwo.dwojapplet.domain.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.RestListClassTypes;
import fi.dwo.rest.dom.entities.DomDwoProfile;
import fi.dwo.rest.exceptions.Dwo2Exception;

public class SecureDwoAdminProfileManager {
    private static final Logger LOG = Logger.getLogger(SecureDwoAdminProfileManager.class.getName());

    public static List<DomDwoProfile> getProfiles() throws Dwo2Exception {
        List<DomDwoProfile> src;
        src = StoredRestManager.getInstance().getList("rest/secure/dwoadmin/profile/getList", RestListClassTypes.DomDwoProfile);
        LOG.log(Level.FINE, "Retrieved list of profiles for the dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }
    
}
