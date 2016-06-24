package fi.dwo.dwojapplet.domain.rest;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 *
 * @author Gert van der Plas
 */
public class RestAuthenticator extends Authenticator{

    private static volatile Authenticator instance;
    private static volatile PasswordAuthentication pwa;

    public RestAuthenticator(String username, String password) {
        pwa = new PasswordAuthentication(username, password.toCharArray());
    }

    /**
     * @return the instance
     */
    public static Authenticator getInstance() {
        return instance;
    }

    /**
     * @param aInstance the instance to set
     */
    public synchronized static void setInstance(Authenticator aInstance) {
        instance = aInstance;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return pwa;
    }

    protected void setPasswordAuthentication(String username, String password) {
        pwa = new PasswordAuthentication(username, password.toCharArray());
    }
}
