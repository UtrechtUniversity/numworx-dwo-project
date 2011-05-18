/*
 * Created on Mar 30, 2005
 *
 */
package fi.dwo.client.gui;

import java.util.Vector;

import fi.beans.base64code.StringCodeObject;
import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.Admin;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.PersistenceFacade;

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

	/**
     * @param dwo
     */
    public GuiCreatorTeacher(DwoIF dwo) {
        super(dwo);
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
    public CenterSubPanel getClassUsersPanel(SchoolClass c) {
        return new ClassUsersPanel(c);
    }

    /**
     * Returns a panel for managing schoolclasses.
     * 
     * @return A panel for managing schoolclasses.
     *  
     */
    public CenterSubPanel getClassPanel() {
        return new ClassPanel();
    }

    /**
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    public CenterSubPanel getCourseManagementPanel() {
        Course[] editableCourses = dwo.getEditableCourses();
        if(editableCourses != null) {
            return new CourseManagementPanel(editableCourses, ModuleTreePanel.SCHOOL_MODULES);
        } else {
            return null;
        }
    }
    public CenterSubPanel getCourseManagementPanel(CourseMap map)
    {
    	return new CourseManagementPanel(map);
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
  
    public AppletConfig[] getAppletConfigFromTeacher() {
    	Vector ac = new Vector();
    	LazyAppletConfig config;
    	Sco[] scos = dwo.getEditableScos();
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
    	AppletConfig[] result = new AppletConfig[ac.size()];
    	ac.toArray(result);
    	return result;
    }
    
    /**
     * @param name
     * @param description
     * @return fi.dwo.client.domain.Course
     */
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        Course course = dwo.addCourse(name, description, parent, isMap);
        getMainPanel().getCenter().addCourse(course);
		return course;
    }

    /**
     * @param course
     * @return boolean
     */
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
    public boolean deleteCourse(Course course) {
    	getMainPanel().getCenter().deleteCourse(course);
        return dwo.deleteCourse(course);
    }

    /**
     * @param sco
     * @return boolean
     */
    public boolean deleteSco(Sco sco) {
        return dwo.deleteSco(sco);
    }

    /**
     * @param course
     * @return fi.dwo.client.gui.CenterSubPanel)
     */
    public CenterSubPanel getScoManagementPanel(Course course) {
        return new ScoManagementPanel(course);
    }

    /**
     * @param sco
     * @return boolean
     */
    public boolean updateSco(Sco sco) {
        return dwo.updateSco(sco);
    }

    /**
     * @param sco
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    public void loadParameterManagementPanel(Sco sco) {
        new ParameterManagementPanel(sco);
    }

    /**
     * @return fi.dwo.client.domain.DWO
     */
    public DWO getDWO() {
        return null;
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    public AppletConfig[] getAppletConfig() {
        return dwo.getAppletConfig();
    }

    /**
     * @param appletConfig
     * @return fi.dwo.client.gui.ScoPanel
     */
    public ScoPanel previewSco(AppletConfig appletConfig) {
        return dwo.previewSco(appletConfig);
    }

    /**
     * @param sco
     * @return fi.dwo.client.gui.ScoPanel
     */
    public ScoPanel previewSco(Sco sco) {
        return dwo.previewSco(sco);
    }

    /**
     * @param appletID
     * @param name
     * @param description
     * @return fi.dwo.client.domain.Sco
     */
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean showScore) {
        return dwo.addSco(course, appletConfig, name, description, showScore);
    }

    /**
	 * Verwissel de sequencenrs van twee Sco's.
	 * De Sco's moeten tot dezelfe Course behoren.
	 * @param sco1 Sco
	 * @param sco2 Sco
	 * @return boolean: succes of gefaald
	 */
	public boolean swapSco(Sco sco1, Sco sco2) {
		return dwo.swapSco(sco1, sco2);
	}
   
}
