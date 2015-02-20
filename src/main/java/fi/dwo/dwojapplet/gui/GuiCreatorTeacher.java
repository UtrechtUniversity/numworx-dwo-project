/*
 * Created on Mar 30, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import fi.beans.base64code.StringCodeObject;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Admin;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.CourseManagementAction;
import fi.dwo.dwojapplet.gui.action.ScoManagementAction;
import fi.dwo.dwojapplet.gui.action.ScoParameterAction;

/**
 * This class implements Teacher-specific methods of the GuiCreator.
 * These methods are implemented in this specific subclass because of imports.<br>
 * Now, a normal user must not import the teacher-specific classes and must not load them.
 * @author M.J.B. Kupers
 *
 */
public class GuiCreatorTeacher extends GuiCreator {

    public static final class LazyAppletConfig extends AppletConfig {
		private Sco sco;

		/* (non-Javadoc)
		 * @see fi.dwo.client.domain.AppletConfig#getLaunchdata()
		 */
		public String getLaunchdata() {
			if(super.getLaunchdata() != null)
				return super.getLaunchdata();
			String result = StringCodeObject.encodeObjectToString(sco.getLaunchdata());
			this.setLaunchdata(result);
			return result;
		}

		/**
		 * @param sco the sco to set
		 */
		void setSco(Sco sco) {
			this.sco = sco;
		}
	}

	private boolean noAdmin, readOnly;
	
	/**
     * @param dwo
     */
    public GuiCreatorTeacher(DwoIF dwo) {
        super(dwo);
        noAdmin = ! dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT);
        readOnly =  ! dwo.getUser().hasRight(User.MODIFY_MODULES_RIGHT);
    }

    /**
     * Returns a MenuPanel. The type of the menupanel depends on the type of
     * user. <BR>
     * If the user is null, a GuestMenuPanel is returned. <BR>
     * If the user is a teacher, a TeacherMenuPanel is returned. <BR>
     * Otherwise, a normal MenuPanel is returned. <BR>
     * <BR>
     * The menupanel shows all the menu-options for the type of user.
     * 
     * @return The MenuPanel with all the menu-options for the type user.
     */
    @Override
    public GuestMenuPanel getMenuPanel() {
        User u = dwo.getUser();

        if (u instanceof Teacher) {
            return new TeacherMenuPanel(dwo);
        }
        else if (u instanceof Admin) {
            return new AdminMenuPanel();
        } 
        else {
            return super.getMenuPanel();
        }
    }
    
    /**
     * Returns a panel for changing the profile of the user. If the user is a
     * teacher, it returns a TeacherProfilePanel. Otherwise, it returns a
     * ProfilePanel for the user.
     * 
     * @return fi.dwo.client.gui.CenterSubPanel
     *  
     */
    @Override
    public CenterSubPanel getProfilePanel() {
        dwo.setWait();
        CenterSubPanel csb;
        csb = new TeacherProfilePanel();
        dwo.setReady();
        return csb;
    }
    
    /**
     * Returns a panel with results for users of the classes of the teacher. The
     * teacher can analyse these results.
     * 
     * @return A panel with results for users of the classes of the teacher.
     */
    @Override
    public CenterSubPanel getResultPanel() {
        dwo.setWait();
        CenterSubPanel csp = new ResultsModulePanel(dwo.getResultsModule());
        dwo.setReady();
        return csp;
    }

    /**
     * Returns a panel with results for users of the classes of the teacher. The
     * teacher can analyse these results.
     * 
     * @return A panel with results for users of the classes of the teacher.
     */
    @Override
    public CenterSubPanel getResultPanel(Course c) {
        Course[] courses = new Course[1];
        courses[0] = c;
        dwo.setWait();
        CenterSubPanel csp = new ResultsModulePanel(dwo.getResultsModule(courses));
        dwo.setReady();
        return csp;
    }

    /**
     * Returns a panel with results for users of the specified class. 
     * The teacher can analyse these results.
     * 
     * @param schoolClass The SchoolClass to show the results from.
     * @return A panel with results for users of the specified class.
     */
    @Override
    public CenterSubPanel getResultPanel(SchoolClass schoolClass) {
        dwo.setWait();
        CenterSubPanel csp = new ResultsModulePanel(dwo.getResultsModule(schoolClass));
        dwo.setReady();
        return csp;
    }

    /**
     * Returns a panel representing the specified SchoolClass.
     * 
     * @param c The SchoolClass of the panel to return.
     * @return A panel representing the specified SchoolClass.
     *  
     */
    @Override
    public CenterSubPanel getClassUsersPanel(SchoolClass c) {
        return new ClassUsersPanel(c);
    }

    /**
     * Returns a panel for managing schoolclasses.
     * 
     * @return A panel for managing schoolclasses.
     *  
     */
    @Override
    public CenterSubPanel getClassPanel() {
        return new ClassPanel();
    }

    /**
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    @Override
    public CenterSubPanel getCourseManagementPanel() {
        Course[] editableCourses = dwo.getEditableCourses();
        if(editableCourses != null) {
            return new CourseManagementPanel(editableCourses, ModuleTreePanel.SCHOOL_MODULES);
        } else {
            return null;
        }
    }
    @Override
    public CenterSubPanel getCourseManagementPanel(CourseMap map)
    {
 // constructor cannot sort, we do!
    	CourseManagementPanel panel = new CourseManagementPanel(map.getChildren(), map);
    	panel.setMap(map);
		return panel;
    }
    
    
 
    /** 
     * Gebruik de andere. 
     * @deprecated 
     * 
     * @return LazyAppletConfig[]
     */
    public AppletConfig[] getAppletConfigFromTeacher_oud() {
    	Vector ac = new Vector();
    	LazyAppletConfig config;
    	Course[] courses = dwo.getEditableCourses();
    	for (int i = 0; i < courses.length; i++) {

    		Course course = courses[i];
    		course.loadScos();
			Sco[] scos = course.getScoList();
			for (int j = 0; j < scos.length; j++) {
				config = new LazyAppletConfig();
				Sco sco = scos[j];
				String name = sco.getScoName();
				int    aid  = sco.getAppletID();
				int    sid  = sco.getScoID();
				config.setSco(sco);
				config.setAppletID(aid);
				config.setAppletConfigID(-sid); // HACK HACK negatief = scoid
				config.setName(name);
				ac.addElement(config);
			}
    	}
    	// TODO sorteren....
    	AppletConfig[] result = new AppletConfig[ac.size()];
    	ac.toArray(result);
    	return result;
    }
  
    @Override
    public AppletConfig[] getAppletConfigFromTeacher() {
    	Vector ac = new Vector();
    	Sco[] scos = dwo.getEditableScos();
		for (int j = 0; j < scos.length; j++) {
			Sco sco = scos[j];
	    	AppletConfig config = getAppletConfigFromSco(sco);
			ac.addElement(config);
		}
    	AppletConfig[] result = new AppletConfig[ac.size()];
    	ac.toArray(result);
    	return result;
    }

    @Override
	public AppletConfig getAppletConfigFromSco(Sco sco) {
		LazyAppletConfig config;
		config = new LazyAppletConfig();
		String name = sco.getScoName();
		int    aid  = sco.getAppletID();
		int    sid  = sco.getScoID();
		config.setSco(sco);
		config.setAppletID(aid);
		config.setAppletConfigID(-sid); // HACK HACK negatief = scoid
		config.setName(name);
		return config;
	}
    
    /**
     * @param name
     * @param description
     * @return fi.dwo.client.domain.Course
     */
    @Override
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        Course course = dwo.addCourse(name, description, parent, isMap);
        getMainPanel().getCenter().addCourse(course);
		return course;
    }

    /**
     * @param course
     * @return boolean
     */
    @Override
    public boolean updateCourse(Course course) {
    	boolean b = dwo.updateCourse(course);
    	if(b)
            getMainPanel().getCenter().updateCourse(course);
		return b;
    }

    /**
     * @param course
     * @return boolean
     */
    @Override
    public boolean deleteCourse(Course course) {
    	getMainPanel().getCenter().deleteCourse(course);
        return dwo.deleteCourse(course);
    }

    /**
     * @param sco
     * @return boolean
     */
    @Override
    public boolean deleteSco(Sco sco) {
    	Course c = sco.getCourse();
        boolean b = dwo.deleteSco(sco);
        getMainPanel().getCenter().updateCourse(c);
		return b;
    }

    /**
     * @param course
     * @return fi.dwo.client.gui.CenterSubPanel)
     */
    @Override
    public CenterSubPanel getScoManagementPanel(Course course) {
        return new ScoManagementPanel(course);
    }

    /**
     * @param sco
     * @return boolean
     */
    @Override
    public boolean updateSco(Sco sco) {
        return dwo.updateSco(sco);
    }

    /**
     * @param sco
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    @Override
    public void loadParameterManagementPanel(Sco sco) {
        new ParameterManagementPanel(sco);
    }

    /**
     * @return fi.dwo.client.domain.DWO
     */
    @Override
    public DwoIF getDWO() {
        return dwo;
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    @Override
    public AppletConfig[] getAppletConfig() {
        return dwo.getAppletConfig();
    }

    /**
     * @param appletConfig
     * @return fi.dwo.client.gui.ScoPanel
     */
    @Override
    public ScoPanel previewSco(AppletConfig appletConfig) {
        return dwo.previewSco(appletConfig);
    }

    /**
     * @param sco
     * @return fi.dwo.client.gui.ScoPanel
     */
    @Override
    public ScoPanel previewSco(Sco sco) {
        return dwo.previewSco(sco);
    }

    /**
     * @param appletID
     * @param name
     * @param description
     * @return fi.dwo.client.domain.Sco
     */
    @Override
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean showScore) {
        Sco result = dwo.addSco(course, appletConfig, name, description, showScore);
        if(result != null) 
        	getMainPanel().getCenter().updateCourse(course);
		return result;
    }

    /**
	 * Verwissel de sequencenrs van twee Sco's.
	 * De Sco's moeten tot dezelfe Course behoren.
	 * @param sco1 Sco
	 * @param sco2 Sco
	 * @return boolean: succes of gefaald
	 */
    @Override
	public boolean swapSco(Sco sco1, Sco sco2) {
		return dwo.swapSco(sco1, sco2);
	}

    @Override
	public JComponent getButtonBox(CourseChoicePanel courseChoisePanel) {
			Object userObject = courseChoisePanel.getUserObject();
			if(userObject == ModuleTreePanel.ALLE_MODULES)
				return null;
// DWO profiel manager hier wel	
			if(noAdmin)
			{   if(userObject == ModuleTreePanel.STANDAARD_DWO_MODULES)
				   return null;
			    if(userObject instanceof Course && ((Course)userObject).getSchoolID() == 0)
				   return null;
			}
			return fx(new JButton(new CourseManagementAction(courseChoisePanel)));
	}

    @Override
	public JComponent fx(JComponent b) { 
		if(!CenterPanel.isIconizer() || readOnly)
			return null;
		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(b);
		box.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0)); // Meten!
		return box;
	}
	
    @Override
	public JComponent getButtonBox(CoursePanel coursePanel) {
		if(noAdmin && coursePanel.getCourse().getSchoolID() == 0)
			return null;
		return fx(new JButton(new ScoManagementAction(coursePanel)));
	}
   
    @Override
	public JComponent getButtonBox(ScoPanel scoPanel) {
		if(noAdmin && scoPanel.getSco().getCourse().getSchoolID() == 0)
			return null;
		String lessonMode = scoPanel.getSco().getLessonMode();
		if(Sco.BROWSE.equals(lessonMode))
		{
			Box box = Box.createHorizontalBox();
			JLabel lab = new JLabel(TextMapper.getText("PREVIEW"));
			lab.setVerticalAlignment(JLabel.BOTTOM);
			lab.setForeground(Color.red);
			lab.setFont(new Font("SansSerif", Font.BOLD, 20));
			box.add(lab);
			box.add(Box.createHorizontalStrut(10));
			if(Sco.BROWSE == lessonMode) // FIXME SUBTIEL verschil 
			{ 
				box.add(new JButton(new PreviewAction(scoPanel)));
			} else {
				box.add(new JButton(new ScoParameterAction(scoPanel)));
			}
			return fx(box);
		}
		return fx(new JButton(new ScoParameterAction(scoPanel)));
	}
	
    @Override
	public void updateLogo(Course c) { 
		if( dwo.updateLogo(c) )
			;
	}
}
