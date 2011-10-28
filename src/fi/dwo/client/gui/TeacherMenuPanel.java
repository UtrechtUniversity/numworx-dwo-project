// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java

package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
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

    class DeleteAction extends AbstractAction {

		private CourseMap map;
		private CourseMap parent;
		Course course;
		int row = 0;
		Sco sco;
		
		DeleteAction(CourseMap map) {
			super("Delete");
			this.map = map;
			Object o = map.getUserObject();
			if(o instanceof Sco)
			{	sco = (Sco) o;
				parent = sco.getCourse();
				String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCO);
				Object[] arguments = { sco.toString() };
				putValue(NAME, MessageFormat.format(format, arguments));

			}
			else if(o instanceof Course)
			{
				course = (Course) o;
				parent = map.getParentMap();
				Course[] courses = parent.getChildren();
				for (row = 0; row < courses.length; row++) {
					if(courses[row] == course)
						break;
				}
				String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_COURSE);
				Object[] arguments = { o.toString() };
				putValue(NAME, MessageFormat.format(format, arguments));
			}
		}

		public void actionPerformed(ActionEvent e) {
// verwijder clipboard als die wordt verwijdert
			if (clipboard == map)
			{	clipboard = null;
				cmd = null;
			}
	
			if(course != null) 
			{
                if (GuiCreator.instance().deleteCourse(course)) {
                    parent.removeChild(row);
                 }

			} else if (sco != null)
			{
				GuiCreator.instance().deleteSco(sco);
				center.updateCourse((Course) parent);
				return;
			}
			center.updateMap(parent);
		}

	}

	class NewAction extends AbstractAction {

		private final Course STANDARD_MAP = new Course();
		private CourseMap map;
		boolean ismap, submap;
		Course course;
		
		
		public NewAction(CourseMap map, boolean submap) {
			super();
			this.map = map;
			this.submap = submap;
			if(map instanceof Course)
			{ course = (Course) map.getUserObject();
			  ismap = course.isWithChildren();
			} else
			if (map == ModuleTreePanel.SCHOOL_MAP)
			{
				course = null;
				ismap = true;
			} else {
				course = STANDARD_MAP;
				ismap = true;
			}
			
			if(submap)
				putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_MAP));
			else if(ismap)
				putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_COURSE));
			else 
				putValue(NAME, TextMapper.getText(TextMapper.GUIS_ADD_SCO));
		}

		public void actionPerformed(ActionEvent e) {
// FIXME werkt niet goed, als updateMap werkt op een toplevel map.
			if(submap)
			{
				Course child = CourseNameDialog.addMap(TeacherMenuPanel.this, course);
				if(child != null) 
				{
					map.addChild(child);
					center.updateMap(map);
				}
			}
			else if(ismap)
			{
				Course child = CourseNameDialog.addCourse(TeacherMenuPanel.this, course);
				if(child != null)
				{
					map.addChild(child);
					center.updateMap(map);
				}
			}
			else
				AddScoDialog.addSco(TeacherMenuPanel.this, course);

		}

	}

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
		hasAdminRight = dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT);
		schoolID = dwo.getUser().getSchool().getSchoolID();
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
			if(u == ModuleTreePanel.STANDAARD_DWO_MODULES && ! hasAdminRight)
				return;
			CenterSubPanel cp = new CourseManagementPanel(node);
			center.loadCenter(cp);
		} else
		if(u instanceof Course)
		{
            Course c = (Course) u;
            if(c.getSchoolID()!= 0 || hasAdminRight) // allowed?
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
            if(c.getSchoolID()!= 0 || hasAdminRight) // allowed?
            	instance.loadParameterManagementPanel(s);
		}
	}

	
	private CourseMap clipboard;
	private String cmd;
	private boolean hasAdminRight;
	private int schoolID;
	
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
	
	class PasteAction extends AbstractAction 
	{
		CourseMap map;
		private GuiCreator instance;

		public void actionPerformed(ActionEvent e) {
			Object object = map.getUserObject();
			Object clip = clipboard.getUserObject();
			System.out.println( cmd  + " " + clip + " into " + object);
			instance = GuiCreator.instance();
			if("cut".equals(cmd))
			{
				if(clip instanceof Course)
				{
					cutCourse((Course)clip, object);
				} else if(clip instanceof Sco && object instanceof Course)
				{
					Course course = (Course) object;
					Sco sco = (Sco)clip;
					if (course.isWithChildren() ||
						  sco.getCourse() == course && sco.getSequencenr()==course.getScoList().length)
						return;
					cutSco( sco, course);
				} else if(clip instanceof Sco && object instanceof Sco)
				{
					Sco before = (Sco)object;
					Sco sco = (Sco) clip;
					if(sco.getID() != before.getID())
						cutSco(sco, before);
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
					copySco(course, sco);
				} else
				if(clip instanceof Course && object instanceof String)
				{
					copyCourseTop( (Course) clip, object == ModuleTreePanel.STANDAARD_DWO_MODULES);
				} else if(clip instanceof Course && object instanceof Course)
				{
					Course source = (Course) clip;
					Course dest   = (Course) object;
					if(!dest.isWithChildren())
						return;
					if(dest.getSchoolID() == 0 && hasAdminRight) return;
					// TODO check copy parent into child.
					copyCourseMap(dest, source);
				}
				
			}
		}
		private void copySco(Course course, Sco sco) {
			AppletConfig config = instance.getAppletConfigFromSco(sco);
			String name = config.getName();
			name = CourseManagementPanel.replaceDuplicate(name, course.getScoNames());
			instance.addSco(course, config, name, sco.getDescription(), sco.isShowScore());
		}
/**
 * 
 * @param course
 * @param b true if standaard modules.
 */
		private void copyCourseTop(Course course, boolean b) {
			CourseMap oldmap = getParentMap(course);
			if(oldmap.getUserObject() == map.getUserObject()) // copy/paste in zelfde map?
				return;
			if(b && !hasAdminRight)
				return;
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, map.getChildNames());
			boolean isMap = course.isWithChildren();
			Course parent = b?new Course():null;
			String description = course.getDescription();
			Course c = instance.addCourse(name, description, parent, isMap);
			map.addChild(c);
			center.updateMap(map);
			if(isMap) {
				copyCourseMap(c, course.getChildren());
			} else {
				copySco(c, course);
			}
			
			// recurse copyCourseMap, copySco
		}


		private void copyCourseMap(Course c, Course[] children) {
			for (int i = 0; i < children.length; i++) {
				copyCourseMap(c, children[i]);
			}
		}
		
		private void copyCourseMap(Course dest, Course course) {
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, dest.getChildNames());
			boolean isMap = course.isWithChildren();
			String description = course.getDescription();
			Course c = instance.addCourse(name, description, dest, isMap);
			if(c == null)
			{
				System.err.println("copyCourseMap failed: "+course + ", " + dest + ", " + isMap);
				return;
			}
			map.addChild(c);
			center.updateMap(map);
			if(isMap) {
				copyCourseMap(c, course.getChildren());
			} else {
				copySco(c, course);
			}
				
			
		}
		private void copySco(Course dest, Course course) {
			Sco[] list = course.getScoList();
			for (int i = 0; i < list.length; i++) {
				copySco(dest, list[i]);
			}
	
}
		private void cutSco(Sco sco, Course course) {
			if(course.getScoList() == null) course.loadScos();
			sco.setSequencenr(course.getScoList().length+1); // to the end.
			cutSco_1(sco, course);			
		}

		private void cutSco_1(Sco sco, Course course) {
			Course old = sco.getCourse();
			sco.setCourse(course);
			if(old.getID() != course.getID())
			{
				String name = sco.getScoName();
				name = CourseManagementPanel.replaceDuplicate(name, course.getScoNames());
				sco.setName(name);
			}
			instance.updateSco(sco);
//			old.loadScos(); course.loadScos(); // refresh sco's (zonder dbaccess mogelijk?)
			center.updateCourse(old);
			center.updateCourse(course);
		}

		private void cutSco(Sco sco, Sco before) {
			Course course = before.getCourse();
			sco.setSequencenr(before.getSequencenr()); // before that sco.
			cutSco_1(sco, course);
		}

		private void cutCourse(Course course, Object object) {
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
			center.updateMap(map);
			center.updateMap(oldmap);
			//cmd = "copy"; // 2x paste wordt altijd copy
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
			super(TextMapper.getText("paste"));
			this.map = object;
		}
	}
	
	public JPopupMenu getPopup(CourseMap map) {
		Object object = map.getUserObject();
		JPopupMenu m = new JPopupMenu();
		JMenuItem item;
		if(object == ModuleTreePanel.SCHOOL_MODULES)
		{
			m.add(new JMenuItem(new NewAction(map, true)));
			m.add(new JMenuItem(new NewAction(map, false)));	
		} else if (object == ModuleTreePanel.STANDAARD_DWO_MODULES)
		{
			if( hasAdminRight)
			{
				m.add(new JMenuItem(new NewAction(map, true)));
				m.add(new JMenuItem(new NewAction(map, false)));				
			}
		}
		
		int school = -1;
		if(object instanceof Course)
			school = ((Course) object).getSchoolID();
		else if (object instanceof Sco)
			school = ((Sco) object).getCourse().getSchoolID();
		boolean update = school==schoolID || hasAdminRight;
		if(object instanceof Course) 
		{	
			Course course = (Course)object;
			if(update)
			{
				if( course.isWithChildren())
					m.add(new JMenuItem(new NewAction(map, true)));
				item = new JMenuItem(new NewAction(map, false));
				m.add(item);
			}
		}
		if(object instanceof Course || object instanceof Sco)
		{
			ActionListener listener = new CutCopyAction(map);
			if(update) 
			{   item = new JMenuItem(TextMapper.getText("cut")); 
				item.setActionCommand("cut");
				item.addActionListener(listener);m.add(item);
			}
			item = new JMenuItem(TextMapper.getText("copy"));
			item.setActionCommand("copy");
			item.addActionListener(listener);m.add(item);
		}
		if(clipboard != null) {
			Object uo = clipboard.getUserObject();
			boolean acceptable = update;
			if(object == ModuleTreePanel.SCHOOL_MODULES)
				acceptable = uo instanceof Course;
			else if(object == ModuleTreePanel.ALLE_MODULES)
				acceptable = false;
			else if(object == ModuleTreePanel.STANDAARD_DWO_MODULES)
				acceptable &= uo instanceof Course;
			else if(uo instanceof Sco)
				acceptable &= object instanceof Sco || object instanceof Course &&  !((Course)object).isWithChildren();
			else if (object instanceof Course)
				acceptable &= ((Course)object).isWithChildren();
			if(acceptable)
			{	item = new JMenuItem(new PasteAction(map));
			    m.add(item);
			}	
		}
		if(update && (object instanceof Course || object instanceof Sco))
		{
			m.add(new JMenuItem(new DeleteAction(map)));
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