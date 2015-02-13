package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import fi.dwo.client.domain.School;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.MainPanel;
import fi.dwo.client.gui.SchoolConfigPanel;
import fi.dwo.client.system.TextMapper;

public class SchoolConfigAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		MainPanel main = GuiCreator.instance().getMainPanel();
		CenterPanel center = main.getCenter();
		School school = GuiCreator.instance().getUser().getSchool();
		center.loadCenter(new SchoolConfigPanel(school));
	}

	public SchoolConfigAction() {
		super(TextMapper.getText(TextMapper.GUIH_SETTINGS));
	}


}
