/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolClassStudentCommand implements Command {
    private DomUserFull currentUser;
    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());

    public SchoolClassStudentCommand(DomUserFull currentUser) {
        this.currentUser = currentUser;
    }
    
    @Override
    public void execute() {
        // Create the new popup.
        PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
        //popup.setSize("500", "400");
        SchoolClassStudentPanel panel = new SchoolClassStudentPanel(currentUser);
        panel.setPopup(popup);
        panel.setSize("300", "200");
        popup.add(panel);
        popup.center();
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(DomUserFull currentUser) {
        this.currentUser = currentUser;
    }
}
