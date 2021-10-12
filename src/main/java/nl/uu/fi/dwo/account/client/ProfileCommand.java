package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;

import javax.inject.Inject;

/**
 *
 * @author plas0006
 */
public class ProfileCommand implements Command {

    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());
    final EventBus bus;
    /**
     *
     */
    @Inject public ProfileCommand(EventBus bus) {
      this.bus = bus;
    }
    
    @Override
    public void execute() {
        if(DwoGlobalVars.instance().getCurrentUser()==null){
            bus.fireEvent(new DialogEvent(Dwo2ExceptionCode.GUI_NoUserIsSignedIn));
            return;
        }
        // Create the new popup.
        PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
		popup.setStyleName("numworx-popup");

        //popup.setSize("500", "400");
        ProfilePanel panel = new ProfilePanel(DwoGlobalVars.instance().getCurrentUser(), bus);
        panel.setPopup(popup);
        //panel.setSize("300", "200");
        popup.add(panel);
        popup.center();
    }
}
