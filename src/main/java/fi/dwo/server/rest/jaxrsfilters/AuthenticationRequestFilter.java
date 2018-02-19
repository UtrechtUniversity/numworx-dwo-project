/** Copyrighted Feb 15, 2018 */
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
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;
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
public class AuthenticationRequestFilter implements ContainerRequestFilter {

    public static class DwoUserPrincipal implements Principal {

        private PersistentUser u;
        private RoleType role;

        DwoUserPrincipal(PersistentUser u) {
            this.u = u;
            this.role = role;
        }

        @Override
        public String getName() {
            return u.getUsername();
        }

        public PersistentUser getUser() {
            return u;
        }
    }

    public static class DwoUserSecurityContext implements SecurityContext {

        DwoUserPrincipal u;

        public DwoUserSecurityContext(DwoUserPrincipal user) {
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
            return u != null;
        }

        @Override
        public String getAuthenticationScheme() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        final SecurityContext securityContext = requestContext.getSecurityContext();
// inspect headers, SecurityContext, etc...
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return;
        }
        //fetch password if matches
//        if (authHeader.startsWith("totpkey ")) {
        if (authHeader.startsWith("Basic ")) {
            SecurityContext context = validateBasicAuthorization(authHeader.substring("Basic ".length()), securityContext);
            requestContext.setSecurityContext(context);
        } else if (authHeader.startsWith("Bearer ")) {
            //We use our TOTP with username as the bearer token.
            SecurityContext context = validateTOTPToken(authHeader.substring("Bearer ".length()), securityContext);
            requestContext.setSecurityContext(context);
        } else {
            //do nothing
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
        if (u.getPassword().equals(authFields[1])) {
            SecurityContext sc = new DwoUserSecurityContext(new DwoUserPrincipal(u));
            return sc;
        }
        //else error
        return null;
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
                    SecurityContext sc = new DwoUserSecurityContext(new DwoUserPrincipal(u));
                    return sc;
                }
            }
        return null;
        }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
