package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import fi.dwo.dwojapplet.gui.GuiCreatorTeacher;
import fi.dwo.dwojapplet.gui.ProfileDescriptor;

public class ProfileManagementAction extends GuiAction {

  public ProfileManagementAction() {
    super(TextMapper.getText(TextMapper.GUIH_EDIT));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    GuiCreatorTeacher.instance().setWait();
    CenterSubPanel cp = new CourseManagementPanel(new ProfileDescriptor());
    CenterPanel center = getCenter();
    center.setStrategy(new NullStrategy());
    center.getMenu().setEditing(true);
    center.loadCenter(cp);
    GuiCreatorTeacher.instance().setReady();
  }

}
