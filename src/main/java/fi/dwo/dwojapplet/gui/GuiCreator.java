// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\GuiCreator.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.ClassException;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Admin;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.ContactDocent;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.Group;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolPasswdMap;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.fullscreen.FramedScoPanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.Container;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * This Class is responsible for creating some GUI elements and to communicate
 * from the GUI to the domain (the DwoIF).
 *
 * @author M.J.B. Kupers
 *
 */
public class GuiCreator {

    private static final Logger log = Logger.getLogger(GuiCreator.class.getName());

    protected DwoIF dwo;

    private static GuiCreator _instance;

    protected MainPanel mainPanel;

    protected WelcomePanel welcomePanel;

    /**
     * Creates a new instance of a GuiCreator. The GuiCreator can be reached by
     * the static instance() method.
     *
     * @param dwo The dwo to communicate with.
     */
    //TODO fix memory leak, instantiation should occur different.
    public GuiCreator(DwoIF dwo) {
        this.dwo = dwo;
        GuiCreator._instance = this;

    }

    /**
     * Returns the main panel
     *
     * @return The main panel
     */
    public MainPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Logs a user in into the system. The user will be remembered while the
     * user is logged in. Then it shows the MainPanel.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @throws fi.dwo.commons.exceptions.LoginException
     *
     */
    public void login(String username, String password) throws LoginException {
        dwo.setWait();
        try {
            DwoHelper.setContact(false);
            if (dwo.login(username, password)) {
// HOOK: check if username is valide volgens de nieuwe regels.
                //validUsernameCheck(username);
                validLicenceCheck(dwo.getUser());

                login(dwo.getUser());

                //ModuleTreePanel.create(dwo );  
            }
        } catch (LoginException e) {
            throw e;
        } finally {
            dwo.setReady();
        }
    }

    /**
     * Toon <b>G</b>een waarschuwing. Fix voor gebruikersnamen die met het
     * fidentity systeem ongeldig worden.
     *
     * @param username gebruikersnaam
     * @deprecated wordt niet meer gebruikt.
     */
    private void validUsernameCheck(String username) {
//		if(!DWO.isValid(username))
//		{
//			JOptionPane.showMessageDialog(null,'\'' + username + "' is vanaf november 2007 ongeldig!\nRegistreer een nieuwe gebruikersnaam");	
//		}
    }

    private void validLicenceCheck(User u) {
        School s = u.getSchool();
        if (s != null) {
            Date expire = s.getExpire();
            if (expire != null && expire.getTime() < System.currentTimeMillis()) {
                String message = TextMapper.GUICDLG_LICENCE;
                message = TextMapper.format(message, new Object[]{s.getName()});
                JOptionPane.showMessageDialog(DwoHelper.getFrameForComponent(null), message);
            }
        }
    }

    /**
     * @param u
     */
    public void login(User u) {
        if (u instanceof Teacher) {
            GuiCreator gc;
            if (u instanceof ContactDocent) {
                gc = new GuiCreatorContactDocent(dwo);
            } else {
                gc = new GuiCreatorTeacher(dwo);
            }
            gc.mainPanel = mainPanel;
            gc.welcomePanel = welcomePanel;
            gc.mainPanel = new MainPanel(dwo.getDwoProfile());
            dwo.setPanel(gc.mainPanel);
        } else if (u instanceof Admin) {
            GuiCreator gc = new GuiCreatorAdmin(dwo);
            gc.mainPanel = mainPanel;
            gc.welcomePanel = welcomePanel;
            gc.mainPanel = new MainPanel(dwo.getDwoProfile());
            dwo.setPanel(gc.mainPanel);
        } else {
            if (this instanceof GuiCreatorTeacher || this instanceof GuiCreatorAdmin) {
                GuiCreator gc = new GuiCreator(dwo);
                gc.mainPanel = mainPanel;
                gc.welcomePanel = welcomePanel;
                gc.mainPanel = new MainPanel(dwo.getDwoProfile());
                dwo.setPanel(gc.mainPanel);
            } else {
                mainPanel = new MainPanel(dwo.getDwoProfile());
                dwo.setPanel(mainPanel);
            }
        }
    }

    /**
     * Login as guest. Then it shows the MainPanel.
     *
     * @throws fi.dwo.commons.exceptions.LoginException
     *
     */
    public void login() throws LoginException {
        dwo.setWait();
        try {
            if (dwo.login()) {
                mainPanel = new MainPanel(dwo.getDwoProfile());
                dwo.setPanel(mainPanel);
            }
        } catch (LoginException e) {
            throw e;
        } finally {
            dwo.setReady();
        }
    }

    /**
     * Register a user in the system, and shows the WelcomeScreen to the user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param rePassword The re-password for the user. It is used to check for a
     * typing error.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    public void register(String username, String password, String rePassword,
            String firstname, String middlename, String lastname, String email)
            throws RegisterException {
        dwo.register(username, password, rePassword, firstname, middlename,
                lastname, email);
        JOptionPane.showMessageDialog(DwoHelper.getApplet(), TextMapper
                .getText(TextMapper.GUIR_MSG_REGISTERED));
        WelcomePanel wcp = getWelcomePanel();
        wcp.setUsername(username);
        wcp.setPassword(password);
        dwo.setPanel(wcp);

    }

    /**
     * Register a user in the system. Als links a user to a school. Then it
     * shows the WelcomeScreen to the user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param rePassword The re-password for the user. It is used to check for a
     * typing error.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     * and the school.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    public void register(String username, String password, String rePassword,
            String firstname, String middlename, String lastname, String email,
            String schoolLogin, Group group, String groupPassword)
            throws RegisterException {
        dwo.register(username, password, rePassword, firstname, middlename,
                lastname, email, schoolLogin, group, groupPassword);
        JOptionPane.showMessageDialog(DwoHelper.getApplet(), TextMapper
                .getText(TextMapper.GUIR_MSG_REGISTERED));
        WelcomePanel wcp = getWelcomePanel();
        wcp.setUsername(username);
        wcp.setPassword(password);
        dwo.setPanel(wcp);

    }

    /**
     * Shows the register panel to the user.
     */
    public void toRegister() {
        dwo.setWait();
        RegisterPanel rp = new RegisterPanel(dwo.getGroups());
        dwo.setPanel(rp);
        dwo.setReady();
    }

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, an instance of Guest is returned.
     *
     * @return the current user who is logged in. If the user is logged in as a
     * guest, Guest.instance().
     *
     */
    public User getUser() {
        User u = dwo.getUser();
        if (u == null) {
            u = Guest.instance();
        }
        return u;
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

        if (u == null || u instanceof Guest) {
            return new GuestMenuPanel();
        } else {
            return new MenuPanel();
        }
    }

    public WelcomePanel getWelcomePanel() {
        return new WelcomePanel();

    }

    public WelcomePanel getWelcomePanel(boolean loginOnly) {
        return new WelcomePanel(loginOnly);

    }

    public WelcomePanel getWelcomePanel(boolean loginOnly, Map samlData) {
        return new WelcomePanel(loginOnly, samlData);

    }

    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     *
     * @return An array of all the courses for the current user.
     *
     */
    public Course[] getCourseList() {
        return dwo.getCourses();
    }

    /**
     * Clears all the information of the current user out of the memory, so no
     * cashing problems can appear.
     *
     */
    public void clearCurrentUserData() {
        dwo.clearCurrentUserData();
    }

    /**
     * Log the user off of the system. Shows the Welcome screen.
     *
     */
    public void logoff() {
        mainPanel.end();
        mainPanel = null;
        dwo.logoff();
        dwo.setWelcomePanel();

    }

    public void setWait() {
        dwo.setWait();
    }

    public void setReady() {
        dwo.setReady();
    }

    /**
     * Sets a course to the current course. Loads the course and returns a panel
     * representing the course.
     *
     * @param course The course to select.
     * @return A panel representing the course.
     *
     */
    public CenterSubPanel getCoursePanel(Course course) {
        dwo.setWait();
        CoursePanel csp = (CoursePanel) dwo.loadCourse(course);
        csp.setLessonMode(Sco.NORMAL);
        dwo.setReady();
        csp.getComponent().requestFocus();
        return csp;
    }

    public CenterSubPanel getCourseChoisePanel() {
        dwo.setWait();
        CenterSubPanel csp = null;
        if (dwo.getScoViewNr() > 0) {
            Sco viewSco = null;
            try {
                viewSco = (Sco) PersistenceFacade.instance().get(dwo.getScoViewNr(), Sco.class);
            } catch (Exception e) {
                log.log(Level.SEVERE, null, e);
            }
            if (viewSco != null) {
                dwo.setCurrentSco(viewSco);
                viewSco.setLessonMode(Sco.NORMAL);
                csp = getScoPanel(viewSco);
                ((ScoPanel) csp).setScoView(true);
            } else {
                System.out.println("ScoNr " + dwo.getScoViewNr() + " niet gevonden");
            }
        } else if (dwo.getCourseViewNr() > 0) {
            Course viewCourse = null;
            try {
                viewCourse = (Course) PersistenceFacade.instance().get(dwo.getCourseViewNr(), Course.class);
            } catch (Exception exc) {
            }

            csp = getCoursePanel(viewCourse);
            ((CoursePanel) csp).setCourseView(true);
        } else {
            csp = CourseChoicePanel.newInstance();
        }
        dwo.setReady();
        return csp;
    }

    /**
     * Sets a sco to the course current sco. Returns a panel with the
     * sco-applet.
     *
     * @param sco The sco to select.
     * @return A panel with the sco-applet.
     *
     */
    public CenterSubPanel getScoPanel(Sco sco) {
        dwo.setWait();
        try {
            CenterSubPanel csp = dwo.loadSco(sco);
            if (sco.getCreditStatus() == Sco.CREDIT) {
                return new FramedScoPanel(csp, sco);
            }
            return csp;
        } finally {
            dwo.setReady();
        }
    }

    /**
     * Returns an instance of a GuiCreator Object. This object must be created
     * once, with the constructor, so he knows the DWO to communicate with.
     *
     * @return An instance of the GuiCreator to communicate with.
     *
     */
    public static GuiCreator instance() {
        return _instance;
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
//        csb = new ProfilePanel(dwo.getGroups());
        csb = new TabbedProfilePanel(dwo.getGroups());
        dwo.setReady();
        return csb;
    }
//TODO MANY TO MANY DONE: obsolete
//    /**
//     * Change the current user his account.
//     *
//     * @param password The current password of the user. It will be used to
//     * validate the current user.
//     * @param newPassword The new password of the user.
//     * @param reNewPassword The re-password for the user. It is used to check
//     * for a typing error.
//     * @param firstName The firstname of the user.
//     * @param middleName The middlename of the user. <br>
//     * e.g: <code>Van</code>
//     * @param lastName The lastname (familyname) of the user.
//     * @param email The e-mail address of the user.
//     * @param c The new SchoolClass of the user.
//     * @throws fi.dwo.commons.exceptions.RegisterException
//     *
//     */
//    public void changeAccount(String password, String newPassword,
//            String reNewPassword, String firstName, String middleName,
//            String lastName, String email, SchoolClass c)
//            throws RegisterException {
//        dwo.changeAccount(password, newPassword, reNewPassword, firstName,
//                middleName, lastName, email);
//    }
    
    /**
     * Change the current user his account who not is linked to a school.
     *
     * @param password The current password of the user. It will be used to
     * validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     * for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     * and the school.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException {
        dwo.changeAccount(password, newPassword, reNewPassword, firstName,
                middleName, lastName, email, schoolLogin, group, groupPassword);

    }

    /**
     * Change the current user his account.
     *
     * @param password The current password of the user. It will be used to
     * validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     * for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     * e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @throws fi.dwo.commons.exceptions.RegisterException
     *
     */
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email) throws RegisterException {
        dwo.changeAccount(password, newPassword, reNewPassword, firstName,
                middleName, lastName, email);

    }

    /**
     * Shows a dialog to add a class and adds the class.
     *
     * @return boolean Indicates if the class is added, or the operation is
     * canceled.
     * @throws fi.dwo.commons.exceptions.ClassException
     *
     */
    public boolean addClass() throws ClassException {
        String newClass = JOptionPane.showInputDialog(mainPanel,
                TextMapper.getText(TextMapper.GUIMNU_MSG_ADD_CLASS) + ":",
                TextMapper.getText(TextMapper.GUIMNU_MSG_ADD_CLASS_TITLE),
                JOptionPane.OK_CANCEL_OPTION);
        if ((newClass != null) && (!newClass.equals(""))) {
            return dwo.addClass(newClass);
        }
        return false;
    }

    /**
     * Shows a dialog to edit a school.
     *
     * @param schoolID
     * @param schoolName
     * @param schoolLogin
     * @param date
     * @param schoolPasswdMap
     *
     * @return edited School
     * @throws fi.dwo.commons.exceptions.SchoolException
     *
     */
    public School editSchool(int schoolID, String schoolName, String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date) throws SchoolException {
        return dwo.editSchool(schoolID, schoolName, schoolLogin, schoolPasswdMap, date);
    }

    /**
     * Shows a dialog to add a school and adds the school.
     *
     * @param id
     * @param schoolName
     * @param schoolLogin
     * @param date
     * @param schoolPasswdMap
     *
     * @return boolean Indicates if the school is added, or the operation is
     * canceled.
     * @throws fi.dwo.commons.exceptions.SchoolException
     *
     */
    public School addSchool(int id, String schoolName, String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date) throws SchoolException {
        return dwo.addSchool(id, schoolName, schoolLogin, schoolPasswdMap, date);
    }

    /**
     * Deletes the account of the current user and loggs of.
     *
     */
    public void deleteUser() {
        dwo.deleteUser();
        logoff();
    }

    /**
     * Deletes the specified class from the system.
     *
     * @param c The class to delete.
     * @return boolean If the class was successfully deleted it returns true.
     * Otherwise it returns false.
     *
     */
    public boolean deleteClass(SchoolClass c) {
        return dwo.deleteClass(c);
    }

    /**
     * Returns a panel with results for users of the classes of the teacher. The
     * teacher can analyse these results.
     *
     * @return A panel with results for users of the classes of the teacher.
     */
    public CenterSubPanel getResultPanel() {
        return null;
    }

    /**
     * Returns a panel with results for users of the classes of the teacher. The
     * teacher can analyse these results.
     *
     * @param c
     * @return A panel with results for users of the classes of the teacher.
     */
    public CenterSubPanel getResultPanel(Course c) {
        return null;
    }

    /**
     * Returns a panel with results for users of the specified class. The
     * teacher can analyse these results.
     *
     * @param schoolClass The SchoolClass to show the results from.
     * @return A panel with results for users of the specified class.
     */
    public CenterSubPanel getResultPanel(SchoolClass schoolClass) {
        return null;
    }

    /**
     * Returns a panel representing the specified SchoolClass.
     *
     * @param c The SchoolClass of the panel to return.
     * @return A panel representing the specified SchoolClass.
     *
     */
    public CenterSubPanel getClassUsersPanel(SchoolClass c) {
        return null;
    }

    /**
     * Returns a panel for managing schools.
     *
     * @return A panel for managing schools.
     *
     */
    public CenterSubPanel getSchoolPanel() {
        return null;
    }

    /**
     * Returns a panel for managing schoolclasses.
     *
     * @return A panel for managing schoolclasses.
     *
     */
    public CenterSubPanel getClassPanel() {
        return null;
    }

    /**
     * Loads a main panel on the DWO.
     *
     * @param p The panel to load.
     */
    public void loadPanel(Container p) {
        dwo.setPanel(p);
    }

    /**
     * Renames the specified class.
     *
     * @param schoolClass The class to rename.
     * @param newName The new name for the class.
     * @param iconizer
     * @return If the class is successfully renamed it returns true. Otherwise
     * it returns false.
     */
    public boolean renameClass(SchoolClass schoolClass, String newName, String newRegistrationKey, boolean iconizer) {
        return dwo.renameClass(schoolClass, newName, newRegistrationKey, iconizer);
    }

    /**
     * @return fi.dwo.client.gui.CenterSubPanel
     */
    public CenterSubPanel getCourseManagementPanel() {
        return null;
    }

    /**
     * @param name
     * @param description
     * @param isMap
     * @param parent
     * @return fi.dwo.client.domain.Course
     */
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        return null;
    }

    /**
     * @param course
     * @return boolean
     */
    public boolean updateCourse(Course course) {
        return true;
    }

    /**
     * @param course
     * @return boolean
     */
    public boolean deleteCourse(Course course) {
        return true;
    }

    /**
     * @param sco
     * @return boolean
     */
    public boolean deleteSco(Sco sco) {
        return true;
    }

    /**
     * @param course
     * @return fi.dwo.client.gui.CenterSubPanel)
     */
    public CenterSubPanel getScoManagementPanel(Course course) {
        return null;
    }

    /**
     * @param sco
     * @return boolean
     */
    public boolean updateSco(Sco sco) {
        return true;
    }

    /**
     * @param sco
     */
    public void loadParameterManagementPanel(Sco sco) {
    }

    /**
     * @return fi.dwo.client.domain.DWO
     */
    public DwoIF getDWO() {
        if (dwo instanceof DWO) {
            return (DWO) dwo;
        }
        return null;
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    public AppletConfig[] getAppletConfig() {
        return null;
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    public AppletConfig[] getAppletConfigFromTeacher() {
        return null;
    }

    /**
     * @return fi.dwo.client.domain.AppletConfig[]
     */
    public School[] getSchool() {
        return null;
    }

    /**
     * @param appletConfig
     * @return fi.dwo.client.gui.ScoPanel
     */
    public ScoPanel previewSco(AppletConfig appletConfig) {
        return null;
    }

    /**
     * @param sco
     * @return fi.dwo.client.gui.ScoPanel
     */
    public ScoPanel previewSco(Sco sco) {
        return null;
    }

    /**
     * @param course
     * @param appletConfig
     * @param name
     * @param description
     * @param b
     * @return fi.dwo.client.domain.Sco
     */
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description, boolean b) {
        return null;
    }

    /**
     * Verwijder een school.
     *
     * @param sc School
     * @return true if success
     */
    public boolean deleteSchool(School sc) {
        return false;
    }

    /**
     * Verwissel de sequencenrs van twee Sco's. De Sco's moeten tot dezelfe
     * Course behoren.
     *
     * @param sco1 Sco
     * @param sco2 Sco
     * @return boolean: succes of gefaald
     */
    public boolean swapSco(Sco sco1, Sco sco2) {
        return false;
    }

    public CenterSubPanel getUserManagementPanel() {

        return null;
    }

    public CenterSubPanel getClassAdminPanel() {

        return null;
    }

    public CenterSubPanel getCourseManagementPanel(CourseMap c) {
        return getCourseManagementPanel();
    }

    public CenterSubPanel getCourseChoisePanel(Course c) {
        return new CourseChoicePanel(c, c.getChildren(), c);
    }

    public AppletConfig getAppletConfigFromSco(Sco sco) {
        return null;
    }

    public JComponent getButtonBox(CourseChoicePanel courseChoisePanel) {
        return null;
    }

    public JComponent getButtonBox(CoursePanel coursePanel) {
        return null;
    }

    public JComponent getButtonBox(ScoPanel scoPanel) {
        return null;
    }

    public JComponent fx(JComponent stopBtn) {
        return null;
    }

    public void unsafeSaveSco(Sco sco) {


    }

    public void updateLogo(Course c) {


    }

    public void linkViaSAML() {
        dwo.linkViaSAML();

    }

}
