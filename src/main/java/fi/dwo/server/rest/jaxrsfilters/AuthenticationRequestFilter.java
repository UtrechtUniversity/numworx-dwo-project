/**
 * Copyrighted Feb 15, 2018
 */
package fi.dwo.server.rest.jaxrsfilters;

import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.xml.bind.DatatypeConverter;

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
public class AuthenticationRequestFilter implements ContainerRequestFilter {

	
	@Context HttpServletRequest request;
	
    private static class DwoUserSecurityContext implements SecurityContext {

        DwoUserPrincipal u;
        boolean secure;
        String scheme;

        public DwoUserSecurityContext(DwoUserPrincipal user, boolean secure, String scheme) {
            u = user;
        }

        @Override
        public Principal getUserPrincipal() {
            return u;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
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
            } else if (authHeader.startsWith("Bearer ")) {
                //We use our TOTP with username as the bearer token.
                SecurityContext context = validateTOTPToken(authHeader.substring("Bearer ".length()), securityContext);
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

    private SecurityContext validateBasicAuthorization(String authHeader, SecurityContext secCtx) {

        byte[] header = Base64.getDecoder().decode(authHeader);
        String headerString = ":";
        try {
            headerString = new String(header, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        String authFields[] = headerString.trim().split(":");

        PersistentUser u = UserManager.findByUserName(authFields[0]);
        if (u != null && u.getPassword().equals(authFields[1])) {
            SecurityContext sc = new DwoUserSecurityContext(new DwoUserPrincipal(u), secCtx.isSecure(), SecurityContext.BASIC_AUTH);
            setUsername(sc);
            return sc;
        }
        //else error
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

	private SecurityContext validateTOTPToken(String authHeader, SecurityContext secCtx) {

        byte[] header = Base64.getDecoder().decode(authHeader);
        String headerString = ":";
        try {
            headerString = new String(header, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        String authFields[] = headerString.trim().split(":");
        PersistentUser u = UserManager.findByUserName(authFields[0]);
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

}
