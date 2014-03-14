package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.client.domain.Course;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.CoursePanel;
import fi.dwo.client.gui.GuiCreatorTeacher;
import fi.dwo.client.system.TextMapper;

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