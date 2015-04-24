/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Handles login actions and updates user and role stored in the DwoHelper.
 *
 * @author G.A.J. van der Plas
 */
public class LoginManager {

    private static final Logger log = Logger.getLogger(LoginManager.class.getName());

    public static PersistentUser login(String username, String password) {
        //login to rest service, note there is usually not yet be a fully configured StoredRestManager.
        PersistentUser user;
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest(username, password).build();
        Client client = ClientBuilder.newClient().register(feature);
        
        Response response = client.target(DwoHelper.getBaseServletUrlString())
                .path("/rest/secure/user/userprofile/get/json")
                .request().get(Response.class);
        if (response.getStatus() != 200) {
            // failed login
            System.out.println("Code: " + response.getStatus() + ". Reason: " + response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            //Set return value
           user = response.readEntity(PersistentUser.class);
            // succeeded login
            log.log(Level.INFO, "Logged in with username {0}.", new Object[]{user.getUsername()});
            //Set webtarget with credentials for future rest login.
            WebTarget target = client.target(DwoHelper.getBaseServletUrlString());
            StoredRestManager.setWebTargetRest(target);
            //Set current user for domain
            DwoHelper.setCurrentUser(user);
        }
        return user;
    }
}
