/* Copyrighted 2015. */
package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import com.owlike.genson.Genson;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles basicLogin actions and updates user and role stored in the DwoHelper. Should call a
 * session password Manager in the future. Particular for students.
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
  public static DomUserFullwLoginContext basicLogin(String username, String password)
      throws Dwo2Exception {
    // login to rest service, note there is usually not yet be a fully configured StoredRestManager.
    DomUserFullwLoginContext user;
    // Should clear any existing autentication cache but does not work due to feature bug.
    try {
      // RestAuthenticator restAuth = RestAuthenticator.(username,password);
      // clears any auth data and cookies remaining from a previous session in Java browser-like
      // framework
      Authenticator.setDefault(null);
      CookieManager.setDefault(null);
      StoredRestManager restManager = StoredRestManager.getInstance();
      URL url = new URL(restManager.getServerUrlPath(), "rest/secure/user/account/login"); // TODO
                                                                                           // make
                                                                                           // basicLogin
      String authString = username + ":" + password;
      authString = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Authorization", authString);
      conn.addRequestProperty("Cookie", "");
      conn.setUseCaches(false);
      conn.setAllowUserInteraction(false);
      conn.connect();

      if (conn.getResponseCode() != 200) {
        throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError,
            conn.getResponseMessage());
      }

      BufferedReader br = new BufferedReader(
          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

      String output;
      StringBuilder json = new StringBuilder();
      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      br.close();
      conn.disconnect();
      // Authenticator.setDefault(null);
      // decode JSON
      Genson genson = restManager.getGenson();

      // LIST EXAMPLE: List<DomUserFull> user = genson.deserialize(json.toString(), new
      // GenericType<List<DomUserFull>>(){});
      user = genson.deserialize(json.toString(), DomUserFullwLoginContext.class);
      // initialize authenticated services
      restManager.setBasicAuthString(authString);
      // turn isAuthenticated into true
      restManager.getAuthenticator().setUsername(username);
      restManager.getAuthenticator().setPassword(password);
      // Set current user for domain
      return user;
    } catch (MalformedURLException e) {
      String msg = "Malformed URL error.";
      LOG.log(Level.WARNING, msg, e);
      throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");
    } catch (ConnectException e) {
      String msg = "Connection error, cannot connect to port.";
      LOG.log(Level.WARNING, msg, e);
      throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, msg);
    } catch (IOException e) {
      String msg = "IO-error,failed or interrupted connection to server.";
      LOG.log(Level.WARNING, msg, e);
      throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
    }
  }

}
