// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\DwoIF.java

package fi.dwo.client.domain;

import java.awt.Container;
import java.awt.Panel;

import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.ScoPanel;
import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.CourseException;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.RegisterException;

/**
 * The interface for the Gui to communicate with the DWO.<br>
 * The most requests from the GUI are send to the GuiCreator, 
 * who communicates with the DwoIF.
 * @author M.J.B. Kupers
 *  
 */
public interface DwoIF {

    /**
     * Sets the current panel of the applet.
     * 
     * @param p The panel to set.
     * @see fi.dwo.client.domain.DwoIF#setPanel(java.awt.Panel)
     */
    public void setPanel(Container p);
    
    public void setWelcomePanel();

    /**
     * Logs a user in into the system. The user will be remembered while the
     * user is logged in.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @return If the user was successfully logged in it returns true. Otherwise
     *         it returns false.
     * @throws fi.dwo.client.system.LoginException If some login-information is
     *             incorrect.
     *  
     */
    public boolean login(String username, String password)
            throws LoginException;

    /**
     * Login as guest
     * 
     * @return If the guest was successfully logged in it returns true.
     *         Otherwise it returns false.
     * @throws fi.dwo.client.system.LoginException If some login-information is
     *             incorrect.
     *  
     */
    public boolean login() throws LoginException;

    /**
     * Register a user in the system.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @param rePassword The re-password for the user. It is used to check for a
     *            typing error.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @return If the user was successfully registered true is returned.
     *         Otherwise false is returned.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public boolean register(String username, String password,
            String rePassword, String firstname, String middlename,
            String lastname, String email) throws RegisterException;

    /**
     * Register a user in the system. Als links a user to a school.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @param rePassword The re-password for the user. It is used to check for a
     *            typing error.
     * @param firstname The firstname of the user.
     * @param middlename The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastname The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     *            and the school.
     * @return If the user was successfully registered true is returned.
     *         Otherwise false is returned.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public boolean register(String username, String password,
            String rePassword, String firstname, String middlename,
            String lastname, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException;

    /**
     * Returns all the available groups.
     * 
     * @return An array of all the available groups.
     *  
     */
    public Group[] getGroups();

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, NULL is returned.
     * 
     * @return the current user who is logged in. If the user is logged in as a
     *         guest, NULL is returned.
     *  
     */
    public User getUser();

    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     * 
     * @return An array of all the courses for the current user.
     *  
     */
    public Course[] getCourses();

	/**
     * Returns  the dwoProfile. 
     *  
     */
	public DwoProfile getDwoProfile();
    
    
    /**
     * Log the user off of the system. Sets all the data to null.
     *  
     */
    public void logoff();

    /**
     * Sets a course to the current course. Loads the course his sco's and
     * returns a panel representing the course.
     * 
     * @param course The course to select.
     * @return A panel representing the course.
     *  
     */
    public CenterSubPanel loadCourse(Course course);

    /**
     * Sets a sco to the course current sco. Returns a panel with the
     * sco-applet.
     * 
     * @param sco The sco to select.
     * @return A panel with the sco-applet.
     *  
     */
    public CenterSubPanel loadSco(Sco sco);

    /**
     * Change the current user his account.
     * 
     * @param password The current password of the user. It will be used to
     *            validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     *            for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param c The new SchoolClass of the user.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email, SchoolClass c)
            throws RegisterException;

    /**
     * Change the current user his account who not is linked to a school.
     * 
     * @param password The current password of the user. It will be used to
     *            validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     *            for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @param schoolLogin The schoolloginname of the school of the user.
     * @param group The group from the user.
     * @param groupPassword The password corresponding with the specified group
     *            and the school.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email, String schoolLogin, Group group,
            String groupPassword) throws RegisterException;

    /**
     * Change the current user his account.
     * 
     * @param password The current password of the user. It will be used to
     *            validate the current user.
     * @param newPassword The new password of the user.
     * @param reNewPassword The re-password for the user. It is used to check
     *            for a typing error.
     * @param firstName The firstname of the user.
     * @param middleName The middlename of the user. <br>
     *            e.g: <code>Van</code>
     * @param lastName The lastname (familyname) of the user.
     * @param email The e-mail address of the user.
     * @throws fi.dwo.client.system.RegisterException If some
     *             register-information is incorrect or the user already exists.
     *  
     */
    public void changeAccount(String password, String newPassword,
            String reNewPassword, String firstName, String middleName,
            String lastName, String email) throws RegisterException;

    /**
     * Adds a class to the school of the current user. The current user will
     * also be the teacher. The operation is only carried out if the user is a
     * teacher.
     * 
     * @param className The name of the new class.
     * @return boolean If the class is successfully inserted it returns true.
     *         Otherwise it returns false.
     * @throws fi.dwo.client.system.ClassException If some class-information is
     *             incorrect.
     *  
     */
    public boolean addClass(String className) throws ClassException;


	/**
     * Edit a school to the database. 
     * 
     * @param schoolID The ID of the school..
     * @param schoolName The new name of the school.
     * @param schoolLogin The new login name of the school.
     * @param studentPassw new Password for students.
     * @param teacherPassw new Password for teachers.
     * @return new school 
     * @throws fi.dwo.client.system.ClassException If some school-information is
     *             incorrect.
     *  
     */
  //  public School editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw) throws SchoolException;
    
    	/**
     * Adds a school to the database. 
     * 
     * @param id The id of the new school 
     * @param schoolName The name of the new school.
     * @param schoolLogin The login name of the new school.
     * @param studentPassw Password for students.
     * @param teacherPassw Password for teachers.
     * @param schooladminPasswd Password for SchoolAdministrators
     * @return boolean If the school is successfully inserted it returns true.
     *         Otherwise it returns false.
     * @throws fi.dwo.client.system.ClassException If some school-information is
     *             incorrect.
     *  
     */
   // public School addSchool(int id, String schoolName, String schoolLogin, String studentPassw, String teacherPassw, String schooladminPasswd) throws SchoolException;
    
    /**
     * Deletes the account of the current user.
     *  
     */
    public void deleteUser();

    /**
     * Deletes the specified class from the system.
     * 
     * @param c The class to delete.
     * @return boolean If the class was successfully deleted it returns true.
     *         Otherwise it returns false.
     *  
     */
    public boolean deleteClass(SchoolClass c);

    /**
     * Returns the current resultsmodule.
     * 
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule();

    /**
     * Returns the current resultsmodule with the results of the specified class.
     * 
     * @param schoolClass The SchoolClass to show the results from.
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule(SchoolClass schoolClass);

    /**
     * Returns the current resultsmodule with the selected courses.
     * 
     * @param courses The courses default selected.
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule(Course[] courses);

    /**
     * Returns the current resultsmodule with the selected courses.
     * 
     * @param courses The courses default selected.
     * @param showSco If true and only one course is specified, the sco's of the course are showen.
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule(Course[] courses, boolean showSco);

    /**
     * Renames the specified class.
     * 
     * @param schoolClass The class to rename.
     * @param newName The new name for the class.
     * @return If the class is successfully renamed it returns true. Otherwise
     *         it returns false.
     */
    public boolean renameClass(SchoolClass schoolClass, String newName);

    /**
     * Shows the user that he must wait for a while with the dafault wait
     * string, because for example some data is loaded. The wait can be stopped
     * with <code>setReady()</code>
     * 
     * @see fi.dwo.client.domain.DwoIF#setReady()
     */
    public void setWait();

    /**
     * Shows the user that he must wait for a while with the specified wait
     * string, because for example some data is loaded. The wait can be stopped
     * with <code>setReady()</code>
     * 
     * @see fi.dwo.client.domain.DwoIF#setReady()
     */
    public void setWait(String waitText);

    /**
     * Clears the graphical wait-situation for the user. The wait was started
     * with <code>setWait()</code>
     * 
     * @see fi.dwo.client.domain.DwoIF#setWait()
     */
    public void setReady();
    
    /**
     * Clears all the information of the current user out of the memory,
     * so no cashing problems can appear.
     *
     */
    public void clearCurrentUserData();
    
    /**
    @return fi.dwo.client.domain.Course[]
     */
    public Course[] getEditableCourses();
    
    /**
    @param name
    @param description
    @return fi.dwo.client.domain.Course
     * @throws CourseException
     */
    public Course addCourse(String name, String description);
    
    /**
    @param course
    @return boolean
     */
    public boolean updateCourse(Course course);
    
    /**
    @param appletID
    @param name
    @param description
    @return fi.dwo.client.domain.Sco
     */
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description);
    
    /**
    @param sco
    @return boolean
     */
    public boolean updateSco(Sco sco);
    
    /**
    @param course
    @return boolean
     */
    public boolean deleteCourse(Course course);
    
    /**
    @param sco
    @return boolean
     */
    public boolean deleteSco(Sco sco);
    
    /**
    @return fi.dwo.client.domain.AppletConfig[]
     */
    public AppletConfig[] getAppletConfig();
    
    /**
    @return fi.dwo.client.domain.School[]
     */
    public School[] getSchool();
    
    /**
    @param appletConfig
    @return fi.dwo.client.gui.ScoPanel
     */
    public ScoPanel previewSco(AppletConfig appletConfig);
    
    /**
    @param sco
    @return fi.dwo.client.gui.ScoPanel
     */
    public ScoPanel previewSco(Sco sco);
    
    public void setCurrentSco(Sco sco);
        
    public int getScoViewNr();
    
    public int getCourseViewNr();

    /**
     * Delete a school.
     * @param sc School
     * @return true if success
     */
	public boolean deleteSchool(School sc);

	/**
	 * Verwissel de sequencenrs van twee Sco's.
	 * De Sco's moeten tot dezelfe Course behoren.
	 * @param sco1 Sco
	 * @param sco2 Sco
	 * @return boolean: succes of gefaald
	 */
	public boolean swapSco(Sco sco1, Sco sco2);

	public ResultsModuleIF getUserResultsModule(Course course);

	public String LMSGetValue(Sco sco, User user, String dataModelElement);

	public String LMSSetValue(Sco sco, String dataModelElement, String value);

	public School addSchool(int id, String schoolName, String schoolLogin,
			SchoolPasswdMap schoolPasswdMap) throws SchoolException;

	public School editSchool(int schoolID, String schoolName,
			String schoolLogin, SchoolPasswdMap schoolPasswdMap) throws SchoolException;

}