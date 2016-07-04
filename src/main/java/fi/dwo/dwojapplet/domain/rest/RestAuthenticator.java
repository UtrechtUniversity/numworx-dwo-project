package fi.dwo.dwojapplet.domain.rest;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 *
 * @author Gert van der Plas
 */
public class RestAuthenticator extends Authenticator{

//    private static volatile Authenticator instance;
    String username;
    String password;
    
    public RestAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;

    }

//    /**
//     * @return the instance
//     */
//    public static Authenticator getInstance() {
//        return instance;
//    }
//
//    /**
//     * @param aInstance the instance to set
//     */
//    public synchronized static void setInstance(Authenticator aInstance) {
//        instance = aInstance;
//    }

//    @Override
//    protected PasswordAuthentication getPasswordAuthentication() {
//        return pwa;
//    }

    protected PasswordAuthentication GetPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
    
//    protected void setPasswordAuthentication(String username, String password) {
//        pwa = new PasswordAuthentication(username, password.toCharArray());
//    }
}
