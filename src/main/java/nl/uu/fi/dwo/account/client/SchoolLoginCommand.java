/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
class SchoolLoginCommand implements Command {

    private static final Logger LOG = Logger.getLogger(SchoolLoginCommand.class.getName());
	private Command resetLogin;

    public SchoolLoginCommand(Command resetLogin) {
    	this.resetLogin = resetLogin;
    }

    @Override
    public void execute() {
        try {
            if (DwoGlobalVars.instance().getCurrentUser() == null) {
                DwoViewer.showMessage(Dwo2ExceptionCode.GUI_NoUserIsSignedIn);
                return;
            }
            // Create the new popup.
            PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
            //popup.setSize("500", "400");
            SchoolLoginPanel panel = new SchoolLoginPanel(resetLogin, DwoGlobalVars.instance().getCurrentUser());
            panel.setPopup(popup);
            //panel.setSize("300", "200");
            popup.add(panel);
            popup.center();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SchoolLoginCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	public void setResetLogin(Command resetLogin) {
		this.resetLogin = resetLogin;
	}
}
