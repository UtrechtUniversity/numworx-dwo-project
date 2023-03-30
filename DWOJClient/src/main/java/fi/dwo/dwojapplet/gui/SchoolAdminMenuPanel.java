package fi.dwo.dwojapplet.gui;

import fi.beans.numworxlf.JButton;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.action.SchoolConfigAction;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.JLabel;

public class SchoolAdminMenuPanel extends UserMenuPanel {

    protected MenuPanelButton userManagementButton;
    protected MenuPanelButton klasKeuzeButton;

    public SchoolAdminMenuPanel() {
        super();
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.TeacherMenuPanel#createMenuButtons()
     */
    @Override
    protected void createMenuButtons() {
        super.createMenuButtons();
        add(Box.createVerticalStrut(20));
        JLabel features = new JLabel(TextMapper.getText(TextMapper.GUIMNU_FEATURES_SCHOOLADMIN));
        features.setForeground(GuiConstants.HEADER_COLOR);
        add(features);
        createGap();
        this.userManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_USERS_SCHOOL));
        userManagementButton.addActionListener(this);
        add(userManagementButton);
        createGap();
        klasKeuzeButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASSES_SCHOOL));
        klasKeuzeButton.addActionListener(this);
        add(klasKeuzeButton);

        JButton schoolsetup = new MenuPanelButton(new SchoolConfigAction());
        createGap();
        add(schoolsetup);

    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.TeacherMenuPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      
        if (e.getSource() == mainMenuButton) {
          center.reset();
          center.end(); // must be idempotent 
          PersistenceFacade.instance().clearCurrentCourseDataCache();
          center.tree.createModel(GuiCreator.instance().dwo);
          center.loadCenter(GuiCreator.instance().getCourseChoisePanel());
          return;
        }
      
        super.actionPerformed(e);
        if (e.getSource() == userManagementButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getUserManagementPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        }
        else if (e.getSource() == klasKeuzeButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getClassAdminPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        }

    }
    @Override
    public void setEditing(boolean b) {
        boolean enabled = !b;
        setEnabled(enabled);
        for (int i = 0; i < getComponentCount(); i++) {
            getComponent(i).setEnabled(enabled);
        }
        //repaint();

    }

}
