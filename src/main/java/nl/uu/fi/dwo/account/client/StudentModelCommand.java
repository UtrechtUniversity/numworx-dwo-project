package nl.uu.fi.dwo.account.client;


import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;

import javax.inject.Provider;

/**
 *
 * @author velth101
 */
public class StudentModelCommand implements Command {

	
	private Provider<StudentModelView> builder;
	private EventBus bus;
	
	public StudentModelCommand(Provider<StudentModelView> builder, EventBus bus) {
		this.builder = builder;
		this.bus = bus;
	}
	
	public StudentModelCommand(EventBus bus) {
		this(() -> new StudentModelPanel(new DialogFailure(bus)), bus);
	}
	
    private static final Logger LOG = Logger.getLogger(StudentModelCommand.class.getName());
    
    @Override
    public void execute() {
        if(DwoGlobalVars.instance().getCurrentUser()==null){
            bus.fireEvent(new DialogEvent(Dwo2ExceptionCode.GUI_NoUserIsSignedIn));
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
