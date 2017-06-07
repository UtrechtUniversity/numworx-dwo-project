/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.lms.gwtclient.gwt.graveyard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoViewer;

/**
 *
 * @author G.A.J. van der Plas
 */
class SchoolLoginCommand implements Command {

    private static final Logger LOG = Logger.getLogger(SchoolLoginCommand.class.getName());
	private Command resetLogin;

    /**
     *
     * @param resetLogin
     */
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
            LOG.log(Level.SEVERE, "", ex);
        }
    }

    /**
     *
     * @param resetLogin
     */
    public void setResetLogin(Command resetLogin) {
		this.resetLogin = resetLogin;
	}
}
