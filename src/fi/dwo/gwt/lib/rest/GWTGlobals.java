/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest;

import com.google.gwt.user.client.Window;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
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
public class GWTGlobals  {

    private static final Logger LOG = Logger.getLogger(GWTGlobals.class.getName());

    private static volatile GWTGlobals instance;
    private RestAuthenticator authenticator = new RestAuthenticator();

    static {
        try {
            instance = new GWTGlobals();

        } catch (Dwo2Exception ex) {
            Logger.getLogger(GWTGlobals.class.getName()).log(Level.SEVERE, null, ex);
            Window.alert("System error: app improperly configured.");
        }
    }
    //properties
    private static String server;

    /**
     * @return the instance
     */
    public static GWTGlobals instance() {
        return instance;
    }

    //Runtime Variabes
    DomUserFull curUser;

    public GWTGlobals() throws Dwo2Exception {
        initProperties();
        initObjects();
        
    }

    private void initObjects(){
            Defaults.setServiceRoot(this.getServer());
            Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
            setAuthenticator(new RestAuthenticator());
            DefaultFilterawareDispatcher.singleton().addFilter(this.getAuthenticator());
//            restService = GWT.create(DWO2RestCaller.class);
        
    }
    private void initProperties() throws Dwo2Exception {
            setServer(DwoConstants.constants.server());
            LOG.log(Level.INFO, "restserver=" + server + ".");
    }

    public void setUser(DomUserFull user){
        this.curUser=user;
        getAuthenticator().setCredentials(user.getUserName(),user.getPassword());
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
     * @return the curUser
     */
    public DomUserFull getCurUser() {
        return curUser;
    }

    /**
     * @param curUser the curUser to set
     */
    public void setCurUser(DomUserFull aCurUser) {
        curUser = aCurUser;
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
