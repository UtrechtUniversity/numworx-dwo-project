// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuestMenuPanel.MenuPanelButton;
import fi.dwo.dwojapplet.gui.action.AppletConfigAction;
import fi.dwo.dwojapplet.gui.action.DwoProfileAction;

import java.awt.event.ActionEvent;

/**
 * This class is the menupanel for the teacher who logged in.
 *
 * @author M.J.B. Kupers
 *
 */
public class AdminMenuPanel extends UserMenuPanel {

//    private MenuPanelButton courseManagementButton;
    private MenuPanelButton userManagementButton;
    private MenuPanelButton profileManagementButton;
    private MenuPanelButton schoolManagementButton;
    private MenuPanelButton studentModelButton;

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.StudentMenuPanel#createMenuButtons()
     */
    @Override
    protected void createMenuButtons() {

        super.createMenuButtons();
        /* Variables used to create items */
        //FontMetrics fm;
        createGap();
        /* Add SchoolManagement button */
        schoolManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_SCHOOL_MANAGEMENT));
        schoolManagementButton.addActionListener(this);
        this.add(schoolManagementButton);
        createGap();
        userManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_USER_MANAGEMENT));
        userManagementButton.addActionListener(this);
        this.add(userManagementButton);
        createGap();
        profileManagementButton = new MenuPanelButton(new DwoProfileAction());
        this.add(profileManagementButton);
        createGap();
        this.add(new MenuPanelButton(new AppletConfigAction()));
        createGap();
        /* Add StudentModel Button */
//      studentModelButton = new MenuPanelButton(Dwo2LocaleMessageTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2LocaleMessageCode.GUI_Button_StudentModels));
        if(DwoHelper.isTest()){
          studentModelButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_STUDENTMODELS));
          studentModelButton.addActionListener(this);
          this.add(studentModelButton);
        }

//        /* Add CourseManagement Button */
//        if (CenterPanel.isIconizer()) {
//            return;
//        }
//        courseManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_COURSE_MANAGEMENT));
////        fm = courseManagementButton.getFontMetrics(courseManagementButton.getFont());
////        courseManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
////        courseManagementButton.setLocation(10, 100);
//        courseManagementButton.addActionListener(this);
////        courseManagementButton.setVisible(false);
//        this.add(courseManagementButton);
////        courseManagementButton.setVisible(true);
    }

    /**
     * Creates a new StudentMenuPanel for the user. It contains the parent items
     * (from StudentMenuPanel) and buttons to show the reult of students, and to
     * add a class.
     */
    public AdminMenuPanel() {
        super();
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
        Object source = e.getSource();
        GuiCreator instance = GuiCreator.instance();
        if (source == schoolManagementButton) {
            instance.setWait();
            CenterSubPanel cp = instance.getSchoolPanel();
            center.loadCenter(cp);
            instance.setReady();
        } else if (source == userManagementButton) {
            instance.setWait();
            CenterSubPanel cp = instance.getUserManagementPanel();
            center.loadCenter(cp);
            instance.setReady();
        } else if (source == studentModelButton) {
            instance.setWait();
            try { 
              CenterSubPanel cp = instance.getStudentModelPanel();
              center.loadCenter(cp);
            } finally { instance.setReady(); }
            return;
      }
    }

}
