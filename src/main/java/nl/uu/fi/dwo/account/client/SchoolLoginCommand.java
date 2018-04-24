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
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolLoginCommand implements Command {

  private static final Logger LOG = Logger.getLogger(SchoolLoginCommand.class.getName());
  private Command resetLogin;
  private EventBus bus;

    /**
     *
     * @param resetLogin
     */
    public SchoolLoginCommand(Command resetLogin, EventBus bus) {
    	this.resetLogin = resetLogin;
    	this.bus = bus;
    }

    @Override
    public void execute() {
        try {
            if (DwoGlobalVars.instance().getCurrentUser() == null) {
                bus.fireEvent(new DialogEvent(Dwo2ExceptionCode.GUI_NoUserIsSignedIn));
                return;
            }
            // Create the new popup.
            final PopupPanel popup = new PopupPanel(true);//hide if clicked outside panel
            popup.setStyleName("numworx-popup");
            //popup.setSize("500", "400");
            SchoolLoginPanel panel = new SchoolLoginPanel(resetLogin, DwoGlobalVars.instance().getCurrentUser(), new DialogFailure(bus));
            panel.setPopup(popup);
            panel.setPixelSize(500, 200);
            popup.add(panel);
            popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    int left = (Window.getClientWidth() - offsetWidth) / 3;
                    int top = (Window.getClientHeight() - offsetHeight) / 3;
                    popup.setPopupPosition(left, top);
                }});
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            bus.fireEvent(new DialogEvent(ex));
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
