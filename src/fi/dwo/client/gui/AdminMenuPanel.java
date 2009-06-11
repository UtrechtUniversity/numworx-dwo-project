// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java

package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.Panel;
import java.awt.event.ActionEvent;

import fi.dwo.client.system.TextMapper;

/**
 * This class is the menupanel for the teacher who logged in.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class AdminMenuPanel extends MenuPanel {

     private DwoButton courseManagementButton;
     
     private DwoButton schoolManagementButton;

    
    /**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * MenuPanel) and buttons to show the reult of students, and to add a class.
     */
    public AdminMenuPanel() {
        super();
        /* Variables used to create items */
        FontMetrics fm;
        
         /* Add SchoolManagement button */
        schoolManagementButton = new DwoButton(TextMapper.getText(TextMapper.GUIMNU_SCHOOL_MANAGEMENT), GuiConstants.MAIN_BACKGROUND);
        fm = schoolManagementButton.getFontMetrics(schoolManagementButton.getFont());
        schoolManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
        schoolManagementButton.setLocation(10, 70);
        schoolManagementButton.addActionListener(this);
        schoolManagementButton.setVisible(false);
        this.add(schoolManagementButton);
        schoolManagementButton.setVisible(true);

        /* Add CourseManagement Button */
        courseManagementButton = new DwoButton(TextMapper.getText(TextMapper.GUIMNU_COURSE_MANAGEMENT), GuiConstants.MAIN_BACKGROUND);
        fm = courseManagementButton.getFontMetrics(courseManagementButton.getFont());
        courseManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
        courseManagementButton.setLocation(10, 100);
        courseManagementButton.addActionListener(this);
        courseManagementButton.setVisible(false);
        this.add(courseManagementButton);
        courseManagementButton.setVisible(true);
    }

	/**
     * Adds the name of the classes wherefrom the user is teacher. Can be
     * overridden by subclasses.
     *  
     */
    protected void addClassList() {
        Panel p = new BorderedPanel(null, BorderedPanel.NORTH);
        p.setSize(this.getSize().width - 1, 1);
        p.setLocation(0, 135);
        p.setVisible(false);
        this.add(p);
        p.setVisible(true);

    }
    

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
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