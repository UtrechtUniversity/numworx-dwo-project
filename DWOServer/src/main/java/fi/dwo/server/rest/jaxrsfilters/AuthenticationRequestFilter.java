/**
 * Copyrighted Feb 15, 2018
 */
package fi.dwo.server.rest.jaxrsfilters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DatatypeConverter;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.cache.LoginContextCache;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.xss.SecFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.Keys;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.security.TOTP;

/**
 * AuthenticationRequestFilter supports Basic and TOTP authorization against the
 * database.
 *
 * @author plas0006
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationRequestFilter implements ContainerRequestFilter, SigningKeyResolver {

    static final Logger LOG = Logger.getLogger(AuthenticationRequestFilter.class.getName());
    static final String BEARER = "BEARER";
	
	@Context HttpServletRequest request;
	
	public AuthenticationRequestFilter() { }
	public AuthenticationRequestFilter(HttpServletRequest req) {
		request = req;
	}
	
    private static class DwoUserSecurityContext implements SecurityContext {

        DwoUserPrincipal u;
        boolean secure;
        String scheme;
        RoleType role;

        public DwoUserSecurityContext(DwoUserPrincipal user, boolean secure, String scheme) {
            u = user;
            role = RoleType.NONE;
            this.scheme = scheme;
        }

        public DwoUserSecurityContext(DwoUserPrincipal principal, boolean secure,
            String scheme, RoleType role) {
          this(principal, secure, scheme);
          this.role = role;
        }

        @Override
        public Principal getUserPrincipal() {
            return u;
        }

        @Override
        public boolean isUserInRole(String role) {
            return RoleType.NONE == this.role || role.equals(this.role.name());
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return scheme;
        }
    };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!requestContext.getUriInfo().getPath().startsWith("secure")) {
            //public request
            return;
        } else {
            //secure access
            final SecurityContext securityContext = requestContext.getSecurityContext();
            String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (authHeader == null) {
                //Throw 401
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                //requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
            }
            //fetch password if matches
            if (authHeader.startsWith("Basic ")) {
                SecurityContext context = validateBasicAuthorization(authHeader.substring("Basic ".length()), securityContext);
                if(context==null){
                    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    //                requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
                }
                requestContext.setSecurityContext(context);
            } else if (authHeader.toLowerCase().startsWith("bearer ")) { // ignore case: BEARER en bearer zijn ook okay
                //We use our JWT with username as the bearer token.
                SecurityContext context = validateJWTToken(authHeader.substring("Bearer ".length()), securityContext);
                if(context==null){
                    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
  //                  requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
                }
                requestContext.setSecurityContext(context);
            } else {
                    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
//                requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
            }
        }
    }

    public SecurityContext validateBasicAuthorization(String authHeader, SecurityContext secCtx) {

        byte[] header = Base64.getDecoder().decode(authHeader);
        String headerString;
        headerString = new String(header, StandardCharsets.UTF_8);
        String authFields[] = headerString.trim().split(":");
        if (authFields.length < 2) {
        	LOG.severe("Password is missing, " + authFields[0]);
        	return null;
        }
 // cache code
        Object roleid = getAttribute(SecFilter.HASROLE_ID);
        PersistentHasRole hrcache = HasRoleCache.get(roleid);
        if (hrcache != null) {
        	DwoUserPrincipal du = fromCache(hrcache, authFields[0], authFields[1]);
        	if (du != null) {
        		DwoUserSecurityContext sc = new DwoUserSecurityContext(du, secCtx.isSecure(), SecurityContext.BASIC_AUTH, du.getRole());
        		setUsername(sc);
        		return sc;
        	}
        }
        PersistentUser u = UserManager.login(authFields[0], authFields[1]);
        if (u != null) {
        	SecurityContext sc;
        	Object uid = getAttribute(SecFilter.USER_ID);
        	if (uid != null && ! u.getId().equals(uid))
        		return null;
        	Object sgid = getAttribute(SecFilter.SCHOOLGROUP_ID);
        	if (sgid != null) {
        		PersistentHasRolePK pk = new PersistentHasRolePK(u.getId(), (Long)sgid);
        		PersistentHasRole hr = HasRoleManager.findEntity(pk);
        		if (hr == null) return null;
        		PersistentSchoolGroup sg = hr.getSchoolGroup();        		
        		if (sg == null) {
        			LOG.severe("SG is null " + uid + " " + sgid);
        			sg = SchoolGroupManager.findEntity( (Long) sgid);
        		}
        		if (hr.getUser() == null) {
        			LOG.severe("USER is null " + uid + " " + sgid);
        		}
//        		if (sg.getRole() == null) {
//        			LOG.severe("ROLE is null " + sg.getGroupID());
//        		} else if (sg.getRole().getGroupID() == null) {
//        			LOG.severe("GROUPID is null " + sg.getRole() + " " + sg.getGroupID());
//        		}
        		HasRoleCache.put(hr);
        		DwoUserPrincipal du = new DwoUserPrincipal(u, hr, sg);
        		sc = new DwoUserSecurityContext(du, secCtx.isSecure(), SecurityContext.BASIC_AUTH, du.getRole());
        	} else {
        		sc = new DwoUserSecurityContext(new DwoUserPrincipal(u), secCtx.isSecure(), SecurityContext.BASIC_AUTH);
        	}
        	setUsername(sc);
            return sc;
        }
        //else error
        return null;
    }

	private DwoUserPrincipal fromCache(PersistentHasRole hr, String username, String password) {
		PersistentUser u = hr.getUser();
		if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
			try {
				return new DwoUserPrincipal(hr);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Exception " + hr.getSchoolGroup().getRoleType(), e);
			}
		}
		return null;
	}
	/**
	 * Save the username in a http request attribute <b>"username"</b>. In tomcat
	 * this string can be logged by %{username}r in the logging valve.
	 * 
	 * @param sc
	 */
	private void setUsername(SecurityContext sc) {
		if (request != null) {
			request.setAttribute("username", sc.getUserPrincipal().getName());
		}

	}

	protected Object getAttribute(String key) {
		if (request == null) return null;
		return request.getAttribute(key);
	}
	
	private RoleType role(Claims claims) {
      String role = claims.getAudience();
      RoleType type = RoleType.valueOf(role);
      return type;
	}
	
	public SecurityContext validateJWTToken(String token, SecurityContext ctx) {	  
	  try {
        JwtParser parser = Jwts.parser().setSigningKeyResolver(this);
        Jws<Claims> claims = parser.parseClaimsJws(token);
        String username = claims.getBody().getSubject();
        RoleType type = role(claims.getBody());
        PersistentUser u = findByUsername(username);
        if (u == null) return null;
        Object roleid = getAttribute(SecFilter.HASROLE_ID);
        PersistentHasRole hrcache = HasRoleCache.get(roleid);
    	SecurityContext sc;
        if (hrcache != null) {
    		DwoUserPrincipal du = new DwoUserPrincipal(hrcache);
    		if (type == RoleType.NONE) type = du.getRole();
    		sc = new DwoUserSecurityContext(du, ctx.isSecure(), BEARER, type);    	
        } else {
	    	Object uid = getAttribute(SecFilter.USER_ID);
	    	if (uid != null && ! u.getId().equals(uid))
	    		return null;
	    	Object sgid = getAttribute(SecFilter.SCHOOLGROUP_ID);
	    	if (sgid != null) {
	    		PersistentHasRolePK pk = new PersistentHasRolePK(u.getId(), (Long)sgid);
	    		PersistentHasRole hr = HasRoleManager.findEntity(pk);
	    		if (hr == null) return null;
	    		HasRoleCache.put(hr);
	    		DwoUserPrincipal du = new DwoUserPrincipal(hr);
	    		if (type == RoleType.NONE) type = du.getRole();
	    		sc = new DwoUserSecurityContext(du, ctx.isSecure(), BEARER, type);
	    	} else {
	    		sc = new DwoUserSecurityContext(new DwoUserPrincipal(u), ctx.isSecure(), BEARER, type);
	    	}
        }
        setUsername(sc);
        return sc;
	} catch(ExpiredJwtException e) {
	  LOG.log(Level.INFO, e.toString());
	  return null;
    } catch (Exception e) {
      LOG.log(Level.WARNING, "error in token", e);
      return null;
    } 
	  
	}

  protected PersistentUser findByUsername(String username) {
    Object roleid = getAttribute(SecFilter.HASROLE_ID);
    PersistentHasRole hrcache = HasRoleCache.get(roleid);
    if(hrcache != null) {
    	PersistentUser user = hrcache.getUser();
    	if (username.equalsIgnoreCase(user.getUsername()))
    		return user;
    }
    return UserManager.findByUserName(username);
  }
	
// Not used!	
	SecurityContext validateTOTPToken(String authHeader, SecurityContext secCtx) {

        byte[] header = Base64.getDecoder().decode(authHeader);
        String headerString = ":";
        try {
            headerString = new String(header, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        String authFields[] = headerString.trim().split(":");
        PersistentUser u = UserManager.findByUserName(authFields[0]);
        if (u == null) return null;
        List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
        for (PersistentLoginContext l : loginContextList) {
            if (TOTP.verifyTOTP(authFields[1], DatatypeConverter.printHexBinary(l.getSecretKey()), "8")) {
                SecurityContext sc = new DwoUserSecurityContext(new DwoUserPrincipal(u), secCtx.isSecure(), "BEARER");
                setUsername(sc);
                return sc;
            }
        }
        return null;
    }
//
//    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
//
//    public static String bytesToHex(byte[] bytes) {
//        char[] hexChars = new char[bytes.length * 2];
//        for (int j = 0; j < bytes.length; j++) {
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 2] = hexArray[v >>> 4];
//            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//        }
//        return new String(hexChars);
//    }

  @Override
  public Key resolveSigningKey(JwsHeader header, Claims claims) {
    String kid = header.getKeyId();
    PersistentLoginContext context = findLoginContext(kid);
    if (context == null || context.getNonce() == null) return null;
    PersistentUser u = findByUsername(claims.getSubject());    
    if (u == null || u.getId().longValue() != context.getUserId().longValue()
        && role(claims) != RoleType.ANONYMOUS // 
        ) return null; // No key
    LoginContextCache.put(context);
    return Keys.hmacShaKeyFor(context.getNonce());
  }

  protected PersistentLoginContext findLoginContext(String kid) {
    Long id = Long.decode(kid);
    PersistentLoginContext cache = LoginContextCache.get(id);
    if (cache != null) return cache;
    PersistentLoginContext context = LoginContextManager.findEntity(id);
    return context;
  }

  @Override
  public Key resolveSigningKey(JwsHeader header, String plaintext) {
    String kid = header.getKeyId();
    PersistentLoginContext context = findLoginContext(kid);
    if (context.getNonce() == null) {
      return null;
    }
   return Keys.hmacShaKeyFor(context.getNonce());
  }

}
