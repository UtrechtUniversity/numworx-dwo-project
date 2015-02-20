package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.CourseChoicePanel;
import fi.dwo.dwojapplet.gui.GuiCreatorTeacher;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;

public class CourseManagementAction extends GuiAction {

    private Object userObject;

    public CourseManagementAction(CourseChoicePanel courseChoisePanel) {
        super(TextMapper.getText(TextMapper.GUIH_EDIT));
        userObject = courseChoisePanel.getUserObject();
    }

    public CourseManagementAction(CourseMap map) {
        super(TextMapper.getText(TextMapper.GUIH_EDIT));
        userObject = map.getUserObject();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GuiCreatorTeacher.instance().setWait();
        CenterSubPanel cp;
        if (userObject instanceof CourseMap) {
            cp = GuiCreatorTeacher.instance().getCourseManagementPanel((CourseMap) userObject);
        } else { // van de goede soort....
// TODO of STANDAARD MAP....
            if (userObject == ModuleTreePanel.STANDAARD_DWO_MODULES) {
                cp = GuiCreatorTeacher.instance().getCourseManagementPanel(ModuleTreePanel.STANDAARD_DWO_MAP);
            } else {
                cp = GuiCreatorTeacher.instance().getCourseManagementPanel(ModuleTreePanel.SCHOOL_MAP);
            }
        }
        CenterPanel center = getCenter();
        center.setStrategy(new NullStrategy());
        center.getMenu().setEditing(true);
        center.loadCenter(cp);
        GuiCreatorTeacher.instance().setReady();
    }

}
