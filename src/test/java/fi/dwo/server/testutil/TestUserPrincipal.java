/**
 * Copyrighted Oct 15, 2015
 */
package fi.dwo.server.testutil;

import java.security.Identity;

/**
 *
 * @author Gert van der Plas
 */
public class TestUserPrincipal extends Identity {

    public TestUserPrincipal(final String name) {
        super(name);
    }

}
