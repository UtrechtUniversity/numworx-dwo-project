package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;

import javax.inject.Provider;

/**
 *
 * @author velth101
 */
public class StudentModelCommand implements Command {

	
	private Provider<StudentModelView> builder;
	
	public StudentModelCommand(Provider<StudentModelView> builder) {
		this.builder = builder;
	}
	
	public StudentModelCommand() {
		this(() -> new StudentModelPanel());
	}
	
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

        StudentModelView panel = builder.get();
        panel.setPopup(popup);
        popup.center();
    }
}
