package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.owlike.genson.Genson;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public class OAuthManager {

  StoredRestManager manager;
  private static final Logger LOG = Logger.getLogger(OAuthManager.class.getName());
  private URL url;

  
  public OAuthManager(StoredRestManager manager) {
    this.manager = manager;
    try {
      url = new URL(manager.getServerUrlPath(), "rest/oauth2/token");
    } catch (MalformedURLException e) {
    }
  }
  
  public OAuthManager() {
    this(StoredRestManager.getInstance());
  }
  
  public String authorization_token(String token, String clientId, String verifier, String redirectUri) {
	String pfx = "grant_type=authorization_code&code=" ;

	if (redirectUri != null) {
		pfx = "redirect_uri=" + encode(redirectUri) + "&" + pfx;
	}
	if (verifier != null) {
		pfx = "code_verifier=" + encode(verifier) + "&" + pfx;
	}
	if (clientId != null) {
		pfx = "client_id=" + encode(clientId) + "&" + pfx;
	}
	return requestToken(token, pfx);
  }

  public String refresh_token(String token) {
    return requestToken(token, "grant_type=refresh_token&refresh_token=");
  }
  
  public String client_credentials(String client, String secret) {
    try {
      String form = "grant_type=client_credentials&client_id=" + URLEncoder.encode(client, "UTF-8") +
          "&client_secret=" + URLEncoder.encode(secret, "UTF-8");
      return requestForm(form);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }
  

  private String requestToken(String token, String format) {
    String form = format+encode(token);
    return requestForm(form);
  }

  @SuppressWarnings("deprecation")
  private String encode(String token) {
	return URLEncoder.encode(token);
  }

  private String requestForm(String form) {
    try {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setAllowUserInteraction(false);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();
      out.write(form.getBytes(StandardCharsets.US_ASCII));
      out.close();
      if (conn.getResponseCode() == 200) {
        BufferedReader br = new BufferedReader(
          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

      String output;
      StringBuilder json = new StringBuilder();
      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      conn.disconnect();
      // decode JSON
      Genson genson = manager.getGenson();
      DomToken response = genson.deserialize(json.toString(), DomToken.class);
      manager.setBearerAuthString(response.getAccess_token());
      return response.getRefresh_token();
      } 
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "rest error", e);
    } 
    return null;
  }
  
  
  
  
}
