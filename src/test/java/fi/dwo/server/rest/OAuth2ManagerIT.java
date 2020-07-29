package fi.dwo.server.rest;

import static org.junit.Assert.*;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.rest.jaxrsfilters.AuthenticationRequestFilter;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class OAuth2ManagerIT {

  private static DatabaseManager instance = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
      Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
      DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
      instance = new DatabaseManager();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
      DwoEmfFactory.setDefaultEntityManagerFactory();
      instance = null;
  }

  OAuth2Manager manager;
  @Before
  public void setUp() throws Exception {
      instance.IntializeTestDatabase();
      manager = new OAuth2Manager();
  }

  @After
  public void tearDown() throws Exception {
      instance.ClearDatabase();
  }

  @Test
  public void testToken() {
    MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    TestSecurityContext sc = new TestSecurityContext("user01", RoleType.STUDENT);   
    SecuredUserAccountManager account = new SecuredUserAccountManager();
    account.loginUser(sc);
    String code = account.getBearerToken(sc);
    code = code.substring(1, code.length()-1);
    code = java.util.Base64.getEncoder().encodeToString(("2\f"+code).getBytes());
    
    params.putSingle("code", code);
    params.putSingle(manager.GRANT_TYPE, manager.AUTHORIZATION_CODE);
    
    Response response = manager.token(params, null, null);
    assertEquals(200, response.getStatus());
    DomToken token = (DomToken) response.getEntity();
    assertNotNull(token);
    assertNotNull(token.getAccess_token());
    AuthenticationRequestFilter filter = new AuthenticationRequestFilter();
    SecurityContext ctx = filter.validateJWTToken(token.getAccess_token(), sc);
    assertNotNull(ctx);
    assertEquals("user01", ctx.getUserPrincipal().getName());

    params.putSingle("refresh_token", token.getRefresh_token());
    params.putSingle(manager.GRANT_TYPE, "refresh_token");
    response = manager.token(params,null,null);
    assertNotNull(token);
    assertNotNull(token.getAccess_token());
    ctx = filter.validateJWTToken(token.getAccess_token(), sc);
    assertNotNull(ctx);
    assertEquals("user01", ctx.getUserPrincipal().getName());
 
  
  
  
  
  
  }

}
