/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolClassStudentCommand implements Command {

    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());
    private Command resetLogin;
    private EventBus bus;
    private Failure failure;
    private DwoGlobalVars vars;
    

    /**
     *
     * @param resetLogin
     */
    public SchoolClassStudentCommand(Command resetLogin, EventBus bus, DwoGlobalVars vars, Failure failure) {
        this.resetLogin = resetLogin;
        this.bus = bus;
        this.vars = vars;
        this.failure = failure;
        
    }
    
    public SchoolClassStudentCommand(Command resetLogin, EventBus bus) {
      this(resetLogin, bus, DwoGlobalVars.instance(), new DialogFailure(bus));
    }

    /**
     *
     * @return
     */
    Command getResetLogin() {
        return resetLogin;
    }

    /**
     *
     * @param resetLogin
     */
    void setResetLogin(Command resetLogin) {
        this.resetLogin = resetLogin;
    }

    @Override
    public void execute() {
        if (vars.getCurrentUser() == null) {
            bus.fireEvent(new DialogEvent(Dwo2ExceptionCode.GUI_NoUserIsSignedIn));
            return;
        }
        // Create the new popup.
        final PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
		popup.setStyleName("numworx-popup");
       //popup.setSize("500", "400");
        SchoolClassStudentPanel panel = new SchoolClassStudentPanel(resetLogin, vars.getCurrentUser(), vars.getContext(), failure);
        panel.setPopup(popup);
        //panel.setSize("300", "200");
        popup.add(panel);
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 3;
                int top = (Window.getClientHeight() - offsetHeight) / 3;
                popup.setPopupPosition(left, top);
            }
        ;
    }

);
//        popup.center();
    }
}
