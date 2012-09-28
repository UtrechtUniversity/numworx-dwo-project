/*
 * Created on Mar 30, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Admin;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.action.CourseManagementAction;
import fi.dwo.client.gui.action.ScoManagementAction;
import fi.dwo.client.gui.action.ScoParameterAction;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.ScoException;

/**
 * This class implements Admin-specific methods of the GuiCreator.
 * These methods are implemented in this specific subclass because of imports.<br>
 * Now, a normal user must not import the admin-specific classes and must not load them.
 * @author M.J.B. Kupers
 *
 */
public class GuiCreatorAdmin extends GuiCreator {

    /**
     * @param dwo
     */
    public GuiCreatorAdmin(DwoIF dwo) {
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

        if (u instanceof Admin) {
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
     * Returns a panel representing the specified SchoolClass.
     * 
     * @param c The SchoolClass of the panel to return.
     * @return A panel representing the specified SchoolClass.
     *  
     */
    public CenterSubPanel getSchoolUsersPanel(SchoolClass c) {
        return null; //new ClassUsersPanel(c);
    }

    /**
     * Returns a panel for managing schoolclasses.
     * 
     * @return A panel for managing schoolclasses.
     *  
     */
    public CenterSubPanel getSchoolPanel() {
        return new SchoolPanel();
    }
    
    /**
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    public CenterSubPanel getCourseManagementPanel() {
        Course[] editableCourses = dwo.getEditableCourses();
        if(editableCourses != null) {
            return new CourseManagementPanel(editableCourses, ModuleTreePanel.STANDAARD_DWO_MODULES);
        } else {
            return null;
        }
    }

    /**
     * @param name
     * @param description
     * @return fi.dwo.client.domain.Course
     */
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        return dwo.addCourse(name, description, parent, isMap);
    }

    /**
     * @param course
     * @return boolean
     */
    public boolean updateCourse(Course course) {
        return dwo.updateCourse(course);
        // update ModuleTreePanel
    }

    /**
     * @param course
     * @return boolean
     */
    public boolean deleteCourse(Course course) {
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
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    public AppletConfig[] getAppletConfig() {
        return dwo.getAppletConfig();
    }
    
    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    public School[] getSchool() {
        return dwo.getSchool();
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

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.GuiCreator#deleteSchool(fi.dwo.client.domain.School)
	 */
	public boolean deleteSchool(School sc) {
		return dwo.deleteSchool(sc);
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

	public JComponent getButtonBox(CourseChoisePanel courseChoisePanel) {
		Object userObject = courseChoisePanel.getUserObject();
		if(userObject == ModuleTreePanel.ALLE_MODULES)
			return null;
		return fx(new JButton(new CourseManagementAction(courseChoisePanel)));
	}

	public JComponent fx(JComponent b) { 
		if(!CenterPanel.isIconizer() )
			return null;
		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(b);
		box.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0)); // Meten!
		return box;
	}

	public JComponent getButtonBox(CoursePanel coursePanel) {
		return fx(new JButton(new ScoManagementAction(coursePanel)));
	}
	
	public JComponent getButtonBox(ScoPanel scoPanel) {
		if(scoPanel.getSco().getLessonMode() == Sco.BROWSE)
		{
			Box box = Box.createHorizontalBox();
			JLabel lab = new JLabel("PREVIEW");
			lab.setVerticalAlignment(JLabel.BOTTOM);
			lab.setForeground(Color.red);
			lab.setFont(new Font("SansSerif", Font.BOLD, 20));
			box.add(lab);
			box.add(Box.createHorizontalStrut(10));
			box.add(new JButton(new PreviewAction(scoPanel)));
			return fx(box);
		}
		return fx(new JButton(new ScoParameterAction(scoPanel)));
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.GuiCreator#unsafeSaveSco(fi.dwo.client.domain.Sco)
	 */
	public void unsafeSaveSco(Sco sco) {
		try {
			PersistenceFacade.instance().unsafeSaveSco(sco);
		} catch (ScoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}
