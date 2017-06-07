/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.graveyard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoViewer;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolClassStudentCommand implements Command {

    private static final Logger LOG = Logger.getLogger(ProfileCommand.class.getName());
    private Command resetLogin;

    /**
     *
     * @param resetLogin
     */
    public SchoolClassStudentCommand(Command resetLogin) {
        this.resetLogin = resetLogin;
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
        if (DwoGlobalVars.instance().getCurrentUser() == null) {
            DwoViewer.showMessage(Dwo2ExceptionCode.GUI_NoUserIsSignedIn);
            return;
        }
        // Create the new popup.
        final PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
        //popup.setSize("500", "400");
        SchoolClassStudentPanel panel = new SchoolClassStudentPanel(resetLogin, DwoGlobalVars.instance().getCurrentUser());
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
