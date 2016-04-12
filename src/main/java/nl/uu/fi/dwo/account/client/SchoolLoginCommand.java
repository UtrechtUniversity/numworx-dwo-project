/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.rest.dom.entities.DomUser;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
class SchoolLoginCommand implements Command {

    private DomUser currentUser;
    private static final Logger LOG = Logger.getLogger(SchoolLoginCommand.class.getName());

    public SchoolLoginCommand(DomUser currentUser) {
        this.currentUser = currentUser;
    }

    
    @Override
    public void execute() {
        // Create the new popup.
        PopupPanel popup = new PopupPanel();
        //popup.setSize("500", "400");
        SchoolLoginPanel panel = new SchoolLoginPanel(currentUser);
        panel.setPopup(popup);
        panel.setSize("300", "200");
        popup.add(panel);
        popup.center();
    }

    /**
     * @return the currentUser
     */
    public DomUser getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(DomUser currentUser) {
        this.currentUser = currentUser;
    }

    
}
