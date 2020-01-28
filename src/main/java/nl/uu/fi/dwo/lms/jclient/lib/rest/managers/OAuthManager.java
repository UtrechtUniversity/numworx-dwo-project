package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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

  
  public OAuthManager(StoredRestManager manager) {
    this.manager = manager;
  }
  
  public OAuthManager() {
    this(StoredRestManager.getInstance());
  }
  
  @SuppressWarnings("deprecation")
  public String authorization_token(String token) {
    try {
      URL url = new URL(manager.getServerUrlPath(), "rest/oauth2/token");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setAllowUserInteraction(false);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();
      String form = "grant_type=authorization_code&code="+URLEncoder.encode(token);
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
