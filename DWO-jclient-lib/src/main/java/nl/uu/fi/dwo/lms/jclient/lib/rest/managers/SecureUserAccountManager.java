package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import com.owlike.genson.Genson;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.PathId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountManager {

  private static final Logger LOG = Logger.getLogger(SecureUserAccountManager.class.getName());

  /**
   * Returns the current user 'logged in'. The information is extracted from the security context
   * which depends on the credentials used for accessing the rest interface. Technically it should
   * be equal to the data in the DwoHelper.
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static DomUserFull getAccountData() throws Dwo2Exception {
	return getAccountData(StoredRestManager.getInstance());
}

/**
   * Returns the current user 'logged in'. The information is extracted from the security context
   * which depends on the credentials used for accessing the rest interface. Technically it should
   * be equal to the data in the DwoHelper.
 * @param instance restmanager
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static DomUserFull getAccountData(StoredRestManager instance) throws Dwo2Exception {
    DomUserFull user;
    DomContext context = instance.getContext();
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    user = instance.put("rest/sec:" + PathId.getId(context) + "/user/account/get", DomUserFull.class, rest);
    return user;
  }

  public static DomLoginContext getLoginContext(String username, String password, String realm)
      throws Dwo2Exception {
    DomLoginContext loginContext;
    Authenticator.setDefault(null);
    try {
      URL url = new URL(StoredRestManager.getInstance().getServerUrlPath().toString()
          + "rest/secure/user/account/getLoginContext"); // TODO make basicLogin
      
      String authString = new RestAuthenticator(username, password, realm).getBasicAuthentication();
      
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
      Genson genson = StoredRestManager.getInstance().getGenson();

      // LIST EXAMPLE: List<DomUserFull> user = genson.deserialize(json.toString(), new
      // GenericType<List<DomUserFull>>(){});
      loginContext = genson.deserialize(json.toString(), DomLoginContext.class);
      return loginContext;
    } catch (MalformedURLException e) {
      throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

    } catch (IOException e) {
      if (e.getClass().equals(java.net.ConnectException.class)) {
        throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, e.getMessage());
      } else {
        throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
      }
    }


  }

  // /**
  // * Login for a user. Registers service that the user is logging in. As the
  // * REST interface is stateless this is merely for gathering statistics.
  // *
  // * @return
  // * @throws fi.dwo.rest.exceptions.Dwo2Exception
  // */
  // public static DomUserFull loginUser() throws Dwo2Exception {
  // DomUserFull user;
  // user = StoredRestManager.getInstance().get("rest/secure/user/account/login",
  // DomUserFull.class);
  // return user;
  // }

  /**
   * Registers that the user logs out. When doing basic authentication basicAuthLogout is
   * recommended to be used.
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static Boolean logoutUser(DomLoginContext domLoginContext) throws Dwo2Exception {
	  return logoutUser(StoredRestManager.getInstance(), domLoginContext);
  }
	  
  public static Boolean logoutUser(StoredRestManager restManager,  DomLoginContext domLoginContext) throws Dwo2Exception {
    Boolean result;
    RestLoginContext submit = new RestLoginContext();
    DomContext context = restManager.getContext();
    submit.setRestContext(context);
    submit.setDomLoginContext(domLoginContext);
    try {
		result = restManager.put("rest/sec:" + PathId.getId(context) + "/user/account/logout", Boolean.class, submit);
	} finally {
    // ensures basic auth data and cookies are wiped from Java Browser-like framework
	    Authenticator.setDefault(null);
	    restManager.setBasicAuthString(null, null, null);
	    restManager.setRecover(null);
	}
    return result;
  }

  /**
   * Registers that the user logs out, clears client-side cookies and authenticator data, and clears
   * the session server-side.
   *
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static Boolean basicAuthLogout(DomLoginContext domLoginContext) throws Dwo2Exception {
    Boolean result;
    StoredRestManager restManager = StoredRestManager.getInstance();
    RestLoginContext submit = new RestLoginContext();
    DomContext context = restManager.getContext();
    submit.setRestContext(context);
    submit.setDomLoginContext(domLoginContext);
    result = restManager.put("rest/sec:" + PathId.getId(context) + "/user/account/basicAuthLogout", Boolean.class, submit);
    // ensures basic auth data and cookies are wiped from Java Browser-like framework
    Authenticator.setDefault(null);
    restManager.setBasicAuthString(null,null,null);
    return result;
  }

  /**
   * Updates the user profile of a user.
   *
   * Fields updated are email, password and the full name of the user. The full name exists out of
   * the first, insertion and family name.
   *
   * @param user
   * @return
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static DomUserFull updateAccountData(DomUserFull user) throws Dwo2Exception {
    RestUserFull restUser = new RestUserFull();
    StoredRestManager restManager = StoredRestManager.getInstance();
    DomContext context = restManager.getContext();
	restUser.setRestContext(context);
    restUser.setDomUserFull(user);
    user = restManager.put("rest/sec:" + PathId.getId(context) + "/user/account/update", DomUserFull.class, restUser);
    restManager.setBasicAuthString(null,null,null);
    LOG.log(Level.FINE, "Updated user profile of username {0}.",
        new Object[] {restUser.getDomUserFull().getUserName()});
    return user;
  }

  /**
   * Updates the user profile of a user.
   *
   * Fields updated are email, password and the full name of the user. The full name exists out of
   * the first, insertion and family name.
   *
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  public static Boolean removeAccountData() throws Dwo2Exception {
    Boolean b;
    StoredRestManager restManager = StoredRestManager.getInstance();
    DomContext context = restManager.getContext();
    b = StoredRestManager.getInstance().get("rest/sec:" + PathId.getId(context) + "/user/account/remove", Boolean.class);
    return b;
  }

  public static Boolean link_saml(String userid, String orgid, String token) throws Dwo2Exception {
    Boolean b;
    DomContext context = StoredRestManager.getInstance().getContext();
    DomSamlUser saml = new DomSamlUser();
    saml.setSamlOrgId(orgid);
    saml.setSamlUserId(userid);
    saml.setAuthToken(token);
    RestSamlUser rest = new RestSamlUser();
    rest.setDomSamlUser(saml);
    rest.setRestContext(context);
    b = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(context) + "/user/account/linkSaml", Boolean.class,
        rest);
    return b;
  }

  public static DomLoginContext getLoginContext(StoredRestManager instance) throws Dwo2Exception {
	    DomLoginContext context;
	    context = instance.get("rest/secure/user/account/getLoginContext", DomLoginContext.class);
	    return context;
	  }
   
  public static DomLoginContext getLoginContext() throws Dwo2Exception {
    DomLoginContext context;
    context = StoredRestManager.getInstance().get("rest/secure/user/account/getLoginContext", DomLoginContext.class);
    return context;
  }
  
  public static String getBearerToken() throws Dwo2Exception {
    StoredRestManager restManager = StoredRestManager.getInstance();
    return getBearerToken(restManager);
  }

  public static String getBearerToken(StoredRestManager restManager) throws Dwo2Exception {
	String b;
	DomContext context = restManager.getContext();
    b = restManager.get("rest/sec:" + PathId.getId(context) + "/user/account/getBearerToken", String.class);
    b = java.util.Base64.getEncoder().encodeToString(("2\f"+b).getBytes());
    return b;
}

}
