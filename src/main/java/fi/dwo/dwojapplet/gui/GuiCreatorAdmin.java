/*
 * Created on Mar 30, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import fi.dwo.commons.exceptions.ScoException;
import fi.dwo.dwojapplet.domain.Admin;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.CourseManagementAction;
import fi.dwo.dwojapplet.gui.action.ScoManagementAction;
import fi.dwo.dwojapplet.gui.action.ScoParameterAction;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

/**
 * This class implements Admin-specific methods of the GuiCreator. These methods
 * are implemented in this specific subclass because of imports.<br>
 * Now, a normal user must not import the admin-specific classes and must not
 * load them.
 *
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
    @Override
    public GuestMenuPanel getMenuPanel() {
        User u = dwo.getUser();

        if (u instanceof Admin) {
            return new AdminMenuPanel();
        } else {
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
    @Override
    public CenterSubPanel getSchoolPanel() {
        return new SchoolPanel();
    }

    /**
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    @Override
    public CenterSubPanel getCourseManagementPanel() {
        Course[] editableCourses = dwo.getEditableCourses();
        if (editableCourses != null) {
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
    @Override
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        return dwo.addCourse(name, description, parent, isMap);
    }

    /**
     * @param course
     * @return boolean
     */
    @Override
    public boolean updateCourse(Course course) {
        return dwo.updateCourse(course);
        // update ModuleTreePanel
    }

    @Override
    public void updateLogo(Course c) {
        if (dwo.updateLogo(c))
			;
    }

    /**
     * @param course
     * @return boolean
     */
    @Override
    public boolean deleteCourse(Course course) {
        return dwo.deleteCourse(course);
    }

    /**
     * @param sco
     * @return boolean
     */
    @Override
    public boolean deleteSco(Sco sco) {
        return dwo.deleteSco(sco);
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
     */
    @Override
    public void loadParameterManagementPanel(Sco sco) {
        new ParameterManagementPanel(sco);
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    @Override
    public AppletConfig[] getAppletConfig() {
        return dwo.getAppletConfig();
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    @Override
    public School[] getSchool() {
        return dwo.getSchool();
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
     * @param showScore
     * @return fi.dwo.client.domain.Sco
     */
    @Override
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean showScore) {
        return dwo.addSco(course, appletConfig, name, description, showScore);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreator#deleteSchool(fi.dwo.client.domain.School)
     */
    @Override
    public boolean deleteSchool(School sc) {
        return dwo.deleteSchool(sc);
    }

    /**
     * Verwissel de sequencenrs van twee Sco's. De Sco's moeten tot dezelfe
     * Course behoren.
     *
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
        if (userObject == ModuleTreePanel.ALLE_MODULES) {
            return null;
        }
        return fx(new JButton(new CourseManagementAction(courseChoisePanel)));
    }

    @Override
    public JComponent fx(JComponent b) {
        if (!CenterPanel.isIconizer()) {
            return null;
        }
        Box box = Box.createVerticalBox();
        box.add(Box.createGlue());
        box.add(b);
        box.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0)); // Meten!
        return box;
    }

    @Override
    public JComponent getButtonBox(CoursePanel coursePanel) {
        return fx(new JButton(new ScoManagementAction(coursePanel)));
    }

    @Override
    public JComponent getButtonBox(ScoPanel scoPanel) {
        if (scoPanel.getSco().getLessonMode() == Sco.BROWSE) {
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
    @Override
    public void unsafeSaveSco(Sco sco) {
        try {
            PersistenceFacade.instance().unsafeSaveSco(sco);
        } catch (ScoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public CenterSubPanel getCourseManagementPanel(CourseMap map) {
        CourseManagementPanel panel = new CourseManagementPanel(map.getChildren(), map);
        panel.setMap(map);
        return panel;
    }

}
