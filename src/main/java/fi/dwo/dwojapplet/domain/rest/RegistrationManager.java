package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class RegistrationManager {

    private static final Logger LOG = Logger.getLogger(RegistrationManager.class.getName());

    public static boolean RegisterNewUser(NewUserRegistration newUserReg) throws Dwo2RestException {
        boolean r;
        r = StoredRestManager.getInstance().put("/rest/public/registration/newUser/json", Boolean.class, newUserReg);
                    todo return Dwo2Exception on fail

        return r;
    }
    
    /**
     *
     * @param existingUserReg
     * @return
     */
    public static boolean RegisterExistingUser(NewUserRegistration existingUserReg) {
        boolean r;
        //login to rest service, note there is usually not yet be a fully configured StoredRestManager.
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest(existingUserReg.getUsername(), existingUserReg.getPassword()).build();
        Client client = ClientBuilder.newClient().register(feature);
        
        Response response = client.target(DwoHelper.getBaseServletUrlString())
                .path("/rest/secure/registration/existingUser/json")
                .request().put(Entity.entity(existingUserReg, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            // failed login
            todo return Dwo2Exception
            LOG.log(Level.INFO, "Status {0}, Reason {1}, entity {2}, for user {3}.", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase(), response.getEntity(), existingUserReg.getUsername()});
            return false;
        } else {
            //Set return value
           r = response.readEntity(Boolean.class);
            // succeeded login
            LOG.log(Level.INFO, "Registered new role for username {0}.", new Object[]{existingUserReg.getUsername()});
        }
        return r;
    }

}
