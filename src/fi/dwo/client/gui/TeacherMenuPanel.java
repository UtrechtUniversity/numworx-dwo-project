// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java

package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
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
        if(dwo.getCourseViewNr()>0 || !dwo.getUser().hasRight(User.MODIFY_MODULES_RIGHT))
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
        if(dwo.getUser() instanceof Teacher){
	        Teacher t = (Teacher) dwo.getUser();
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
			if(u == ModuleTreePanel.STANDAARD_DWO_MODULES && ! dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT))
				return;
			CenterSubPanel cp = new CourseManagementPanel(node);
			center.loadCenter(cp);
		} else
		if(u instanceof Course)
		{
            Course c = (Course) u;
            if(c.getSchoolID()!= 0 || dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT)) // allowed?
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

	
	private CourseMap clipboard;
	private String cmd;
	
	class CutCopyAction implements ActionListener 
	{
		CourseMap object;

		public void actionPerformed(ActionEvent e) {
			clipboard = object;
			cmd = e.getActionCommand();
		}

		CutCopyAction(CourseMap object) {
			this.object = object;
		}
	}
	
	class PasteAction implements ActionListener 
	{
		CourseMap map;

		public void actionPerformed(ActionEvent e) {
			Object object = map.getUserObject();
			Object clip = clipboard.getUserObject();
			System.out.println( cmd  + " " + clip + " into " + object);
			GuiCreator instance = GuiCreator.instance();
			if("cut".equals(cmd))
			{
				if(clip instanceof Course)
				{
					Course course = (Course)clip;
					CourseMap oldmap = getParentMap(course);
					if(oldmap.getUserObject() == object) // cut/paste in zelfde map?
						return;
					int id = course.getID();
					if(object instanceof Course)
					{
						Course p = (Course)object;
						int pid = p.getParentID();
						while(pid != 0)
						{
							if(pid == id)
								return;			// course move into course
							try {
								pid = ((Course) PersistenceFacade.instance().get(pid, Course.class)).getParentID();
							} catch (PersistenceException e1) {
								e1.printStackTrace();
								return; 
							}
						}
					}
					String name = course.getName();
					name = CourseManagementPanel.replaceDuplicate(name, map.getChildNames());
					if( object instanceof String ) // toplevel
					{
						removeChild(oldmap, course);
						course.setParentID(0);
						course.setName(name);
						if(object.equals(ModuleTreePanel.STANDAARD_DWO_MODULES))
							course.setSchoolID(0);
						else // School Modules.
							course.setSchoolID(dwo.getUser().getSchool().getSchoolID());
					} else if( object instanceof Course)
					{
						Course map = (Course)object;
						if(map.isWithChildren())
						{
							course.setSchoolID(map.getSchoolID());
							course.setName(name);
							removeChild(oldmap, course);
							map.addChild(course);
							
						} else
							return;
					}
					instance.updateCourse(course);
					instance.getMainPanel().getCenter().updateMap(map);
					instance.getMainPanel().getCenter().updateMap(oldmap);
					//cmd = "copy"; // 2x paste wordt altijd copy
				}
			} else if("copy".equals(cmd))
			{
				if(clip instanceof Sco && object instanceof Course)
				{
					Course course = (Course)object;
					if(course.isWithChildren())
						return;
					Sco sco = (Sco)clip;
					// copy eigen activiteiten
					AppletConfig config = instance.getAppletConfigFromSco(sco);
					String name = config.getName();
					name = CourseManagementPanel.replaceDuplicate(name, course.getScoNames());
					instance.addSco(course, config, name, sco.getDescription(), sco.isShowScore());
				}
			}
			
			
		}

		private void removeChild(CourseMap oldmap, Course course) {
			Course[] children = oldmap.getChildren();
			for (int i = 0; i < children.length; i++) {
				if(children[i] == course)
				{
					oldmap.removeChild(i);
					break;
				}
			}
		}

		private CourseMap getParentMap(Course course) {
			int id = course.getParentID();
			if(id == 0)
			{
				id = course.getSchoolID();
				if(id == 0)
					return ModuleTreePanel.STANDAARD_DWO_MAP;
				else
					return ModuleTreePanel.SCHOOL_MAP;
			}
			try {
				return (CourseMap) PersistenceFacade.instance().get(id, Course.class);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e);
			} 
		}

		PasteAction(CourseMap object) {
			this.map = object;
		}
	}
	
	public JPopupMenu getPopup(CourseMap map) {
		Object object = map.getUserObject();
		JPopupMenu m = new JPopupMenu();
		JMenuItem item;
		if(object instanceof Course || object instanceof Sco)
		{
			ActionListener listener = new CutCopyAction(map);
			if(object instanceof Course)
			{item = new JMenuItem("cut"); item.addActionListener(listener);m.add(item);}
			if(object instanceof Sco)
			{item = new JMenuItem("copy"); item.addActionListener(listener);m.add(item);}
		}
		if(clipboard != null) {
			boolean acceptable = true;
			if(object == ModuleTreePanel.ALLE_MODULES)
				acceptable = false;
			else if(object == ModuleTreePanel.STANDAARD_DWO_MODULES)
				acceptable = clipboard instanceof Course && dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT);
			else if(clipboard instanceof Sco)
				acceptable = object instanceof Course &&  !((Course)object).isWithChildren();
			else if (object instanceof Course)
				acceptable = ((Course)object).isWithChildren();
			if(acceptable)
			{	item = new JMenuItem("paste");
			    item.addActionListener(new PasteAction(map));
			    m.add(item);
			}	
		}
		return m;
	}

	public JPopupMenu nodeAction(CourseMap node) {
		if(dwo.getUser().hasRight(User.MODIFY_MODULES_RIGHT))
			return getPopup(node);
		else
			return null;
		
	}

}