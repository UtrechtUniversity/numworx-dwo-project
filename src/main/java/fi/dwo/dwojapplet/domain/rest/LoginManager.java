/*Copyrighted 2015. */
package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.HttpAuthenticationType;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Handles login actions and updates user and role stored in the DwoHelper. Should
 * call a session password Manager in the future. Particular for students.
 *
 * @author G.A.J. van der Plas
 */
public class LoginManager {

    private static final Logger LOG = Logger.getLogger(LoginManager.class.getName());

    public static DomFullUser login(String username, String password) throws Dwo2Exception {
        //login to rest service, note there is usually not yet be a fully configured StoredRestManager.
        DomFullUser user;
        HttpAuthenticationFeature feature=null;
        switch(DwoHelper.getHttpAuthentication()){
            case BASIC:
                feature = HttpAuthenticationFeature.universalBuilder().credentialsForBasic(username, password).build();
                break;
            case DIGEST:
                feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest(username, password).build();
        }
        Client client = ClientBuilder.newClient().register(feature);
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        cache.setNoStore(true);

        Response response = client.target(DwoHelper.getServerUrlPath().toString())
                .path("/rest/secure/user/account/get")
                .request().cacheControl(cache).get(Response.class);
        if (response.getStatus() != 200) {
            // failed login
            throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, response.getStatusInfo().getReasonPhrase());
        } else {
            //Set return value
            user = response.readEntity(DomFullUser.class);
            // succeeded login
            LOG.log(Level.INFO, "Logged in with username {0}.", new Object[]{user.getUserName()});
            //Set webtarget with credentials for future rest login.
            WebTarget target = client.target(DwoHelper.getServerUrlPath().toString());
            StoredRestManager.setWebTargetRest(target);
            //Set current user for domain
            DwoHelper.setCurrentUser(user);
        }
        return user;
    }
}
