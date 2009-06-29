// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java

package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.system.TextMapper;

/**
 * This class is the menupanel for the teacher who logged in.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class TeacherMenuPanel extends MenuPanel {

    private JButton classManagementButton;
    
    private JButton courseManagementButton;

    private ClassLinkedLabel[] classLinkedList;
    
    private JPanel classPanel;

    /* (non-Javadoc)
	 * @see fi.dwo.client.gui.MenuPanel#createButtons()
	 */
	protected void createMenuButtons() {
		super.createMenuButtons();
        /* Variables used to create items */
        //FontMetrics fm;
		createGap();
        /* Add ClassManagement button */
        classManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASS_MANAGEMENT));
        //fm = classManagementButton.getFontMetrics(classManagementButton.getFont());
        //classManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
        //classManagementButton.setLocation(10, 70);
        classManagementButton.addActionListener(this);
        //classManagementButton.setVisible(false);
        //if(GuiCreator.instance().getUser() instanceof Teacher)
        this.add(classManagementButton);
        //classManagementButton.setVisible(true);
        createGap();
        /* Add CourseManagement Button */
        courseManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_COURSE_MANAGEMENT));
        //fm = courseManagementButton.getFontMetrics(courseManagementButton.getFont());
        //courseManagementButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
        //courseManagementButton.setLocation(10, 100);
        courseManagementButton.addActionListener(this);
        //courseManagementButton.setVisible(false);
        this.add(courseManagementButton);
        //courseManagementButton.setVisible(true);
	}

	/**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * MenuPanel) and buttons to show the reult of students, and to add a class.
     */
    public TeacherMenuPanel() {
        super();
    }

    /**
     * Adds the name of the classes wherefrom the user is teacher. Can be
     * overridden by subclasses.
     *  
     */
    protected void addClassList() {
        /* Variables used to create items */
        FontMetrics fm;
        JLabel l;

        createRuler();
        
        classPanel = new JPanel(null);
        classPanel.setDoubleBuffered(false);
        classPanel.setSize(this.getSize().width - 1, 1);
        //classPanel.setLocation(0, 136);
        classPanel.setOpaque(false);
        /* Add class-info */
        if(GuiCreator.instance().getUser() instanceof Teacher){
	        Teacher t = (Teacher) GuiCreator.instance().getUser();
	        if ((t.getClasses() != null) && (t.getClasses().length != 0)) {
	            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_CLASS_RESULTS)
	                    + ":");
	            l.setOpaque(false);
	            l.setFont(GuiConstants.NORMAL_TEXT);
	            fm = l.getFontMetrics(l.getFont());
	            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	            l.setLocation(10, 10);
	            l.setVisible(false);
	            classPanel.add(l);
	            l.setVisible(true);
	
	            SchoolClass[] classes = t.getClasses();
	            classLinkedList = new ClassLinkedLabel[classes.length];
	            ClassLinkedLabel cll;
	
	
	            for (int i = 0; i < classes.length; i++) {
	                cll = new ClassLinkedLabel(classes[i]);
	                cll.addActionListener(this);
	                classLinkedList[i] = cll;
	                cll.setFont(GuiConstants.NORMAL_TEXT);
	                fm = cll.getFontMetrics(cll.getFont());
	                cll.setSize(fm.stringWidth(cll.getText()) + 10, fm.getHeight());
	                cll.setLocation(20, 26 + i * (cll.getSize().height + 3));
	                cll.setVisible(false);
	                classPanel.add(cll);
	                cll.setVisible(true);
	            }
	
	            classPanel.setSize(this.getSize().width - 1, 26 + classes.length * (l.getSize().height + 3));
	        }
	        classPanel.setVisible(false);
	        this.add(classPanel);
	        classPanel.setVisible(true);
		}
		/*if(GuiCreator.instance().getUser() instanceof Admin){
	        Admin t = (Admin) GuiCreator.instance().getUser();
	        if ((t.getClasses() != null) && (t.getClasses().length != 0)) {
	            l = new Label(TextMapper.getText(TextMapper.GUIMNU_CLASS_RESULTS)
	                    + ":");
	            l.setFont(GuiConstants.NORMAL_TEXT);
	            fm = l.getFontMetrics(l.getFont());
	            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	            l.setLocation(10, 10);
	            l.setVisible(false);
	            classPanel.add(l);
	            l.setVisible(true);
	
	            SchoolClass[] classes = t.getClasses();
	            classLinkedList = new ClassLinkedLabel[classes.length];
	            ClassLinkedLabel cll;
	
	
	            for (int i = 0; i < classes.length; i++) {
	                cll = new ClassLinkedLabel(classes[i]);
	                cll.addActionListener(this);
	                classLinkedList[i] = cll;
	                cll.setFont(GuiConstants.NORMAL_TEXT);
	                fm = cll.getFontMetrics(cll.getFont());
	                cll.setSize(fm.stringWidth(cll.getText()) + 10, fm.getHeight());
	                cll.setLocation(20, 26 + i * (cll.getSize().height + 3));
	                cll.setVisible(false);
	                classPanel.add(cll);
	                cll.setVisible(true);
	            }
	
	            classPanel.setSize(this.getSize().width - 1, 26 + classes.length * (l.getSize().height + 3));
	        }
	        classPanel.setVisible(false);
	        this.add(classPanel);
	        classPanel.setVisible(true);
		}*/
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        if (e.getSource() instanceof ClassLinkedLabel) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getResultPanel(((ClassLinkedLabel) e.getSource()).getSchoolClass());
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        } else if (e.getSource() == classManagementButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getClassPanel();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
       } else if (e.getSource() == courseManagementButton) {
           GuiCreator.instance().setWait();
           CenterSubPanel cp = GuiCreator.instance().getCourseManagementPanel();
           center.loadCenter(cp);
           GuiCreator.instance().setReady();           
       }
    }
    
    public void hideClassList() {
        classPanel.setVisible(false);
    }
    
    public void showClassList() {
        classPanel.setVisible(true);      
    }

}