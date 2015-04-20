/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Handles login actions. 
 * 
 * 
 * @author G.A.J. van der Plas
 */
public class LoginManager {
    private static final Logger log = Logger.getLogger(LoginManager.class.getName());
    
    public static PersistentUser login(String username, String password){
        PersistentUser user;
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest(username,password).build();
        Client client = ClientBuilder.newClient().register(feature);

        Response response = client.target("http://localhost:8080")
                .path("/DWO/DWOServer/rest/secure/gui/panels/userprofile/update/json")
                .request().get();
        if (response.getStatus() != 200) {
            System.out.println("Code: " + response.getStatus() + ". Reason: " + response.getStatusInfo().getReasonPhrase());
            return null;
        } else {
            user = (PersistentUser) response.getEntity();
            log.log(Level.INFO, "Logged in with username {0}.",new Object[]{user.getUsername()});
        }
        return user;
    }
}
