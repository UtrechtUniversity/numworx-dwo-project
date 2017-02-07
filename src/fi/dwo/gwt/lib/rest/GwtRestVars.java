package fi.dwo.gwt.lib.rest;

import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionMapper;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated.
 *
 * @author Gert van der Plas
 */
public class GwtRestVars {

    private static final Logger LOG = Logger.getLogger(GwtRestVars.class.getName());

    private static volatile GwtRestVars instance;

    public static GwtRestVars getInstance() {
        return instance;
    }

    public static void setInstance(GwtRestVars instance) {
        GwtRestVars.instance = instance;
    }

    static {
        try {
            instance = new GwtRestVars();

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            Window.alert("System error: app improperly configured.");
        }
    }

//    /**
//     * @return the dwoLocale
//     */
//    public static DwoLocale getDwoLocale() {
//        return dwoLocale;
//    }
//
//    /**
//     * @param aDwoLocale the dwoLocale to set
//     */
//    public static void setDwoLocale(DwoLocale aDwoLocale) {
//        dwoLocale = aDwoLocale;
//    }

    private RestAuthenticator authenticator = RestAuthenticator.instance;

    //properties
    private static String server;

    /**
     * @return the instance
     */
    public static GwtRestVars instance() {
        if (instance == null) {
            try {
                instance = new GwtRestVars();
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        }
        return instance;
    }

    //Runtime Variabes
    DomUserFull currentUser;

    public GwtRestVars() throws Dwo2Exception {
        initProperties();
        initObjects();
        initVars();
    }

    /**
     * boot phase one
     */
    private void initProperties() throws Dwo2Exception {
        LOG.log(Level.INFO, "Starting initProperties():");
        setServer(DwoConstants.constants.server());
        LOG.log(Level.INFO, "restserver=" + server + ".");
        LOG.log(Level.INFO, "Done initProperties():");
    }

    /**
     * boot phase two
     */
    private void initObjects() {
        LOG.log(Level.INFO, "Starting initObjects():");
        Defaults.setServiceRoot(this.getServer());
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        Defaults.setExceptionMapper(new Dwo2ExceptionMapper());
        setAuthenticator(RestAuthenticator.instance);
//        DefaultFilterawareDispatcher.singleton().addFilter(this.getAuthenticator());
//            restService = GWT.create(DWO2RestCaller.class);
        LOG.log(Level.INFO, "Done initObjects():");
    }

    private void initVars() throws Dwo2Exception {
        //TODO fill DwoSystemParameters and more into the instance.
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * @param aCurUser
     */
    public void setCurrentUser(DomUserFull aCurUser) {
        currentUser = aCurUser;
        if(currentUser != null)
        	setCredentials(currentUser.getUserName(), currentUser.getPassword());        
        else
        	setCredentials(null, null);
    }

    public void setCredentials(String username, String password) {
    	getAuthenticator().setCredentials(username, password);
    }
    
    /**
     * @return the authenticator
     */
    RestAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(RestAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

}
