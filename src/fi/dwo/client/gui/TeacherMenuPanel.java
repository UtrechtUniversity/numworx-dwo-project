// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java

package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

/**
 * This class is the menupanel for the teacher who logged in.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class TeacherMenuPanel extends MenuPanel implements SelectStrategy {

    private static final Border TITLE_BORDER = BorderFactory.createEmptyBorder(0, 10, 0, 0);
    private static final Border CLASS_BORDER = BorderFactory.createEmptyBorder(0, 20, 0, 0);
	private JButton classManagementButton;
    
    private JButton courseManagementButton;

    private ClassLinkedLabel[] classLinkedList;
    
    private JScrollPane classPanel;

    /* (non-Javadoc)
	 * @see fi.dwo.client.gui.MenuPanel#createButtons()
	 */
	protected void createMenuButtons() {
		super.createMenuButtons();
		createGap();
        /* Add ClassManagement button */
        classManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASS_MANAGEMENT));
        classManagementButton.addActionListener(this);
        this.add(classManagementButton);
        /* Als dwo in Deeplink mode, geen coursemanagement */
        if(dwo.getCourseViewNr()>0)
        	return;
        
        createGap();
        /* Add CourseManagement Button */
        courseManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_COURSE_MANAGEMENT));
        courseManagementButton.addActionListener(this);
        add(courseManagementButton);
	}

	/**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * MenuPanel) and buttons to show the result of students, and to add a class.
     */
    public TeacherMenuPanel(DwoIF dwo) {
        super(dwo);
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
        Box classBox = Box.createVerticalBox();
        classPanel = new JScrollPane(classBox);
        classPanel.setDoubleBuffered(false);
        classPanel.setOpaque(false);
        classPanel.getViewport().setOpaque(false);
        classPanel.setViewportBorder(null);
        classPanel.setBorder(null);
        /* Add class-info */
        if(GuiCreator.instance().getUser() instanceof Teacher){
	        Teacher t = (Teacher) GuiCreator.instance().getUser();
	        if ((t.getClasses() != null) && (t.getClasses().length != 0)) {
	            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_CLASS_RESULTS)
	                    + ":");
	            l.setOpaque(false);
	            l.setFont(GuiConstants.NORMAL_TEXT);
	            l.setBorder(TITLE_BORDER);
	            classBox.add(l);
	
	            SchoolClass[] classes = t.getClasses();
	            classLinkedList = new ClassLinkedLabel[classes.length];
	            ClassLinkedLabel cll;
	
	
	            for (int i = 0; i < classes.length; i++) {
	                cll = new ClassLinkedLabel(classes[i]);
	                cll.setBorder(CLASS_BORDER);
	                cll.addActionListener(this);
	                classLinkedList[i] = cll;
	                cll.setFont(GuiConstants.NORMAL_TEXT);
	                classBox.add(cll);
	            }
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
            center.reset();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
        } else if (e.getSource() == classManagementButton) {
            GuiCreator.instance().setWait();
            CenterSubPanel cp = GuiCreator.instance().getClassPanel();
            center.reset();
            center.loadCenter(cp);
            GuiCreator.instance().setReady();
       } else if (e.getSource() == courseManagementButton) {
           GuiCreator.instance().setWait();
           CenterSubPanel cp = GuiCreator.instance().getCourseManagementPanel();
           center.loadCenter(cp);
           center.setStrategy(this);
           GuiCreator.instance().setReady();           
       }
    }
    
    
    
    public void hideClassList() {
        classPanel.setVisible(false);
    }
    
    public void showClassList() {
        classPanel.setVisible(true);      
    }

	public void nodeSelected(CourseMap node) {
		Object u = node.getUserObject();
		GuiCreator instance = GuiCreator.instance();
		if(u instanceof String)
		{
			if(u == ModuleTreePanel.ALLE_MODULES)
				return;
			if(u == ModuleTreePanel.STANDAARD_DWO_MODULES && ! instance.getUser().hasRight(User.PROFILE_ADMIN_RIGHT))
				return;
			CenterSubPanel cp = new CourseManagementPanel(node.getChildren(), u);
			center.loadCenter(cp);
		} else
		if(u instanceof Course)
		{
            Course c = (Course) u;
            if(c.getSchoolID()!= 0 || instance.getUser().hasRight(User.PROFILE_ADMIN_RIGHT)) // allowed?
            {
            	if(c.isWithChildren())
            	{
            		center.loadCenter(instance.getCourseManagementPanel(c));
            	} else
            		center.loadCenter(instance.getScoManagementPanel(c));
            }
		} else 
		if( u instanceof Sco)
		{
			Sco s = (Sco) u;
			Course c = s.getCourse();
            if(c.getSchoolID()!= 0 || instance.getUser().hasRight(User.PROFILE_ADMIN_RIGHT)) // allowed?
            	instance.loadParameterManagementPanel(s);
		}
	}

}