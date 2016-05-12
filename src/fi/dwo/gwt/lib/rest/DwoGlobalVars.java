/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest;

import com.google.gwt.user.client.Window;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import fi.dwo.rest.DwoLocale;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

/**
 *
 *
 * @author Gert van der Plas
 */
public class DwoGlobalVars {

    private static final Logger LOG = Logger.getLogger(DwoGlobalVars.class.getName());

    private static volatile DwoGlobalVars instance;

    static {
        try {
            instance = new DwoGlobalVars();

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            Window.alert("System error: app improperly configured.");
        }
    }    
    /**
     * @return the dwoLocale
     */
    public static DwoLocale getDwoLocale() {
        return dwoLocale;
    }

    /**
     * @param aDwoLocale the dwoLocale to set
     */
    public static void setDwoLocale(DwoLocale aDwoLocale) {
        dwoLocale = aDwoLocale;
    }
    private RestAuthenticator authenticator = new RestAuthenticator();

    //properties
    private static String server;
    private static DwoLocale dwoLocale = new DwoLocale("nl-NL");

    /**
     * @return the instance
     */
    public static DwoGlobalVars instance() {
        if(instance==null){
            try {
                instance = new DwoGlobalVars();
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    //Runtime Variabes
    DomUserFull currentUser;

    public DwoGlobalVars() throws Dwo2Exception {
        initProperties();
        initObjects();
    }

    private void initObjects() {
        LOG.log(Level.INFO, "Starting initObjects():" );

        Defaults.setServiceRoot(this.getServer());
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        setAuthenticator(new RestAuthenticator());
        DefaultFilterawareDispatcher.singleton().addFilter(this.getAuthenticator());
//            restService = GWT.create(DWO2RestCaller.class);
        LOG.log(Level.INFO, "Done initObjects():" );


    }

    private void initProperties() throws Dwo2Exception {
        setServer(DwoConstants.constants.server());
        LOG.log(Level.INFO, "restserver=" + server + ".");
    }

    public void setUser(DomUserFull user) {
        this.currentUser = user;
        getAuthenticator().setCredentials(user.getUserName(), user.getPassword());
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
     * @param curUser the currentUser to set
     */
    public void setCurrentUser(DomUserFull aCurUser) {
        currentUser = aCurUser;
    }

    /**
     * @return the authenticator
     */
    public RestAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(RestAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

}
