package nl.uu.fi.dwo.lms.jclient.rest.managers;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.OAuthManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DWO2ExceptionTranslatorInterface;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class OAuthManagerIT {
  private OAuthManager manager;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
        Dwo2ExceptionTranslator.setTranslator(new DWO2ExceptionTranslatorInterface() {

          @Override
          public String encodeJSON(Dwo2ExceptionCode code, String message) {
              // TODO Auto-generated method stub
              return message;
          }

          @Override
          public String decodeMessageInJSON(String json) {
              // TODO Auto-generated method stub
              return json;
          }

          @Override
          public Dwo2ExceptionCode decodeCodeInJSON(String json) {
              // TODO Auto-generated method stub
              return Dwo2ExceptionCode.Client_InternalError;
          }

          @Override
          public String getLocalizedCodeExplanation(DwoLocale locale, Dwo2ExceptionCode code) {
              return code.toString();
          }});
  }

  @Before
  public void setUp() throws Exception {
      StoredRestManager rest = StoredRestManager.getInstance();
      RestAuthenticator authenticator = rest.getAuthenticator();    
      authenticator.setServerUrlPath(new URL("http://localhost:8080/dwo/rest"));
      authenticator.setContext(new DomContext());
      manager = new OAuthManager();
  }

  @Test
  public void test() throws Dwo2Exception {
    String refresh = manager.client_credentials("peterb", "d79096188b670c2f81b7001f73801117");
    DomUserFull user = SecureUserAccountManager.getAccountData();
    assertEquals("peterb", user.getUserName());
    refresh = manager.refresh_token(refresh);
    user = SecureUserAccountManager.getAccountData();
    assertEquals("peterb", user.getUserName());
  }

}
