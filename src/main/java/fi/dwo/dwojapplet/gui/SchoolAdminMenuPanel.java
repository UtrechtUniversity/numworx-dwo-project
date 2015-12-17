package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.gui.action.SchoolConfigAction;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

public class SchoolAdminMenuPanel extends TeacherMenuPanel {

    protected MenuPanelButton userManagementButton;
    protected MenuPanelButton klasKeuzeButton;

    public SchoolAdminMenuPanel(DwoIF dwo) {
        super(dwo);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.TeacherMenuPanel#createMenuButtons()
     */
    @Override
    protected void createMenuButtons() {
        super.createMenuButtons();
        add(Box.createVerticalStrut(20));
        add(new JLabel(TextMapper.getText(TextMapper.GUIMNU_FEATURES_SCHOOLADMIN)));
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

        super.actionPerformed(e);
        if (e.getSource() == userManagementButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getUserManagementPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        } else if (e.getSource() == klasKeuzeButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getClassAdminPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        }

    }

}
