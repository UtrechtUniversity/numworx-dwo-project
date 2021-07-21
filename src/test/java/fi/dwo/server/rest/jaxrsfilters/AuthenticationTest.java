package fi.dwo.server.rest.jaxrsfilters;

import static org.junit.Assert.*;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.testutil.TestSecurityContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class AuthenticationTest {

  class MockAuthenticationRequestFilter extends AuthenticationRequestFilter {

    int keyuse, expectedkeyuse = 1;
    
    public void verify() {
     assertEquals("keyuse", expectedkeyuse, keyuse);
      
    }

    @Override
    protected PersistentUser findByUsername(String username) {
      PersistentUser u = new PersistentUser();
      u.setUsername(username);
      return u;
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
      assertEquals("1", header.getKeyId());
      keyuse++;
      return key;
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, String plaintext) {
      assertEquals("1", header.getKeyId());
      keyuse++;
      return key;
    }

	@Override
	protected
	Object getAttribute(String key) {
		return null;
	}
    
  }
  
  MockAuthenticationRequestFilter mock;
  Key key;
 
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    mock = new MockAuthenticationRequestFilter();
    byte[] bytes = new byte[32];
    key = Keys.hmacShaKeyFor(bytes);
 }

  @After
  public void tearDown() throws Exception {
    mock.verify();
  }

  @Test
  public void testJWT() {
    String token = 
    Jwts.builder()
      .setAudience(RoleType.TEACHER.name())
      .setExpiration(new Date(System.currentTimeMillis() + 10000L))
      .setHeaderParam("kid", 1L)
      .setSubject("user01")
      .signWith(key)
      .compact();

    SecurityContext ctx = new TestSecurityContext("anonymous", RoleType.ANONYMOUS);
    ctx = mock.validateJWTToken(token, ctx);
    assertTrue("teacher", ctx.isUserInRole(RoleType.TEACHER.name()));
    assertEquals("user01", ctx.getUserPrincipal().getName());
  }

  @Test
  public void testJWT1() {
    String token = 
    Jwts.builder()
      .setAudience(RoleType.TEACHER.name())
      .setHeaderParam("kid", 1L)
      .setSubject("user01")
      .setExpiration(new Date(System.currentTimeMillis() - 10000L))
      .signWith(key)
      .compact();

    SecurityContext ctx = new TestSecurityContext("anonymous", RoleType.ANONYMOUS);
    ctx = mock.validateJWTToken(token, ctx);
    assertNull("expired", ctx);
  }

  @Test
  public void testJWT2() {
    String token = 
    Jwts.builder()
      .setAudience(RoleType.TEACHER.name())
      .setHeaderParam("kid", "1")
      .setSubject("user01")
      .setExpiration(new Date(System.currentTimeMillis() + 10000L))
      .compact();
    mock.expectedkeyuse = 0;
    SecurityContext ctx = new TestSecurityContext("anonymous", RoleType.ANONYMOUS);
    ctx = mock.validateJWTToken(token, ctx);
    assertNull("unsigned", ctx);
   }

  @Test
  public void testJWT3() {
    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    System.out.println(Arrays.toString(key.getEncoded()));
    String token = 
    Jwts.builder()
      .setAudience(RoleType.TEACHER.name())
      .setExpiration(new Date(System.currentTimeMillis() + 10000L))
      .setHeaderParam("kid", 1L)
      .setSubject("user01")
      .signWith(key)
      .compact();

    SecurityContext ctx = new TestSecurityContext("anonymous", RoleType.ANONYMOUS);
    ctx = mock.validateJWTToken(token, ctx);
    assertNull("wrong key", ctx);
   }

  
}
