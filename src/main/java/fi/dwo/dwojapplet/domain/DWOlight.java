package fi.dwo.dwojapplet.domain;

import fi.beans.appletutil.AppletUtil;
import fi.beans.jvmchecker.JVMChecker;
import fi.beans.mainframe.MainFrame;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.commons.exceptions.ClassException;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.ScoPanel;
import fi.dwo.dwojapplet.persistence.DbAccessCreator;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.applet.Applet;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

public class DWOlight extends Applet implements SCORM12APIInterface, DwoIF {

    private static final Logger log = Logger.getLogger(JApplet.class.getName());

    private Course currentCourse;
    private User currentUser;
    private DwoProfile dwoProfile;
    private int dwoProfileID;
    private String userName;
    private String passWord;
    private int courseViewNr;  // Deze funcionaliteit werkt niet! TODO? 
    private int scoViewNr = 1; // FIXME via DWOlight(args) invullen!
    private String languageOveride;

    /**
     * Creates a new DWO object.
     *
     */
    public DWOlight() {
        dwoProfileID = 1;
    }

    /**
     * Creates a new DWO object with an argument.
     * <pre>
     * -s SERVLET
     * profileID
     * username
     * password
     * </pre>
     *
     * @param args
     */
    public DWOlight(String[] args) {
        dwoProfileID = 1;
        int o = 0;
// allow update van SERVLET
        if (args != null && args.length > 1 && "-s".equals(args[0])) {
            DbAccessCreator.SERVLET = args[1];
            o = 2;
        }
// allow definitie van Locale.
        if (args != null && args.length > 1 + o && "-l".equals(args[o])) {
            languageOveride = args[o + 1];
            o += 2;
        }
        if (args != null && args.length > o && args[o] != null) {
            try {
                dwoProfileID = Integer.parseInt(args[o]);
            } catch (NumberFormatException e) {
            }
            if (args.length > 2 + o && args[1 + o] != null && args[2 + o] != null) {
                userName = args[1 + o];
                passWord = args[2 + o];
            }
        }

    }

    /**
     * The main method of the class.
     *
     * @param args
     */
    public static void main(String[] args) {
        int width = 789;
        int height = 492;
        MainFrame mf = new MainFrame(new DWOlight(args), width, height);
        mf.setTitle("DWOlight");
        mf.pack();
        mf.show();
        //mf.setSize(width + 10, height + 20);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addClass(java.lang.String)
     */
    @Override
    public boolean addClass(String className) throws ClassException {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addCourse(java.lang.String, java.lang.String)
     */
    @Override
    public Course addCourse(String name, String description, Course parent, boolean isMap) {
        return null;
    }


    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addSco(fi.dwo.client.domain.Course, fi.dwo.client.domain.AppletConfig, java.lang.String, java.lang.String)
     */
    @Override
    public Sco addSco(Course course, AppletConfig appletConfig, String name,
            String description, boolean showScore) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#changeAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, fi.dwo.client.domain.SchoolClass)
     */
    @Override
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email, SchoolClass c)
            throws RegisterException {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#changeAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, fi.dwo.client.domain.Group, java.lang.String)
     */
    @Override
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#changeAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email) throws RegisterException {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#clearCurrentUserData()
     */
    @Override
    public void clearCurrentUserData() {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteClass(fi.dwo.client.domain.SchoolClass)
     */
    @Override
    public boolean deleteClass(SchoolClass c) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteCourse(fi.dwo.client.domain.Course)
     */
    @Override
    public boolean deleteCourse(Course course) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteSchool(fi.dwo.client.domain.School)
     */
    @Override
    public boolean deleteSchool(School sc) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public boolean deleteSco(Sco sco) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteUser()
     */
    @Override
    public void deleteUser() {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#editSchool(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public School editSchool(int schoolID, String schoolName,
            String schoolLogin, String studentPassw, String teacherPassw)
            throws SchoolException {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getAppletConfig()
     */
    @Override
    public AppletConfig[] getAppletConfig() {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getCourses()
     */
    @Override
    public Course[] getCourses() {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getCourseViewNr()
     */
    @Override
    public int getCourseViewNr() {
        return courseViewNr;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getDwoProfile()
     */
    @Override
    public DwoProfile getDwoProfile() {
        return dwoProfile;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getEditableCourses()
     */
    @Override
    public Course[] getEditableCourses() {
        return null;
    }

    /**
     * Returns all the available groups.
     *
     * @return An array of all the available groups.
     *
     */
    @Override
    public Group[] getGroups() {
        try {
            return (Group[]) PersistenceFacade.instance().get(Group.class);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getResultsModule()
     */
    @Override
    public ResultsModuleIF getResultsModule() {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getResultsModule(fi.dwo.client.domain.Course[], boolean)
     */
    @Override
    public ResultsModuleIF getResultsModule(Course[] courses, boolean showSco) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getResultsModule(fi.dwo.client.domain.Course[])
     */
    @Override
    public ResultsModuleIF getResultsModule(Course[] courses) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getResultsModule(fi.dwo.client.domain.SchoolClass)
     */
    @Override
    public ResultsModuleIF getResultsModule(SchoolClass schoolClass) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getSchool()
     */
    @Override
    public School[] getSchool() {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getScoViewNr()
     */
    @Override
    public int getScoViewNr() {
        return scoViewNr;
    }

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, NULL is returned.
     *
     * @return the current user who is logged in. If the user is logged in as a
     * guest, NULL is returned.
     *
     */
    @Override
    public User getUser() {
        return currentUser;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getUserResultsModule(fi.dwo.client.domain.Course)
     */
    @Override
    public ResultsModuleIF getUserResultsModule(Course course) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#loadCourse(fi.dwo.client.domain.Course)
     */
    @Override
    public CenterSubPanel loadCourse(Course course) {
        currentCourse = course;
        return course.getCoursePanel();
    }
    private CenterSubPanel scoPanel;
    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#loadSco(fi.dwo.client.domain.Sco)
     */

    @Override
    public CenterSubPanel loadSco(Sco sco) {
        if (currentCourse == null) {
            currentCourse = sco.getCourse();
        }
        currentCourse.setCurrentSco(sco);
        return scoPanel = sco.getScoPanel(this, currentUser);
    }

    /**
     * Login as guest. CurrentUser becomes an instance of class Guest.
     *
     * @throws fi.dwo.commons.exceptions.LoginException
     * @see Guest
     *
     * @return If the guest was successfully logged in it returns true.
     * Otherwise it returns false.
     *
     */
    @Override
    public boolean login() throws LoginException {
        currentUser = Guest.instance();
        User.setCurrentUser(currentUser);
        return true;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#login(java.lang.String, java.lang.String)
     */
    @Override
    public boolean login(String username, String password)
            throws LoginException {
        currentUser = PersistenceFacade.instance().login(username, password);
        User.setCurrentUser(currentUser);
        return currentUser != null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#logoff()
     */
    @Override
    public void logoff() {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.AppletConfig)
     */
    @Override
    public ScoPanel previewSco(AppletConfig appletConfig) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public ScoPanel previewSco(Sco sco) {
        return null;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, fi.dwo.client.domain.Group, java.lang.String)
     */
    @Override
    public boolean register(String username, String password,
            String rePassword, String firstname, String middlename,
            String lastname, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean register(String username, String password,
            String rePassword, String firstname, String middlename,
            String lastname, String email) throws RegisterException {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#renameClass(fi.dwo.client.domain.SchoolClass, java.lang.String)
     */
    @Override
    public boolean renameClass(SchoolClass schoolClass, String newName, boolean iconizer) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#setPanel(java.awt.Panel)
     */
    @Override
    public void setPanel(Container p) {
        System.out.println(p);
        setLayout(new GridLayout(1, 1));
        if (scoPanel != null) {
            add(scoPanel.getComponent());
        } else if (currentCourse != null) {
            add(currentCourse.getCoursePanel().getComponent());
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#setReady()
     */
    @Override
    public void setReady() {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#setWait()
     */
    @Override
    public void setWait() {
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#setWait(java.lang.String)
     */
    @Override
    public void setWait(String waitText) {
        System.out.println(waitText);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#swapSco(fi.dwo.client.domain.Sco, fi.dwo.client.domain.Sco)
     */
    @Override
    public boolean swapSco(Sco sco1, Sco sco2) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#updateCourse(fi.dwo.client.domain.Course)
     */
    @Override
    public boolean updateCourse(Course course) {
        return false;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#updateSco(fi.dwo.client.domain.Sco)
     */
    @Override
    public boolean updateSco(Sco sco) {
        return false;
    }

    @Override
    public void destroy() {
        DwoHelper.clrApplet(this);
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#init()
     */
    @Override
    public void init() {
        if (!DwoHelper.setApplet(this)) {
            return;
        }
        setSize(789, 492);
        String lang = getParameter("language");
        if ((lang != null) && (!lang.equals(""))) {
            TextMapper.setLanguage(lang);
            fi.dwo.dwojapplet.parameters.system.TextMapper.setLanguage(lang);
        }
        boolean cookies = false;
        String cookiesString = getParameter("cookies");
        if (cookiesString != null && cookiesString.equals("true")) {
            cookies = true;
        }

        boolean guestUser = false; // Wim: teruggezet
        String guestUserString = getParameter("guestUser");
        if (guestUserString != null && guestUserString.equals("true")) {
            guestUser = true;
        }

        String scoViewNrString = getParameter("scoViewNr");
        if (scoViewNrString != null && (!scoViewNrString.equals(""))) {
            try {
                scoViewNr = Integer.parseInt(scoViewNrString);
            } catch (Exception e) {
            }
        }

        String courseViewNrString = getParameter("courseViewNr");
        if (courseViewNrString != null && (!courseViewNrString.equals(""))) {
            try {
                courseViewNr = Integer.parseInt(courseViewNrString);
            } catch (Exception e) {
            }
        }

        boolean umpc = false;
        String umpcString = getParameter("umpc");
        if (umpcString != null && umpcString.equals("true")) {
            umpc = true;
        }

        DwoHelper.setAu(new AppletUtil(this));
        DwoHelper.setUmpc(umpc);

        if (!DwoHelper.isApplication()) {
            dwoProfileID = 1;
            String dwoProfileString = getParameter("profile");
            if ((dwoProfileString != null) && (!dwoProfileString.equals(""))) {
                try {
                    dwoProfileID = Integer.parseInt(dwoProfileString);
                } catch (Exception e) {
                }
            }
            JVMChecker jvmChecker = new JVMChecker(this);
            jvmChecker.check();
        }

        try {
            dwoProfile = (DwoProfile) PersistenceFacade.instance().get(dwoProfileID, DwoProfile.class);
        } catch (PersistenceException e) {
        }
        GuiConstants.setDwoProfile(dwoProfileID, null);
        GuiConstants.GUI_ICONIZED = false;
        User.DEFAULT_ICONIZER = false;
        GuiCreator gc = new GuiCreator(this);
        try {
            PersistenceFacade.instance().reConnect();
        } catch (PersistenceException e) {
        }

        if (userName != null && passWord != null) {
            try {
                GuiCreator.instance().login(userName, passWord);
                return;
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }
        } else if (guestUser) {
            try {
                GuiCreator.instance().login();
                return;
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }
        } else if (cookies) {
            userName = DwoHelper.getCookie("dwoUserName");
            passWord = DwoHelper.getCookie("dwoPassWord");
            if (userName != null && passWord != null) {
                try {
                    GuiCreator.instance().login(userName, passWord);
                    return;
                } catch (Exception ex) {
                }
            }
        }

// Hier wordt A-Select in DWO actief
        currentUser = getInitialUser();
        if (currentUser != null) // Dit is de enige plaats waar op null
        // getest mag worden!
        {
            gc.login(currentUser);
            return;
        }
        try {
            gc.login();
        } catch (LoginException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Stops the current applet. Indicates at the current course that the applet
     * will be stopped.
     */
    @Override
    public void stop() {
        if (this != DwoHelper.getApplet()) {
            return;
        }
        this.setWait();
        super.stop();
        if (currentCourse != null) {
            currentCourse.end();
        }
        logoff();
        this.setReady();
    }

    @Override
    public String LMSInitialize(String iParam) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSInitialize(iParam);
        } else {
            return null;
        }
    }

    @Override
    public String LMSFinish(String iParam) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSFinish(iParam);
        } else {
            return null;
        }
    }

    @Override
    public String LMSGetValue(String iDataModelElement) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetValue(iDataModelElement);
        } else {
            return null;
        }
    }

    @Override
    public String LMSSetValue(String iDataModelElement, String iValue) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSSetValue(iDataModelElement, iValue);
        } else {
            return null;
        }
    }

    @Override
    public String LMSCommit(String iParam) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSCommit(iParam);
        } else {
            return null;
        }
    }

    @Override
    public String LMSGetLastError() {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetLastError();
        } else {
            return null;
        }
    }

    @Override
    public String LMSGetErrorString(String iErrorCode) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetErrorString(iErrorCode);
        } else {
            return null;
        }
    }

    @Override
    public String LMSGetDiagnostic(String iErrorCode) {
        if (currentCourse != null && currentCourse.getCurrentSco() != null) {
            return currentCourse.getCurrentSco().LMSGetDiagnostic(iErrorCode);
        } else {
            return null;
        }
    }

    /**
     * Geef mij een gebruiker buitenom.
     *
     * @return a user.
     */
    private User getInitialUser() {
        return null;
    }

    /**
     * Converteer een role naar een Group. De namen van de group zijn niet
     * gelijk aan die van de 'role' (entree.kennisnet.nl). Er moet daarom
     * gemapped worden.
     *
     * @param role
     * @return de Group die role representeerd.
     */
    private Group findGroup(String role) {
        if (role == null) {
            return null;
        }
        // TODO is deze mapping compleet?
        if ("docent".equals(role)) {
            role = "TEACHER";
        }
        if ("leerling".equals(role)) {
            role = "STUDENT";
        }
        //
        Group[] groups = getGroups();
        for (Group group : groups) {
            if (role.equalsIgnoreCase(group.getName())) {
                return group;
            }
        }
        return null;
    }

    /**
     * Zet een gebruiker in een klas.
     *
     * @param className naam van klas
     * @param u de gebruiker
     * @param school die van u
     */
    private void setInitialUserInClass(String className, User u, School school) {
        SchoolClass schoolClass = u.getInClass();
        if (className != null
                && (schoolClass == null || !schoolClass.getName().equals(className))) {
            SchoolClass[] classes = school.getClassList();
            for (SchoolClass classe : classes) {
                if (className.equals(classe.getName())) {
                    u.setInClass(classe);
                    try {
                        PersistenceFacade.instance().changeAccount(u, null, null, u.getFirstname(), u.getMiddleName(), u.getLastName(), u.getEmail(), u.getInClass());
                    } catch (RegisterException e) {
        
                        log.log(Level.SEVERE, null, e);
                    }
                }
            }
        }
    }

    /**
     * Returns the LMS value for the specified sco and the specified user.
     *
     * @param sco The sco wherefrom the LMS value is asked.
     * @param user The usere wherefrom the LMS value is asked.
     * @param iDataModelElement The parameter to ask for.
     * @return The value representing for the specified sco, user and parameter.
     */
    @Override
    public String LMSGetValue(ScoBase sco, User user, String iDataModelElement) {
        if (iDataModelElement.equals(SCORM12APIInterface.USER_GROUP)) {
            if (currentUser == null || currentUser instanceof Guest) {
                return SCORM12APIInterface.UG_GUEST;
            } else if (currentUser instanceof Teacher) {
                return SCORM12APIInterface.UG_TEACHER;
            } else {
                return SCORM12APIInterface.UG_STUDENT;
            }

        } else {
            try {
                return PersistenceFacade.instance().LMSGetValue(sco, user, iDataModelElement);
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return "";
            }
        }

    }

    /**
     * Sets the LMS value for the specified sco for the current user.
     *
     * @param sco The sco wherefrom the LMS value is set.
     * @param user
     * @param iDataModelElement The dataModeElement to set.
     * @param iValue The new value for the dataModeElement.
     * @return String representing a boolean
     * <ul>
     * <li><code>true</code> result indicates that the LMSSetValue() was
     * successful</li>
     * <li><code>false</code> result indicates that the LMSSetValue() was
     * unsuccessful</li>
     * </ul>
     */
    @Override
    public String LMSSetValue(ScoBase sco, User user, String iDataModelElement, String iValue) {
        try {
            return PersistenceFacade.instance().LMSSetValue(sco, user, iDataModelElement, iValue);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return Boolean.FALSE.toString();
        }

    }
    /* (non-Javadoc)
     * @see java.applet.Applet#getParameter(java.lang.String)
     */

    @Override
    public String getParameter(String name) {
        if ("language".equals(name) && languageOveride != null) {
            return languageOveride;
        }
        return super.getParameter(name);
    }

    @Override
    public void setCurrentSco(Sco sco) {
    }

    @Override
    public School addSchool(int id, String schoolName, String schoolLogin,
            SchoolPasswdMap schoolPasswdMap, Date date) {
        return null;
    }

    @Override
    public School editSchool(int schoolID, String schoolName,
            String schoolLogin, SchoolPasswdMap schoolPasswdMap, Date date) {
        return null;
    }

    /**
     * Dummy. DWOLight heeft geen Welcome panel
     */
    @Override
    public void setWelcomePanel() {
    }

    @Override
    public String LMSCommit(ScoBase sco, String param) {
        try {
            return PersistenceFacade.instance().LMSCommit(sco, getUser(), param);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            log.log(Level.SEVERE, null, e);
            return "false";
        }
    }

    @Override
    public Sco[] getEditableScos() {
        return null;
    }

    @Override
    public Course[] sequence(Course[] allCourses, SchoolClass sc) {
        return allCourses;
    }

    public Course[] sequence(Course[] c) {
        return c;
    }

    @Override
    public boolean updateLogo(Course c) {
        return false;
    }

    @Override
    public void linkViaSAML() {
    }
}
