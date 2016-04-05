/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.testutil;

import fi.dwo.commons.persistence.RoleType;
import java.security.Identity;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

/**
 * SecurityContext implementation for running junit tests.
 * 
 * @author Gert van der Plas
 */
public class TestSecurityContext implements SecurityContext {

    private Identity principal;
    private RoleType role;

    public TestSecurityContext(String name, final RoleType aRole) {
        principal = new TestUserPrincipal(name);        
        role = aRole;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.DIGEST_AUTH;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isSecure() {
        return true;

    }

    @Override
    public boolean isUserInRole(String aRole) {
        return RoleType.valueOf(aRole) == role;
    }   
}
