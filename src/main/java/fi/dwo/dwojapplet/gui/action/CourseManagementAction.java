package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.CourseChoisePanel;
import fi.dwo.client.gui.GuiCreatorTeacher;
import fi.dwo.client.gui.ModuleTreePanel;
import fi.dwo.client.system.TextMapper;

public class CourseManagementAction extends GuiAction {

	private Object userObject;

	public CourseManagementAction(CourseChoisePanel courseChoisePanel) {
		super(TextMapper.getText(TextMapper.GUIH_EDIT));
		userObject = courseChoisePanel.getUserObject();
	}

	public CourseManagementAction(CourseMap map) {
		super(TextMapper.getText(TextMapper.GUIH_EDIT));
		userObject = map.getUserObject();
	}
	
	public void actionPerformed(ActionEvent e) {
		GuiCreatorTeacher.instance().setWait();
		CenterSubPanel cp;
		if(userObject instanceof CourseMap)
		{
			cp = GuiCreatorTeacher.instance().getCourseManagementPanel((CourseMap) userObject);
		}
		else 
		{ // van de goede soort....
// TODO of STANDAARD MAP....
			if(userObject == ModuleTreePanel.STANDAARD_DWO_MODULES)
				cp = GuiCreatorTeacher.instance().getCourseManagementPanel(ModuleTreePanel.STANDAARD_DWO_MAP);
			else
				cp = GuiCreatorTeacher.instance().getCourseManagementPanel(ModuleTreePanel.SCHOOL_MAP);
		}
		CenterPanel center = getCenter();
        center.setStrategy(new NullStrategy());
        center.getMenu().setEditing(true);
        center.loadCenter(cp);
        GuiCreatorTeacher.instance().setReady();           
	}
	
}