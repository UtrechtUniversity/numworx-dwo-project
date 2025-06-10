package fi.dwo.server.rest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.digitalmolehill.crypto.SymmetricCryptor;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DatatypeConverter;
import fi.dwo.server.PersistentDataManagers.cache.LoginContextCache;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.LoginContextUtilManager;
import fi.dwo.server.rest.jaxrsfilters.AuthenticationRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.oauth.ErrorResponse;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.security.TOTP;

@Path("/oauth2")
public class OAuth2Manager {
  private static final String DWO_SAML_CHALLENGE = "dwoSAMLchallenge";
static final String AUTHORIZATION_CODE = "authorization_code";
  static final String REFRESH_TOKEN = "refresh_token";
  static final String CLIENT_CREDENTIALS = "client_credentials";
  static final String GRANT_TYPE = "grant_type";
  static final String CODE = "code";
  private static final Logger LOG = Logger.getLogger(OAuth2Manager.class.getName());
  private static int expires = 1800;
  public static final AuthenticationRequestFilter AUTH = new AuthenticationRequestFilter();
  
  private String access_token(PersistentUser u, PersistentLoginContext c, String scope) {
    SecretKey key;
    key = getKey(c);
    JwtBuilder builder = Jwts.builder();
    if (u.getId().longValue() != c.getUserId().longValue())
      builder.setAudience(RoleType.ANONYMOUS.name());
    else    
    if (u.isSingleSchoolAccount()) builder = builder.setAudience(RoleType.STUDENT.name());
    else builder = builder.setAudience(RoleType.NONE.name());
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

  static protected SecretKey getKey(PersistentLoginContext c) {
    SecretKey key;
    byte[] bytes = c.getNonce();
    if (bytes == null) {
      key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
      bytes = key.getEncoded();
      c.setNonce(bytes);
      LoginContextManager.edit(c);
      LoginContextCache.put(c);
    } else
      key = Keys.hmacShaKeyFor(bytes);
    return key;
  }
  
  String refresh_token(PersistentUser u, PersistentLoginContext c) {
    JwtBuilder builder = Jwts.builder();
    builder = builder.setSubject(u.getUsername()).setIssuedAt(new Date()).setNotBefore(new Date(c.getLastLogin()));
    builder = builder.setExpiration(new Date(c.getLastLogin()+1000L*3600L*23L));
    builder = builder.setAudience("refresh");
    builder = builder.setHeaderParam("kid", c.getId()).
        setId(DatatypeConverter.printHexBinary(c.getSecretKey()));
    return builder.signWith(getKey(c)).compact();
  }
  
  
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token")
  public Response token(MultivaluedMap<String, String> params, @CookieParam(DWO_SAML_CHALLENGE) String challenge, @Context HttpServletRequest request) {
    String grant = params.getFirst(GRANT_TYPE);
    if (AUTHORIZATION_CODE.equals(grant)) {
        String code  = params.getFirst(CODE);
        String authToken = new String(Base64.getUrlDecoder().decode(code), StandardCharsets.UTF_8);
          char version = authToken.charAt(0);
          String[] split = authToken.split("\f");
          switch (version)  {
            case '5':
            {  String username = split[1];
              Long schoolGroupID = Long.valueOf(split[2]);
              Long schoolClassId = Long.valueOf(split[3]);
              String totp = split[4];
              PersistentUser student = UserManager.findByUserName(username);
              PersistentSchoolClass psc = SchoolClassManager.findEntity(schoolClassId);
              PersistentLoginContext pls = LoginContextManager.findEntities(student.getId()).get(0);
              String secret = DatatypeConverter.printHexBinary(pls.getNonce());
              if (TOTP.verifyTOTP(totp, secret, "8", TOTP.defaultPeriod*10))
              try {
            	if (pls.getSecretKey() == null)
            		pls = LoginContextUtilManager.forceNewLoginContextSession(student, true);
                return buildTokenResponse(student, pls);
			  } catch (Dwo2Exception e) {
			  }
            } 
            break;
            case '4':
              String teacher = split[1];
              String student = split[2];
              String totp = split[3];
              PersistentUser u = UserManager.findByUserName(teacher);
              if (u != null) {
                List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
                for (PersistentLoginContext l : loginContextList) {
                  String password = DatatypeConverter.printHexBinary(l.getSecretKey());
                  if (TOTP.verifyTOTP(totp, password, "8")) {
                    try {
                      student = new SymmetricCryptor().decrypt(password.toCharArray(), student);
                      u = UserManager.findByUserName(student);
                      if (u != null) {
                        return buildTokenResponse(u, l);
                      }
                    
                    } catch (Exception e) {
                     }
                  }
                }
              }
            break;
            case '2':
            String authHeader = split[1].substring(7);
            byte[] header = Base64.getDecoder().decode(authHeader);
            String headerString = ":";
            headerString = new String(header, StandardCharsets.UTF_8);
            String authFields[] = headerString.trim().split(":");
            u = UserManager.findByUserName(authFields[0]);
            if (u != null) {
            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
            for (PersistentLoginContext l : loginContextList) {
                if (
                		l.getSecretKey() != null &&
                		TOTP.verifyTOTP(authFields[1], DatatypeConverter.printHexBinary(l.getSecretKey()), "8")) {
                    return buildTokenResponse(u, l);
             }
                // return "invalid_grant"
        		ErrorResponse error = new ErrorResponse("invalid_grant");
                return Response.status(Status.BAD_REQUEST).entity(error).build();
            }
          }
            break;
            case '3':
              // SAML parameters
              String samlUserId = split[1];
              String samlOrgId = split[2];
              String samlAuthToken = split[3];
              PersistentSamlUser samlUser = SamlUserManager.findEntity(samlUserId, samlOrgId);
              if (samlUser == null && !samlOrgId.startsWith("\"")) {
                  samlOrgId = "\"" + samlOrgId + "\"";
                  samlUser = SamlUserManager.findEntity(samlUserId, samlOrgId);
              }
// implement code_challenge OPTIONAL!!!!!
              String verifier = params.getFirst("code_verifier");
              String remote = request.getRemoteAddr();
              LOG.info("remote = " + remote);
              if (verifier != null && challenge != null && !"127.0.0.1".equals(remote)) {
            	  LOG.info("challenge " + verifier + " " + challenge);
            	 MessageDigest digest = null;
        		try {
        			digest = MessageDigest.getInstance("SHA-256");
            		byte[] encodedhash = digest.digest(
                  		  verifier.getBytes(StandardCharsets.UTF_8));
                  		verifier = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(encodedhash);
                   if (!verifier.equals(challenge))
                   {   LOG.warning("challenge failed");
                	   samlUser = null;
                   }
        		} catch (NoSuchAlgorithmException e) {
        			LOG.log(Level.SEVERE,"Should not happen: no S256", e);
        			samlUser = null;
        		}
              }
             
              if (samlUser==null) 
            	  LOG.log(Level.SEVERE, "not found {0} {1}", new Object[] {samlUserId, samlOrgId});
              else
            	  LOG.log(Level.SEVERE, "equal {0}, tokenValid {1} {2} delta-time={3}", new Object[]{samlUser.getAuthToken().equals(samlAuthToken), samlUser.tokenIsValid(20000), samlUser, System.currentTimeMillis()-samlUser.getAuthTokenTimestamp()});

              if (samlUser != null
                      && samlUser.getAuthToken().equals(samlAuthToken) && samlUser.tokenIsValid(20000) //TODO TESTING, productie aan.
                      ) {//milisseconden
                {
                  try {
                    PersistentUser user = UserManager.findEntity(samlUser.getUserID());
                    PersistentLoginContext l;
                    l = LoginContextUtilManager.forceNewLoginContextSession(user, false);
                    samlUser.setAuthTokenTimestamp(samlUser.getAuthTokenTimestamp()-60000L); // oneshot!
                    try {
						SamlUserManager.edit(samlUser); // not fatal
					} catch (Exception e) {
						LOG.log(Level.WARNING, "samluser edit", e);
					}
                    return buildTokenResponse(user, l);
                 } catch (Exception e) {
                    LOG.log(Level.SEVERE, "logincontext", e);
                  }
               }
               break;
            }
        }    
    } else if (REFRESH_TOKEN.equals(grant)) {
      try {
		return refresh(params);
	} catch (SignatureException | UnsupportedJwtException | MalformedJwtException | NullPointerException | ExpiredJwtException
			| IllegalArgumentException e) {
		ErrorResponse error = new ErrorResponse("invalid_grant");
        return Response.status(Status.BAD_REQUEST).entity(error).build();
	}
    } else if (CLIENT_CREDENTIALS.equals(grant)) {
        String client_id = params.getFirst("client_id");
        String client_secret = params.getFirst("client_secret");
        try {
          PersistentUser u = UserManager.login(client_id, client_secret);
          if (u != null) {
              PersistentLoginContext l;
              l = LoginContextUtilManager.forceNewLoginContextSession(u, true);
              return buildTokenResponse(u, l);
            }
         } catch (Exception e) {
            LOG.log(Level.SEVERE, "logincontext", e);
        }
        ErrorResponse error = new ErrorResponse("unsupported_grant_type");
        return Response.status(Status.BAD_REQUEST).entity(error).build();
    } else {
        ErrorResponse error = new ErrorResponse("unsupported_grant_type");
        return Response.status(Status.BAD_REQUEST).entity(error).build();

    }
    ErrorResponse error = new ErrorResponse("invalid_request");
    return Response.status(Status.BAD_REQUEST).entity(error).build();

  }

private Response refresh(MultivaluedMap<String, String> params) throws NullPointerException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
{
	String code = params.getFirst(REFRESH_TOKEN);
      JwtParser parser = Jwts.parser().setSigningKeyResolver(AUTH);
      Jws<Claims> token = parser.parseClaimsJws(code);
      String kid = token.getHeader().get("kid").toString();
      Claims body = token.getBody();
      Long id = Long.decode(kid);
      PersistentLoginContext l = LoginContextManager.findEntity(id);
      PersistentUser u = UserManager.findEntity(l.getUserId());
      if ( u.getUsername().equals(body.getSubject())
          && l.getSecretKey() != null
          && body.getId().equals(DatatypeConverter.printHexBinary(l.getSecretKey()))
          //&& body.getNotBefore().equals(new Date(l.getLastLogin()/1000L * 1000L))
          && "refresh".equals(body.getAudience())
          )       
    	  return buildTokenResponse(u, l);
      else {
    	  ErrorResponse error = new ErrorResponse("invalid_grant");
    	  return Response.status(Status.BAD_REQUEST).entity(error).build();   	  
      }
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
    NewCookie c = new NewCookie(DWO_SAML_CHALLENGE, "","/",null,null, 0, true);   
    return Response.ok(response).cookie(c).build();
  }
  
  @POST  // Chrome
  @Path("/nekot") // end of token
  @Consumes(MediaType.TEXT_PLAIN)
  public Response nekot(@Context HttpServletRequest servletRequest, String plain) {
		MultivaluedMap<String, String> params = convert(plain);
    return nekot(servletRequest, params);
  }

  @POST // Safari
  @Path("/nekot") // end of token
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response nekot(@Context HttpServletRequest servletRequest, MultivaluedMap<String, String> params) {
	try {
		String accessToken = params.getFirst("access_token");
		String code = params.getFirst(REFRESH_TOKEN);
		// doe je ding.
		JwtParser parser;
// ignore expiration time. 
		parser = Jwts.parserBuilder().setSigningKeyResolver(AUTH).setAllowedClockSkewSeconds(36000L).build();
		Jws<Claims> token = parser.parseClaimsJws(code);
		Jws<Claims> access = parser.parseClaimsJws(accessToken);
		String kid = token.getHeader().getKeyId();
		String kid2 = access.getHeader().getKeyId();
		Claims body = token.getBody();
		Long id = Long.decode(kid);
		PersistentLoginContext l = LoginContextManager.findEntity(id);
		PersistentUser u = UserManager.findEntity(l.getUserId());
		if ( u.getUsername().equals(body.getSubject())
		    && kid.equals(kid2)
		    && u.getUsername().equals(access.getBody().getSubject())
		    && access.getBody().getAudience() != null
		    && l.getSecretKey() != null
		    && body.getId().equals(DatatypeConverter.printHexBinary(l.getSecretKey()))
		    && body.getNotBefore().equals(new Date(l.getLastLogin()/1000L * 1000L))
		    )     {
		  if (!SecuredUserAccountManager.isOIDC(System.getProperty("DWO_ENV", "app")) || u.isSingleSchoolAccount())
		  {
			  l.setSecretKey(null);
			  l.setLastLogin(System.currentTimeMillis());
		  }
		  LoginContextManager.edit(l);
		  LoginContextCache.remove(l.getId());
	      if (servletRequest.getSession(false) != null) {
	          servletRequest.getSession().invalidate();
	      }
		}
	} catch (JwtException | IllegalArgumentException | PersistenceException | NullPointerException e) {
		LOG.log(Level.WARNING, e.toString(), e);
	}
    
    return Response.status(Status.UNAUTHORIZED).build();
}

  private MultivaluedMap<String, String> convert(String plain) {
    MultivaluedHashMap<String, String> result = new MultivaluedHashMap<>();
    String split[] = plain.split("&");
    for(String entry: split) {
      String kv[] = entry.split("=",2);
      result.add(kv[0], kv[1]);
    }
    return result;
  }
}
