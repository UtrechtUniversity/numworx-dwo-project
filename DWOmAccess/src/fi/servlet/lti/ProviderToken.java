package fi.servlet.lti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uoc.elc.lti.exception.BadToolProviderConfigurationException;

public class ProviderToken  {
  private static final Logger LOG = Logger.getLogger(ProviderToken.class.getName());
  private ProviderInfo info;
  public static void main(String[] args) {
    ProviderToken token = new ProviderToken();
    token.info = ProviderInfo.get("http://localhost:9001/");    
    System.out.println(token.getToken());
  }

  public String getToken() {
    
    try {
      return info.tool.getAccessToken().getAccessToken();
    } catch (BadToolProviderConfigurationException | IOException e) {
      LOG.log(Level.SEVERE, "fail", e);
    }
    return null;
  }

}
