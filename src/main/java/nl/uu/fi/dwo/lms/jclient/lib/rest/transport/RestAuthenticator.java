package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;

/**
 *
 * @author Gert van der Plas
 */
@Deprecated //Static class use is evil!
public class RestAuthenticator extends Authenticator {

    private static URL serverUrlPath;
    private DomContext context;
    private String username;
    private String password;

    private static volatile RestAuthenticator instance;

    public static RestAuthenticator getInstance() {
        return instance;
    }

    public static void setInstance(RestAuthenticator instance) {
        RestAuthenticator.instance = instance;
    }

    static {
        instance = new RestAuthenticator();
    }

//    public RestAuthenticator(String username, String password) {
//        this.username = username;
//        this.password = password;
//
//    }
    protected PasswordAuthentication GetPasswordAuthentication() {
        return new PasswordAuthentication(getUsername(), getPassword().toCharArray());
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the serverUrlPath
     */
    public static URL getServerUrlPath() {
        return serverUrlPath;
    }

    /**
     * @param aServerUrlPath the serverUrlPath to set
     */
    public static void setServerUrlPath(URL aServerUrlPath) {
        serverUrlPath = aServerUrlPath;
    }

    /**
     * @return the context
     */
    public DomContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(DomContext context) {
        this.context = context;
    }
}
