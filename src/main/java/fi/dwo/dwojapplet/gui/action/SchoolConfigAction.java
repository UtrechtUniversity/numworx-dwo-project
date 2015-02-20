package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.MainPanel;
import fi.dwo.dwojapplet.gui.SchoolConfigPanel;

public class SchoolConfigAction extends AbstractAction {

    @Override
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
