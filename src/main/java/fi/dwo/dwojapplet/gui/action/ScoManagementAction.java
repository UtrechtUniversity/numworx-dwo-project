package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.CoursePanel;
import fi.dwo.dwojapplet.gui.GuiCreatorTeacher;

public class ScoManagementAction extends GuiAction {

    private Course course;

    public ScoManagementAction(CoursePanel coursePanel) {
        super(TextMapper.getText(TextMapper.GUIH_EDIT));
        course = coursePanel.getCourse();
    }

    public ScoManagementAction(Course course) {
        super(TextMapper.getText(TextMapper.GUIH_EDIT));
        this.course = course;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        GuiCreatorTeacher.instance().setWait();
        CenterSubPanel cp;
        cp = GuiCreatorTeacher.instance().getScoManagementPanel(course);
        CenterPanel center = getCenter();
        center.setStrategy(new NullStrategy());
        center.getMenu().setEditing(true);
        center.loadCenter(cp);
        GuiCreatorTeacher.instance().setReady();
    }

}
