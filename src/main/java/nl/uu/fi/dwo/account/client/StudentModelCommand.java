package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;

/**
 *
 * @author plas0006
 */
public class StudentModelCommand implements Command {

    private static final Logger LOG = Logger.getLogger(StudentModelCommand.class.getName());
    
    @Override
    public void execute() {
        if(DwoGlobalVars.instance().getCurrentUser()==null){
            DwoViewer.showMessage(Dwo2ExceptionCode.GUI_NoUserIsSignedIn);
            return;
        }
        // Create the new popup.
        PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
		popup.setStyleName("numworx-popup");

        StudentModelPanel panel = new StudentModelPanel(DwoGlobalVars.instance().getCurrentUser(), DwoGlobalVars.instance().getActiveSchoolRoleAndClass());
        panel.setPopup(popup);
        panel.setPixelSize(300, 200);
        popup.add(panel);
        popup.center();
    }
}
