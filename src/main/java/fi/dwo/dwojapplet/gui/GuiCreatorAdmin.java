/*
 * Created on Mar 30, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import fi.beans.numworxlf.JButton;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.dwojapplet.domain.Admin;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolPasswdMap;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.GuiCreatorTeacher.LazyAppletConfig;
import fi.dwo.dwojapplet.gui.action.CourseManagementAction;
import fi.dwo.dwojapplet.gui.action.ProfileManagementAction;
import fi.dwo.dwojapplet.gui.action.ScoManagementAction;
import fi.dwo.dwojapplet.gui.action.ScoParameterAction;
import fi.dwo.dwojapplet.gui.action.WrapSco;
import fi.dwo.dwojapplet.persistence.StoreCreator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.AbstractScoContextManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.ConfigManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminConfigManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminScoContextManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

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

    @Override
  public School editSchool(int schoolID, String schoolName, String schoolLogin,
      SchoolPasswdMap schoolPasswdMap, Date date, AboType aboType) throws SchoolException {
    // TODO Auto-generated method stub
    return dwo.editSchool(schoolID, schoolName, schoolLogin, schoolPasswdMap, date, aboType, schoolManager);
  }

    private static final Logger LOG = Logger.getLogger(GuiCreatorAdmin.class.getName());
    private final SecureDwoAdminScoContextManager scoManager;
    private final SecureDwoAdminCourseManager courseManager;
    private final SecureDwoAdminSchoolManager schoolManager;
    private SecureDwoAdminStudentModelManager studentModelManager;
    /**
     * @param dwo
     */
    public GuiCreatorAdmin(DWO dwo) {
        super(dwo);
        scoManager = new SecureDwoAdminScoContextManager(RestAuthenticator.getInstance().getContext());
        courseManager = new SecureDwoAdminCourseManager();
        schoolManager = new SecureDwoAdminSchoolManager();
    }

    @Override
    public SchoolManager getSchoolManager() {
      return schoolManager;
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
        csb = //new TeacherProfilePanel();
                new AccountPanel();
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
        try {
            return new UsersDwoAdminPanel();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(GuiCreatorAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns a panel for managing schoolclasses.
     *
     * @return A panel for managing schoolclasses.
     *
     */
    @Override
    public CenterSubPanel getSchoolPanel() {
        return new SchoolDwoAdminPanel();
    }

    /**
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    @Override
    public CenterSubPanel getCourseManagementPanel() {
        CourseMap[] editableCourses = ModuleTreePanel.STANDAARD_DWO_MAP.getChildren();
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
        return dwo.addCourse(name, description, parent, isMap, courseManager);
    }

    /**
     * @param course
     * @return boolean
     */
    @Override
    public boolean updateCourse(Course course) {
        return dwo.updateCourse(course, courseManager);
        // update ModuleTreePanel
    }

    @Override
    public void updateLogo(Course c) {
        if (dwo.updateLogo(c,courseManager))
			;
    }

    /**
     * @param course
     * @return boolean
     */
    @Override
    public boolean deleteCourse(Course course) {
        return dwo.deleteCourse(course, courseManager, false);
    }

    @Override
    public boolean trashCourse(Course course) {
      CourseMap c = course.getParentMap();
      boolean b = dwo.deleteCourse(course,courseManager, true);
      getMainPanel().getCenter().updateMap(c);
      return b;
    }

    @Override
    public boolean trashSco(Sco sco) {
      Course c = sco.getCourse();
      boolean b = dwo.deleteSco(sco,scoManager, true);
      getMainPanel().getCenter().updateCourse(c);
      return b;
    }

    /**
     * @param sco
     * @return boolean
     */
    @Override
    public boolean deleteSco(Sco sco) {
        return dwo.deleteSco(sco, scoManager, false);
    }

    /**
     * @param course
     * @return fi.dwo.client.gui.CenterSubPanel)
     */
    @Override
    public CenterSubPanel getScoManagementPanel(Course course) {
        return new ScoManagementPanel(course);
    }
 
    public CenterSubPanel getHTML5ScoPanel(Sco sco) {
      if (sco.hasFeature(Sco.JSON_OUT) && DwoHelper.hasProfileRight(DwoHelper.PREVIEW)) {
        dwo.setWait();
        try {
          final WrapSco wrap = new WrapSco(sco);
          CenterSubPanel csp = getScoPanel(wrap);
          dwo.setCurrentSco(wrap);
          return csp;
        } finally {
          dwo.setReady();
        }
      } else {
        return getScoPanel(sco);
      }
    }

    /**
     * @param sco
     * @return boolean
     */
    @Override
    public boolean updateSco(Sco sco) {
        return dwo.updateSco(sco,scoManager);
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

//    /**
//     * @return fi.dwo.client.domain.AppletConfig[]
//     */
//    @Override
//    public School[] getSchool() {
//        return dwo.getSchool();
//    }

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
     * @param course
     * @param name
     * @param description
     * @param showScore
     * @return fi.dwo.client.domain.Sco
     */
    @Override
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean showScore, byte[] imageData) {
        return dwo.addSco(course, appletConfig, name, description, showScore, imageData,scoManager);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreator#deleteSchool(fi.dwo.client.domain.School)
     */
    @Override
    public boolean deleteSchool(School sc) {
        return dwo.deleteSchool(sc, schoolManager);
    }
    @Override
    public School addSchool(int id, String schoolName, String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date, AboType aboType) throws SchoolException {
      return dwo.addSchool(id, schoolName, schoolLogin, schoolPasswdMap, date, aboType, schoolManager);
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
        return dwo.swapSco(sco1, sco2,scoManager);
    }

    @Override
    public JComponent getButtonBox(CourseChoicePanel courseChoisePanel) {
        Object userObject = courseChoisePanel.getUserObject();
        if (userObject == ModuleTreePanel.ALLE_MODULES) {
            return fx(null, new JButton(new ProfileManagementAction()));
        }
        return fx(null, new JButton(new CourseManagementAction(courseChoisePanel)));
    }

    @Override
    public JComponent fx(Object o, JComponent b) {
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
        return fx(null, new JButton(new ScoManagementAction(coursePanel)));
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
            return fx(null, box);
        }
        return fx(null, new JButton(new ScoParameterAction(scoPanel)));
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreator#unsafeSaveSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public void unsafeSaveSco(Sco sco) {
        if(!sco.isDataChanged())
          return;
        try {
 // PersistenceFacade.instance().unsafeSaveSco(sco);
          DomScoContextFull scoContext = new DomScoContextFull();
          DomScoData scoData = new DomScoData();
 // excerpts from DWO.changeSco
          scoContext.setId(PersistentScoContext.buildPersistenceId((long)sco.getScoID()));
          @SuppressWarnings("unchecked")
          Map<String, Object> m = sco.getLaunchdata();
          Object mode = m.get("mode");
          int value = mode == null ? 0 : Integer.parseInt(mode.toString());
          scoContext.setScoType(ScoType.values()[value]);
          scoData.setLaunchdata(sco.getLaunchdataString());
          dwo.extractStudentModel(scoContext, sco, m);
          if (sco.hasFeature(Sco.JSON_OUT))
              scoData.setLaunchdatabytes(sco.getLaunchdataBytes());
          StoreCreator.instance().uncache(sco, false);
          scoContext = scoManager.UNSAFEupdate(scoContext, scoData, DWO.getDwoProfile());
          sco.setDataChanged(false);
          
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public CenterSubPanel getCourseManagementPanel(CourseMap map) {
        CourseManagementPanel panel = new CourseManagementPanel(map.getChildren(), map);
        panel.setMap(map);
        panel.addTrash();
        return panel;
    }

	@Override
	public CenterSubPanel getDwoProfilePanel() {
		DwoProfilePanel panel = new DwoProfilePanel();
		return panel;
	}

	@Override
	public CenterSubPanel getAppletConfigPanel() {
		AppletConfigPanel panel = new AppletConfigPanel();
		return panel;
	}

        
    @Override
    public CenterSubPanel getUserManagementPanel() {
        UsersDwoAdminPanel panel;
        try {
            panel = new UsersDwoAdminPanel();
		return panel;
        } catch (Dwo2Exception ex) {
            Logger.getLogger(GuiCreatorAdmin.class.getName()).log(Level.SEVERE, null, ex);
            ShowErrorDialog(null,ex);
        }
        return null;
    }

    @Override
    public AbstractScoContextManager getScoContextManager() {
      return scoManager;
    }

    @Override
    public CourseManager getCourseManager() {
      return courseManager;
    }
    
    @Override
    public ConfigManager getConfigManager() {
      return new SecureDwoAdminConfigManager();
    }
    
    @Override
    public AppletConfig getAppletConfigFromSco(Sco sco) {
        LazyAppletConfig config;
        config = new LazyAppletConfig();
        String name = sco.getScoName();
        int aid = sco.getAppletID();
        int sid = sco.getScoID();
        config.setSco(sco);
        config.setAppletID(aid);
        config.setAppletConfigID(-sid); // HACK HACK negatief = scoid
        config.setName(name);
        config.setImageSource(PersistentScoContext.buildPersistenceId((long)sco.getID()));
        return config;
    }
    /**
     * check access rights of object
     * @param o
     * @return can write
     */
    public boolean readOnly(Object o) {
      return false;
    }

    
    @Override
    public CenterSubPanel getStudentModelPanel()  {
        try {
        	
            TeacherStudentModelPanelTableModel tmodel = new TeacherStudentModelPanelTableModel() {
            	   @Override
            	    public int getColumnCount() {
            	        return 2;
            	    }

            };
            return new TeacherStudentModelPanel(new TeacherStudentModelPanelProperties(new SecureDwoAdminStudentModelManager()), tmodel);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(GuiCreatorTeacher.class.getName()).log(Level.SEVERE, null, ex);
            this.ShowErrorDialog(welcomePanel, ex);
            return null;
        }
    }
    @Override
    public SecureStudentModelManager getStudentModelManager() {
      if (studentModelManager == null) studentModelManager = new SecureDwoAdminStudentModelManager();
      return studentModelManager;
    }

}
