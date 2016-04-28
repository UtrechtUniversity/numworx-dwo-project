/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class GlobalVars {
    private static final Logger LOG = Logger.getLogger(GlobalVars.class.getName());

    GlobalVars instance = new GlobalVars();
    //properties
    private static String server;

    //Runtime Variabes
    DomUserFull curUser;

    public GlobalVars() throws Dwo2Exception {
        initProperties();
    }

    private void initProperties() throws Dwo2Exception {
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/META-INF/run.properties"));
            setServer(properties.getProperty("server","https://app.dwo.nl"));
            LOG.log(Level.INFO, "restserver="+server+".");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
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

}
