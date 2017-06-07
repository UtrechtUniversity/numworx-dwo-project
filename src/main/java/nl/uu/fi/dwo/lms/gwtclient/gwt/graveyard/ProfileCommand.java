package nl.uu.fi.dwo.lms.gwtclient.gwt.graveyard;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoViewer;

/**
 *
 * @author plas0006
 */
public class ProfileCommand implements Command {

    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());

    /**
     *
     */
    public ProfileCommand() {
    }
    
    @Override
    public void execute() {
        if(DwoGlobalVars.instance().getCurrentUser()==null){
            DwoViewer.showMessage(Dwo2ExceptionCode.GUI_NoUserIsSignedIn);
            return;
        }
        // Create the new popup.
        PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
        //popup.setSize("500", "400");
        ProfilePanel panel = new ProfilePanel(DwoGlobalVars.instance().getCurrentUser());
        panel.setPopup(popup);
        //panel.setSize("300", "200");
        popup.add(panel);
        popup.center();
    }
}
