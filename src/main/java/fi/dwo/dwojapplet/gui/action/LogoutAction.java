package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.GuiCreator;

public class LogoutAction extends AbstractAction {

    public LogoutAction() {
        boolean gast = User.getCurrentUser() instanceof Guest;
        putValue(Action.NAME,
                TextMapper.getText(gast ? TextMapper.GUIL_BTN_LOGIN : TextMapper.GUIL_BTN_LOGOFF));

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        GuiCreator.instance().logoff();
        DwoHelper.deleteCookie("dwoUserName");
        DwoHelper.deleteCookie("dwoPassWord");
    }

}
