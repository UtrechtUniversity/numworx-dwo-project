/*Copyrighted 2015. */
package fi.dwo.dwojapplet.domain.rest;

import com.owlike.genson.Genson;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import java.util.logging.Logger;

/**
 * Handles basicLogin actions and updates user and role stored in the DwoHelper.
 * Should call a session password Manager in the future. Particular for
 * students.
 *
 * @author G.A.J. van der Plas
 */
public class LoginManager {

    private static final Logger LOG = Logger.getLogger(LoginManager.class.getName());

    /**
     * Does basic login for authentication. To be replaced by digest.
     *
     * @param username
     * @param password
     * @return
     * @throws Dwo2Exception
     * @deprecated
     */
    @Deprecated
    public static DomUserFull basicLogin(String username, String password) throws Dwo2Exception {
        //login to rest service, note there is usually not yet be a fully configured StoredRestManager.
        DomUserFull user;
// Should clear any existing autentication cache but does not work due to feature bug.
        try {
            //RestAuthenticator restAuth = RestAuthenticator.(username,password);
            Authenticator.setDefault(new RestAuthenticator(username, password));
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + "rest/secure/user/account/get"); //TODO make basicLogin            
            String authString = username + ":" + password;
            authString = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("Authorization", authString);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.connect();

            if (conn.getResponseCode() != 200) {
                throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            conn.disconnect();
            Authenticator.setDefault(null);
            //decode JSON
            Genson genson = new Genson();

//          LIST EXAMPLE: List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            user = genson.deserialize(json.toString(), DomUserFull.class);
            StoredRestManager.setBasicAuthString(authString);
            //Set current user for domain
            DwoHelper.setCurrentUser(user);
            return user;
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

        } catch (IOException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Server error");
        }
    }

}
