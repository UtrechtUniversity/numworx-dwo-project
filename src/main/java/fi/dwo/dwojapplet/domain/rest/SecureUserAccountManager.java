package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.dom.entities.DomSchool;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.RestFullUser;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Manages the user profile.
 * 
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountManager {
    private static final Logger LOG = Logger.getLogger(SecureUserAccountManager.class.getName());
    
    /**
    * Returns the current user 'logged in'. The information is extracted from the 
    * security context which depends on the credentials used for accessing the rest
    * interface. Technically it should be equal to the data in the DwoHelper.
    * 
    * @return 
     * @throws fi.dwo.commons.exceptions.Dwo2Exception 
    */
    public static DomFullUser getAccountData() throws Dwo2Exception{
        DomFullUser user;
        user = StoredRestManager.getInstance().get("/rest/secure/user/account/get", DomFullUser.class);
        return user;
    }   
    
    /**
     * Updates the user profile of a user. 
     * 
     * Fields updated are email, password and the full name of the user. The full
     * name exists out of the first, insertion and family name.
     * 
     * @param user
     * @return 
     * @throws fi.dwo.commons.exceptions.Dwo2Exception 
     */

    public static DomFullUser updateAccountData(DomFullUser user) throws Dwo2Exception {
            RestFullUser restUser = new RestFullUser();
            restUser.setRestContext(new DomContext());
            restUser.setDomFullUser(user);
            
            user = StoredRestManager.getInstance().put("/rest/secure/user/account/update", DomFullUser.class, restUser);
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest(user.getUsername(), user.getPassword()).build();
            Client client = ClientBuilder.newClient().register(feature);
            WebTarget target = client.target(DwoHelper.getServerUrlPath().toString());
            StoredRestManager.setWebTargetRest(target);
            
            DwoHelper.setCurrentUser(user);
            LOG.log(Level.FINE, "Updated user profile of username {0}.",new Object[]{restUser.getDomFullUser().getUsername()});
        return user;
    }
    
    
    /**
     * Updates the user profile of a user. 
     * 
     * Fields updated are email, password and the full name of the user. The full
     * name exists out of the first, insertion and family name.
     * 
     * @throws fi.dwo.commons.exceptions.Dwo2Exception 
     */

    public static Boolean removeAccountData() throws Dwo2Exception {
        Boolean b;
        b = StoredRestManager.getInstance().get("/rest/secure/user/account/remove", Boolean.class);
        return b;
    }

    public static DomSchool getNullSchool() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
