package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.gui.GuiCreator;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class LogoutAction extends AbstractAction {

    public LogoutAction() {
        boolean gast = DwoHelper.getCurrentFacadeUser() instanceof Guest;
        putValue(Action.NAME,
                TextMapper.getText(gast ? TextMapper.GUIL_BTN_LOGIN : TextMapper.GUIL_BTN_LOGOFF));

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
    	DomLoginContext context = DwoHelper.getCurrentLoginContext();
    	if (context != null) {
    		context.setSecretKey(null); // really, really logout. void secret key & refresh_token 
    	}
        GuiCreator.instance().logoff(); // normally logout van keep secret key -> keep refresh_token valid.
    }

}
