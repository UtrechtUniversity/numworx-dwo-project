package fi.dwo.server.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.rest.jaxrsfilters.AuthenticationRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.oauth.ErrorResponse;
import nl.uu.fi.dwo.rest.security.TOTP;

@Path("/oauth2")
public class OAuth2Manager {
  static final String AUTHORIZATION_CODE = "authorization_code";
  static final String REFRESH_TOKEN = "refresh_token";
  static final String GRANT_TYPE = "grant_type";
  static final String CODE = "code";
  private static final Logger LOG = Logger.getLogger(OAuth2Manager.class.getName());
  private static int expires = 3600*3;
  private static final AuthenticationRequestFilter AUTH = new AuthenticationRequestFilter();
  
  private String access_token(PersistentUser u, PersistentLoginContext c, String scope) {
    SecretKey key;
    key = getKey(c);
    JwtBuilder builder = Jwts.builder();
    if (u.isSingleSchoolAccount()) builder = builder.setAudience(RoleType.STUDENT.name());
    if (scope != null) builder = builder.claim("scope", scope);
    String token = 
       builder
      .setExpiration(new Date(System.currentTimeMillis() + expires*1000L))
      .setHeaderParam("kid", c.getId())
      .setSubject(u.getUsername())
      .signWith(key)
      .compact();
    return token;
  }

  protected SecretKey getKey(PersistentLoginContext c) {
    SecretKey key;
    byte[] bytes = c.getNonce();
    if (bytes == null) {
      key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
      bytes = key.getEncoded();
      c.setNonce(bytes);
      LoginContextManager.edit(c);
    } else
      key = Keys.hmacShaKeyFor(bytes);
    return key;
  }
  
  String refresh_token(PersistentUser u, PersistentLoginContext c) {
    JwtBuilder builder = Jwts.builder();
    builder = builder.setSubject(u.getUsername()).setIssuedAt(new Date()).setNotBefore(new Date(c.getLastLogin()));
    builder = builder.setHeaderParam("kid", c.getId()).
        setId(DatatypeConverter.printHexBinary(c.getSecretKey()));
    return builder.signWith(getKey(c)).compact();
  }
  
  
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token")
  public Response token(MultivaluedMap<String, String> params) {
    String grant = params.getFirst(GRANT_TYPE);
    if (AUTHORIZATION_CODE.equals(grant)) {
        String code  = params.getFirst(CODE);
        String authToken = new String(Base64.getUrlDecoder().decode(code), StandardCharsets.UTF_8);
          char version = authToken.charAt(0);
          String[] split = authToken.split("\f");
          if (version == '2') {
            String authHeader = split[1].substring(7);
            byte[] header = Base64.getDecoder().decode(authHeader);
            String headerString = ":";
            headerString = new String(header, StandardCharsets.UTF_8);
            String authFields[] = headerString.trim().split(":");
            PersistentUser u = UserManager.findByUserName(authFields[0]);
            if (u != null) {
            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
            for (PersistentLoginContext l : loginContextList) {
                if (TOTP.verifyTOTP(authFields[1], DatatypeConverter.printHexBinary(l.getSecretKey()), "8")) {
                    return buildTokenResponse(u, l);
             }
            }
          }}    
    } else if (REFRESH_TOKEN.equals(grant)) {
      String code = params.getFirst(REFRESH_TOKEN);
      JwtParser parser = Jwts.parser().setSigningKeyResolver(AUTH);
      Jws<Claims> token = parser.parseClaimsJws(code);
      String kid = token.getHeader().get("kid").toString();
      Claims body = token.getBody();
      Long id = Long.decode(kid);
      PersistentLoginContext l = LoginContextManager.findEntity(id);
      PersistentUser u = UserManager.findEntity(l.getUserId());
      if ( u.getUsername().equals(body.getSubject())
          && body.getId().equals(DatatypeConverter.printHexBinary(l.getSecretKey()))
          && body.getNotBefore().equals(new Date(l.getLastLogin()/1000L * 1000L))
          )       
    	  return buildTokenResponse(u, l);
      else {
    	  ErrorResponse error = new ErrorResponse("invalid_grant");
    	  return Response.status(Status.BAD_REQUEST).entity(error).build();
    	  
      }
    } else {
        ErrorResponse error = new ErrorResponse("unsupported_grant_type");
        return Response.status(Status.BAD_REQUEST).entity(error).build();

    }
    ErrorResponse error = new ErrorResponse("invalid_request");
    return Response.status(Status.BAD_REQUEST).entity(error).build();

  }

  protected Response buildTokenResponse(PersistentUser u,
      PersistentLoginContext l) {
    DomToken response = new DomToken();

    response.setExpires_in(expires);
    String scope; // extract from "code"
    Long courseID = l.getCourseID();
    scope = courseID == null ? null : PersistentCourse.buildPersistenceId(courseID).getIdString();
    response.setAccess_token(access_token(u, l, scope));
    response.setRefresh_token(refresh_token(u,l));
    response.setToken_type(DomToken.BEARER);
    response.setScope(scope);
    return Response.ok(response).build();
  }
}
