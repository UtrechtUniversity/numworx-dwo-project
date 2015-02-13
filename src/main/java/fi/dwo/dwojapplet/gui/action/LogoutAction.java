package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Guest;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.system.TextMapper;

public class LogoutAction extends AbstractAction {

	public LogoutAction() {
		boolean gast = User.getCurrentUser() instanceof Guest;
		putValue(Action.NAME,
		TextMapper.getText(gast?TextMapper.GUIL_BTN_LOGIN:TextMapper.GUIL_BTN_LOGOFF));
	
	}

	public void actionPerformed(ActionEvent arg0) {
        GuiCreator.instance().logoff();
        DwoHelper.deleteCookie("dwoUserName");
        DwoHelper.deleteCookie("dwoPassWord");
	}

}
