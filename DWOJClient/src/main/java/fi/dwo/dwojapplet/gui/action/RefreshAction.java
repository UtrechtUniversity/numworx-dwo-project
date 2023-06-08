package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.User;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class RefreshAction extends GuiAction {

	public RefreshAction() {
		super(getTitle());
		User user = instance().getUser();
		setEnabled( !(user instanceof Guest) );
	}


	private static String getTitle() {
		return TextMapper.getText("Refresh");
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		User user = instance().getUser();
        instance().logoff();
        try {
			instance().login(user.getUsername(), null);
		} catch (LoginException e) {
		} catch (Dwo2Exception e) {
		}


	}

}
