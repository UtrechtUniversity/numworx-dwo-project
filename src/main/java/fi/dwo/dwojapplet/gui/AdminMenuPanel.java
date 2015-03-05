// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import java.awt.event.ActionEvent;

/**
 * This class is the menupanel for the teacher who logged in.
 *
 * @author M.J.B. Kupers
 *
 */
public class AdminMenuPanel extends MenuPanel {

    private MenuPanelButton courseManagementButton;

    private MenuPanelButton schoolManagementButton;

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.MenuPanel#createMenuButtons()
     */
    @Override
    protected void createMenuButtons() {

        super.createMenuButtons();
        /* Variables used to create items */
        //FontMetrics fm;
        createGap();
        /* Add SchoolManagement button */
        schoolManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_SCHOOL_MANAGEMENT));
//        fm = schoolManagementButton.getFontMetrics(schoolManagementButton.getFont());
//        schoolManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
//        schoolManagementButton.setLocation(10, 70);
        schoolManagementButton.addActionListener(this);
//        schoolManagementButton.setVisible(false);
        this.add(schoolManagementButton);
//       schoolManagementButton.setVisible(true);
        createGap();
        /* Add CourseManagement Button */
        if (CenterPanel.isIconizer()) {
            return;
        }
        courseManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_COURSE_MANAGEMENT));
//        fm = courseManagementButton.getFontMetrics(courseManagementButton.getFont());
//        courseManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
//        courseManagementButton.setLocation(10, 100);
        courseManagementButton.addActionListener(this);
//        courseManagementButton.setVisible(false);
        this.add(courseManagementButton);
//        courseManagementButton.setVisible(true);
    }

    /**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * MenuPanel) and buttons to show the reult of students, and to add a class.
     */
    public AdminMenuPanel() {
        super();
    }

    /**
     * Adds the name of the classes wherefrom the user is teacher. Can be
     * overridden by subclasses.
     *
     */
    @Override
    protected void addClassList() {
        createRuler();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        if (e.getSource() == courseManagementButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getCourseManagementPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        } else if (e.getSource() == schoolManagementButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getSchoolPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        }
    }

}
