package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Logger;

public class ProfileCommand implements Command {

    private DomUserFull currentUser;
    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());

    public ProfileCommand(DomUserFull currentUser) {
        this.currentUser = currentUser;
    }
    
    @Override
    public void execute() {
        // Create the new popup.
        PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
        //popup.setSize("500", "400");
        ProfilePanel panel = new ProfilePanel(currentUser);
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
