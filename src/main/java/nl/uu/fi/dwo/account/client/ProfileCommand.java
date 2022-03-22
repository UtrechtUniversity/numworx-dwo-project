package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;

/**
 *
 * @author plas0006
 */
public class ProfileCommand implements Command {

    //private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());
    final EventBus bus;
    final DwoGlobalVars vars;
    final Failure failure;
    /**
     *
     */
    public ProfileCommand(EventBus bus, DwoGlobalVars vars, Failure failure) {
      this.bus = bus;
      this.vars = vars;
      this.failure = failure;
    }
    
    public ProfileCommand(EventBus bus2) {
      this(bus2, DwoGlobalVars.instance(), new DialogFailure(bus2));
    }

    @Override
    public void execute() {
        if(vars.getCurrentUser()==null){
            bus.fireEvent(new DialogEvent(Dwo2ExceptionCode.GUI_NoUserIsSignedIn));
            return;
        }
        // Create the new popup.
        PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
		popup.setStyleName("numworx-popup");

        //popup.setSize("500", "400");
        ProfilePanel panel = new ProfilePanel(vars, bus, failure);
        panel.setPopup(popup);
        //panel.setSize("300", "200");
        popup.add(panel);
        popup.center();
    }
}
