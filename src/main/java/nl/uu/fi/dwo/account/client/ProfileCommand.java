package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.rest.dom.entities.DomUser;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileCommand implements Command {

    private DomUser currentUser;
    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());

    public ProfileCommand(DomUser currentUser) {
        this.currentUser = currentUser;
    }
    
    @Override
    public void execute() {
        // Create the new popup.
        PopupPanel popup = new PopupPanel();
        //popup.setSize("500", "400");
        if (currentUser == null) {
            LOG.log(Level.INFO, "Setting a default user for testing.");
            currentUser = new DomUser();
            currentUser.setGivenName("Gert");
            currentUser.setInsertion("van der");
            currentUser.setFamilyName("Plas");
            currentUser.setUserName("project_gert");
            currentUser.setSingleSchool(false);
//        currentUser.setId();
        }
        ProfilePanel panel = new ProfilePanel(currentUser);
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
