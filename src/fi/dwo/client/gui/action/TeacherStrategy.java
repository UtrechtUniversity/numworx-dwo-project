package fi.dwo.client.gui.action;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.Descriptor;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.CourseChoisePanel;
import fi.dwo.client.gui.CoursePanel;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ModuleTreePanel;
import fi.dwo.client.gui.SelectStrategy;
import fi.dwo.client.system.TextMapper;

public class TeacherStrategy implements SelectStrategy{

	static class NO_SCO implements  CenterSubPanel {

		private Object userObject;
		
		
		NO_SCO(Object userObject) {
			super();
			this.userObject = userObject;
		}

		private JPanel panel = new JPanel();
		@Override
		public void stateChanged(ChangeEvent e) {
		}

		@Override
		public void end() {
		}

		@Override
		public Component getHeaderPanel() {
			return panel;
		}

		@Override
		public void setCenterPanel(CenterPanel centerPanel) {
		}

		@Override
		public JComponent getComponent() {
			return panel;
		}

		@Override
		public Object getUserObject() {
			return this;
		}};

	public TeacherStrategy() {
		hasAdminRight = getUser().hasRight(User.PROFILE_ADMIN_RIGHT);
		School school = getUser().getSchool();
		if(school != null) schoolID = school.getSchoolID();
		// TODO else // Admin / Guest
	}

	private User getUser() {
		return GuiCreator.instance().getUser();
	}

	private boolean hasAdminRight;
	private int schoolID;
	
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
		if(Clipboard.getClipboard() != null) {
			Object uo = Clipboard.getClipboard().getUserObject();
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
			    if(object instanceof Sco && uo instanceof Sco) {
			    	item = new JMenuItem(new MergeAction(map));
			    	if (item.isEnabled()) m.add(item);
			    }
			}	
		}
		if(object == ModuleTreePanel.SCHOOL_MODULES 
			||	(update && object instanceof Course && ((Course) object).isWithChildren())
			||  (hasAdminRight && object == ModuleTreePanel.STANDAARD_DWO_MODULES)
		) {
			m.add(new JMenuItem(new CourseManagementAction(map)));
		} else if(update && object instanceof Course && !((Course)object).isWithChildren())
		{
			m.add(new JMenuItem(new ScoManagementAction((Course)object)));
		} else if(update && object instanceof Sco) 
		{
			m.add(new JMenuItem(new ScoParameterAction((Sco)object)));
		}
	
		if(update && (object instanceof Course || object instanceof Sco))
		{
			m.add(new JMenuItem(new RenameAction(map)));
			m.add(new JMenuItem(new DeleteAction(map)));
		}
		
		if(object instanceof Sco) {
			Action action = new OpenHtml5(map);
			if(action.isEnabled()) {
				m.addSeparator();
				m.add(new JMenuItem(action));
			}
		}
		
		return m;
	}

	public JPopupMenu nodeAction(CourseMap node) {
		if(getUser().hasRight(User.MODIFY_MODULES_RIGHT))
			return getPopup(node);
		else
			return null;
	}

    String lessonMode = Sco.NORMAL;
	String getLessonMode() {
		return lessonMode;
	}

	void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
	}

	public static class Bridge implements Descriptor {

		public Bridge(Descriptor profile, CourseMap parent) {
			super();
			this.profile = profile;
			this.parent = parent;
		}
		Descriptor profile;
		CourseMap  parent;

		/**
		 * @return
		 * @see fi.dwo.client.domain.Descriptor#getText()
		 */
		public String getText() {
			return profile.getText();
		}
		/**
		 * @return
		 * @see fi.dwo.client.domain.Descriptor#getHeader()
		 */
		public String getHeader() {
			return parent.getUserObject().toString(); // profile.getHeader();
		}
		/**
		 * @return
		 * @see fi.dwo.client.domain.CourseMap#getChildren()
		 */
		public CourseMap[] getChildren() {
			return parent.getChildren();
		}
		
	}
	
	
	public void nodeSelected(CourseMap node) {
		Clipboard.setSelection(node);
		Object value = node.getUserObject();
		CenterSubPanel panel;
		
		GuiCreator instance = GuiCreator.instance();
		CenterPanel center = instance.getMainPanel().getCenter();
		if(value instanceof Course)
		{
			Course c = (Course)value;
			if(c.isWithChildren())
			{
				panel = new CourseChoisePanel(c, c);
				center.loadCenter(panel);
			} else {
				CoursePanel cp = (CoursePanel) instance.getCoursePanel(c);
				cp.setLessonMode(getLessonMode());
				center.loadCenter(cp);
			}
		} else if (value instanceof String) // geen onderscheid tussen alle/school/standaard
		{
			if(value == ModuleTreePanel.ALLE_MODULES)
			{				
				panel = CourseChoisePanel.newInstance();
			} else 
			{
				panel = new CourseChoisePanel(new Bridge(instance.getDWO().getDwoProfile(), node), value);
				
			}
			center.loadCenter(panel); // undo side-effect 'select Alle_modules'
			
		} else if (value instanceof Sco) 
		{
			Sco s = (Sco)value;
			center.end();
		    CenterSubPanel csp = instance.getScoPanel(s);
		    if(csp != null) {
		    	s.setLessonMode(getLessonMode());
		        center.loadTotal(csp);
		    } else {
		    	center.loadTotal(new NO_SCO(value));
		    }
		}
	}

	
	
}
