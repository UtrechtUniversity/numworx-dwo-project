package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;

/**
 *
 * @author Gert van der Plas
 */
public class RestAuthenticator extends Authenticator {

  private URL serverUrlPath;
  private DomContext context;
  private String username;
  private String password;
  private String realm;

  private static volatile RestAuthenticator instance;

  public RestAuthenticator(String username, String password, String realm) {
    setUsername(username);
    setPassword(password);
    setRealm(realm);
  }

  public RestAuthenticator() {}
  
  @Deprecated // Static class use is evil!
  public static RestAuthenticator getInstance() {
    return instance;
  }

  // @Deprecated //Static class use is evil!
  // public static void setInstance(RestAuthenticator instance) {
  // RestAuthenticator.instance = instance;
  // }

  static {
    instance = new RestAuthenticator();
    try {
		instance.setServerUrlPath(new URL("http://127.0.0.1:8080/dwo/"));
	} catch (MalformedURLException e) {
	}
  }

  // public RestAuthenticator(String username, String password) {
  // this.username = username;
  // this.password = password;
  //
  // }
  protected PasswordAuthentication GetPasswordAuthentication() {
    return new PasswordAuthentication(getUsername() + getRealm(), getPassword().toCharArray());
  }
  
  public String getBasicAuthentication() {
    if ( isAuthenticated()) {
      String u = username;
      if(u.endsWith("@"))
        u = u.substring(0, u.length()-1);
      else if (!u.contains("@"))
        u = u + realm;
      String authString = u + ":" + password;
      // note that reference changes in Java are atomic.
      return  "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
    }
    return null;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  public boolean isAuthenticated() {
    return password != null && username != null;
  }

  /**
   * @param password the password to set
   */
  void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the serverUrlPath
   */
  public URL getServerUrlPath() {
    return serverUrlPath;
  }

  /**
   * @param aServerUrlPath the serverUrlPath to set
   */
  public void setServerUrlPath(URL aServerUrlPath) {
    serverUrlPath = aServerUrlPath;
  }

  /**
   * @return the context
   */
  public DomContext getContext() {
    return context;
  }

  /**
   * @param context the context to set
   */
  public void setContext(DomContext context) {
    this.context = context;
  }

  /**
   * @return the realm
   */
  public String getRealm() {
    return realm;
  }

  /**
   * @param realm the realm to set
   */
  void setRealm(String realm) {
    if (realm == null) realm = "";
    this.realm = realm;
  }

}
