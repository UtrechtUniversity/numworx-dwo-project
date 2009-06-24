// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\DWO.java

package fi.dwo.client.domain;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import fi.beans.appletutil.AppletUtil;
import fi.beans.base64code.StringCodeObject;
import fi.beans.fidentity.Fidentity;
import fi.beans.mainframe.MainFrame;
import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.tooltip.ToolTipManager;
import fi.beans.jvmchecker.JVMChecker;

import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.CourseIcon;
import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ScoLinkedLabel;
import fi.dwo.client.gui.ScoPanel;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.CourseException;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.ScoException;
import fi.dwo.client.system.TextMapper;

/**
 * This is the main applet class of the DWO.<br>
 * At the start, a WelcomePanel is showed.<br>
 * @author M.J.B. Kupers
 *  
 */
public class DWO extends Applet implements SCORM12APIInterface, DwoIF  {

    private Course currentCourse;

    private Course courseList[];

    private User currentUser;

    private ResultsModule resultsModule;

    private Panel panel;

    private String waitText;
    
    private int nestedWait;
    
    private DwoProfile dwoProfile;
    
    private int dwoProfileID;
    
    private String userName;
    
    private String passWord;

	private Fidentity fidentity;
	
	private int courseViewNr;
	
	private int scoViewNr;

	private String languageOveride;
	
	
    /**
     * Creates a new DWO object.
     *  
     */
    public DWO() {
        nestedWait = 0;
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
     */
    public DWO(String[] args) {
        nestedWait = 0;
        dwoProfileID = 1;
        int o = 0;
// allow update van SERVLET
        if(args!= null && args.length>1 && "-s".equals(args[0]))
        {
        	fi.dwo.client.persistence.DbAccessCreator.SERVLET = args[1];
        	o = 2;
        }
// allow definitie van Locale.
        if(args != null && args.length>1+o && "-l".equals(args[o]))
        {
        	languageOveride=args[o+1];
        	o += 2;
        }
        if (args != null && args.length>o && args[o] != null) {
            try	{
            	dwoProfileID = Integer.parseInt(args[o]);
           	}catch(NumberFormatException e){}
           	if (args.length>2+o && args[1+o] != null && args[2+o]!= null) {
           		userName = args[1+o];
           		passWord = args[2+o];
           	}
        }
        
    }

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
            throws LoginException {
    	if(password == null)
    		currentUser = PersistenceFacade.instance().login(username);
    	else
    		currentUser = PersistenceFacade.instance().login(username, password);
        if(currentUser instanceof Admin) 
        		DwoHelper.setAdminLoggedIn(true);
        else DwoHelper.setAdminLoggedIn(false);
        if(currentUser.getID()==4 		//peterb
        	|| currentUser.getID()==8691 	//peterb_gr
        	|| currentUser.getID()==24073	//peterb_mw
        	|| currentUser.getID()==13584) 	//peterb_mbo
        		DwoHelper.setScormExportLoggedIn(true);
        else DwoHelper.setScormExportLoggedIn(false);
        if(	currentUser.getID()==4 		//peterb
        		|| currentUser.getID()==8691 	//peterb_gr
        		|| currentUser.getID()==24073	//peterb_mw
        		|| currentUser.getID()==13584 	//peterb_mbo
        		|| currentUser.getID()==22194) 	//harmh
        	DwoHelper.setAppletExportLoggedIn(true);
        else DwoHelper.setAppletExportLoggedIn(false);
        
        return currentUser != null;
    }

    /**
     * Login as guest.
     * CurrentUser becomes an instance of class Guest.
     * @see Guest
     * 
     * @return If the guest was successfully logged in it returns true.
     *         Otherwise it returns false.
     * @throws fi.dwo.client.system.LoginException If some login-information is
     *             incorrect.z
     *  
     */
    public boolean login() throws LoginException {
        currentUser = Guest.instance();
        
        /*Object[] args = new Object[5];
        args[0] = "http://www.fi.uu.nl/wisweb/scorm/scos/nabouwenaanzichten/NabouwenAanzichten1.htm";
        args[1] = "name";
        args[2] = "800";
        args[3] = "600";
        args[4] = "yes";
        String result = (String) window.call("NewWindow", args);
        
        System.out.println("Aanroep NewWindow:"+ result);*/
        
        return true;
    }

    /**
     * Checks a string if he is empty or null.
     * 
     * @param s The string to be check.
     * @return If the string is null or empty true is returned. Otherwise false
     *         is returned.
     */
    private boolean isEmpty(String s) {
        return (s == null) || (s.equals(""));
    }

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
            String lastname, String email) throws RegisterException {

        String[] arguments = new String[2];
// checks:
// no spaces (trimmed)
// ascii only
// aselect: ....
        if (isEmpty(username)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValid(username)) {
        	arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
        	arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
        	throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
        } else if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValidEmail(email)) {
        	arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
        	arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
        	throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
        }
        if (!password.equals(rePassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            return PersistenceFacade.instance().register(username, password, firstname, middlename, lastname, email);
        }
    }
/**
 * Test of we een echt e-mail adres hebben.
 * Test of het ASCII is, minus spatie en del.
 * @param email de email om te testen
 * @return true indien valide 
 */
private static boolean isValidEmail(String email) {
	char[] chars = email.toCharArray();
	for (int i = 0; i < chars.length; i++) {
		char c = chars[i];
		if ( c <= 0x20 || c >= 0x7F ) 
			return false;
	}
	return true;
	}

//  checks:
//  no spaces (trimmed)
//  ascii only
//  aselect: ....
/**
 * Test username op illegale characters.
 * Alleen ASCII is toegestaan, echter geen =?* en geen geen ( ) , of \
 * spaties zijn wel toegestaan, maar niet aan begin of eind.
 * @param username String
 * @return true als username voldoet
 * @see org.aselect.server.udb.jndi.JNDIConnector#getUserProfile(String)
 */
    public static boolean isValid(String username) {
    	
    	if ( !username.trim().equals(username)) 
    		return false;
    	char[] chars = username.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if(c < 0x20 || c >= 0x7F 		// ascii, no space?, no delete?
					|| c == '('				// aselect verbiedt =*? 
					|| c == ')'				// maar ook , \ ( en ) mogen niet
					|| c == '*'
					|| c == '?'
					|| c == '='
					|| c == '\\' 
					|| c == ','
					|| c == ';'				// beter van niet in LDAP
					|| c == '+'
					|| c == '#'				// nieuw, werkt niet in PHP
			)
				return false;
		}
		
		for (int i = 0; i < realms.length; i++) {
			if( username.endsWith( realms[i]))
				return false;
		}
		return true;
	}

    /**
     * Lijst met realms die niet in een te registreren username mogen voorkomen.
     * Zij komen wel in de lijst van users voor, maar dan alleen via getInitialUser
     * @see #getInitialUser()
     */
    private static final String[] realms = {
    	"@kennisnet.org",
    	"@fi.uu.nl",
    	"@w2k3.fi.uu.nl",
    	"@soliscom.uu.nl"
    };
    
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
            String groupPassword) throws RegisterException {

        String[] arguments = new String[2];
//      checks:
//      no spaces (trimmed)
//      ascii only
//      aselect: ....
        if (isEmpty(username)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValid(username)) {
        	arguments[0] = TextMapper.getText(TextMapper.GUIR_USERNAME);
        	arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
        	throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
        } else if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastname)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (!isValidEmail(email)) {
        	arguments[0] = TextMapper.getText(TextMapper.GUIR_EMAIL);
        	arguments[1] = TextMapper.getText(TextMapper.GUIR_PERSONALINFO);
        	throw new RegisterException(RegisterException.RE_WRONG_FORMAT, arguments);
        } else if (isEmpty(schoolLogin)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLLOGIN);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (group == null) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLGROUP);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(groupPassword)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIR_SCHOOLPASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIR_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        }

        if (!password.equals(rePassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            return PersistenceFacade.instance().register(username, password, firstname, middlename, lastname, email, schoolLogin, group, groupPassword);
        }
    }

    /**
     * Returns all the available groups.
     * 
     * @return An array of all the available groups.
     *  
     */
    public Group[] getGroups() {
        try {
            return (Group[]) PersistenceFacade.instance().get(Group.class);
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /**
     * Returns the current user who is logged in. If the user is logged in as a
     * guest, NULL is returned.
     * 
     * @return the current user who is logged in. If the user is logged in as a
     *         guest, NULL is returned.
     *  
     */
    public User getUser() {
        return currentUser;
    }
    
    private Course[] selectDwoProfileCourses(Course[] completeList){
    	Vector v = new Vector();
        for(int i=0 ; i<courseList.length; i++){
        	if(completeList[i].getDwoProfile() == dwoProfile.getID()) v.addElement(completeList[i]);
		}
		Course[] selectedCourses = new Course[v.size()];
		for(int i=0 ; i<selectedCourses.length; i++){
        	selectedCourses[i] = (Course)v.elementAt(i);
        }
        return selectedCourses;
    }

    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     * 
     * @return An array of all the courses for the current user.
     *  
     */
    public Course[] getCourses() {
        try {
            courseList = PersistenceFacade.instance().getCourses(currentUser);
            return selectDwoProfileCourses(courseList);
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;
        }
    }
    
    /**
     * Returns all the courses available for the user. If some courses are
     * available for the users school, they are also returned.
     * 
     * @return An array of all the courses for the current user.
     *  
     */
    public Course[] getCourses(SchoolClass schoolClass) {
        try {
            courseList = PersistenceFacade.instance().getCourses(schoolClass);
            return selectDwoProfileCourses(courseList);
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;
        }
    }
    
    public DwoProfile getDwoProfile()
    {	return dwoProfile;
    }

    /**
     * Log the user off of the system. Sets all the data to null.
     *  
     */
    public void logoff() {
        currentUser = null;
        currentCourse = null;
        courseList = null;
        resultsModule = null;
        MapperCreator.instance(Applet.class).removeAllObjects();
        MapperCreator.instance(Sco.class).removeAllObjects();
        MapperCreator.instance(Course.class).removeAllObjects();
    }

    /**
     * Sets a course to the current course. Loads the course his sco's and
     * returns a panel representing the course.
     * 
     * @param course The course to select.
     * @return A panel representing the course.
     *  
     */
    public CenterSubPanel loadCourse(Course course) {
        currentCourse = course;
        return course.getCoursePanel();
    }

    /**
     * Sets a sco to the course current sco. Returns a panel with the
     * sco-applet.
     * 
     * @param sco The sco to select.
     * @return A panel with the sco-applet.
     *  
     */
    public CenterSubPanel loadSco(Sco sco) {
        if(currentCourse!=null) currentCourse.setCurrentSco(sco);
        return sco.getScoPanel(this, currentUser);
    }

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
            throws RegisterException {

        String[] arguments = new String[2];
        if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_OLD_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        }

        if (!newPassword.equals(reNewPassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            PersistenceFacade.instance().changeAccount(currentUser, password, newPassword, firstName, middleName, lastName, email, c);
        }

    }

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
            String groupPassword) throws RegisterException {
        String[] arguments = new String[2];
        if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_OLD_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(schoolLogin)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLLOGIN);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (group == null) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLGROUP);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(groupPassword)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_SCHOOLPASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_SCHOOLINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        }

        if (!newPassword.equals(reNewPassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            PersistenceFacade.instance().addToSchool(currentUser, schoolLogin, group, groupPassword);
            PersistenceFacade.instance().changeAccount(currentUser, password, newPassword, firstName, middleName, lastName, email);
        }

    }

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
            String lastName, String email) throws RegisterException {

        String[] arguments = new String[2];
        if (isEmpty(password)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_OLD_PASSWORD);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(firstName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_FIRSTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_REGISTERINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(lastName)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_LASTNAME);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        } else if (isEmpty(email)) {
            arguments[0] = TextMapper.getText(TextMapper.GUIP_EMAIL);
            arguments[1] = TextMapper.getText(TextMapper.GUIP_PERSONALINFO);
            throw new RegisterException(RegisterException.RE_MANDATORY, arguments);
        }

        if (!newPassword.equals(reNewPassword)) {
            throw new RegisterException(RegisterException.RE_WRONG_SECOND_PASSWORD);
        } else {
            PersistenceFacade.instance().changeAccount(currentUser, password, newPassword, firstName, middleName, lastName, email);
        }

    }

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
    public boolean addClass(String className) throws ClassException {
        if (currentUser instanceof Teacher) {
            SchoolClass sc = PersistenceFacade.instance().addClass((Teacher) currentUser, className);
            ((Teacher) currentUser).addClass(sc);
            
            if(currentUser.getSchool() != null) {
                currentUser.getSchool().addClass(sc);
            }
        }
        return false;
    }
    
    /**
     * Adds a school to the database. 
     * @param id The id of the new school
     * @param schoolName The name of the new school.
     * @param schoolLogin The login name of the new school.
     * @param studentPassw Password for students.
     * @param teacherPassw Password for teachers.
     * @return boolean If the school is successfully inserted it returns true.
     *         Otherwise it returns false.
     * @throws fi.dwo.client.system.ClassException If some school-information is
     *             incorrect.
     *  
     */
    public School addSchool(int id, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)  throws SchoolException {
    	return PersistenceFacade.instance().addSchool(id, schoolName, schoolLogin, studentPassw, teacherPassw);
        
    }

	/**
     * Edit a school to the database. 
     * 
     * @param schoolID The ID of the school.
     * @param schoolName The new name of the school.
     * @param schoolLogin The new login name of the school.
     * @param studentPassw new Password for students.
     * @param teacherPassw new Password for teachers.
     * @return school
     * @throws fi.dwo.client.system.ClassException If some school-information is
     *             incorrect.
     *  
     */
    public School editSchool(int schoolID, String schoolName, String schoolLogin, String studentPassw, String teacherPassw)  throws SchoolException {
    	return PersistenceFacade.instance().editSchool(schoolID, schoolName, schoolLogin, studentPassw, teacherPassw);
        
    }
    
    /**
     * Deletes the current user.
     *  
     */
    public void deleteUser() {
        try {
            PersistenceFacade.instance().deleteUser(currentUser);
        } catch (RegisterException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Deletes the specified class from the system.
     * 
     * @param c The class to delete.
     * @return boolean If the class was successfully deleted it returns true.
     *         Otherwise it returns false.
     *  
     */
    public boolean deleteClass(SchoolClass c) {
        boolean returnvalue = false;
        try {
            if (!PersistenceFacade.instance().deleteClass(c, true)) {
                if (DwoMessageDialog.showConfirmDialog(this, TextMapper.getText(TextMapper.GUIC_CLASS_NOT_EMPTY)
                        + "?", TextMapper.getText(TextMapper.GUIC_CLASS_NOT_EMPTY_TITLE), DwoMessageDialog.YES_NO_OPTION) == DwoMessageDialog.YES_OPTION) {
                    returnvalue = PersistenceFacade.instance().deleteClass(c, false);
                }
            } else {
                returnvalue = true;
            }
        } catch (ClassException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
        }

        if (returnvalue) {
            if (currentUser instanceof Teacher) {
                ((Teacher) currentUser).deleteClass(c);
            }
            if(currentUser.getSchool() != null) {
                currentUser.getSchool().deleteClass(c);
            }
        }

        return returnvalue;
    }

    /**
     * Returns the current resultsmodule.
     * 
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule() {
        return getResultsModule(getCourses(), false);
    }
    
    /**
     * Returns the current resultsmodule with the selected courses.
     * 
     * @param courses The courses default selected.
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule(Course[] courses) {
        return getResultsModule(courses, true);
    }

    /**
     * Returns the current resultsmodule with the results of the specified class.
     * 
     * @param schoolClass The SchoolClass to show the results from.
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule(SchoolClass schoolClass) {
        if (resultsModule == null) {
            resultsModule = new ResultsModule(new Course[0], (Teacher) currentUser, this);
        }
        
        resultsModule.reset();
        resultsModule.selectCourses(getCourses(schoolClass), false);
        resultsModule.zoomIn(schoolClass);
        return resultsModule;        
    }
    /**
     * Returns the current resultsmodule with the selected courses.
     * 
     * @param courses The courses default selected.
     * @return The current results module.
     *  
     */
    public ResultsModuleIF getResultsModule(Course[] courses, boolean showSco) {
        if (resultsModule == null) {
            resultsModule = new ResultsModule(new Course[0], (Teacher) currentUser, this);
        }
        
        resultsModule.reset();
        resultsModule.selectCourses(courses, false);
        if(showSco && (courses.length == 1)) {
            resultsModule.zoomIn(courses[0]);
        }
        return resultsModule;
    }

    /**
     * Initialises the applet.
     */
    public void init() {
    	// override van swing properties... 
    	// TODO dit ook testen in een applet omgeving!
    	UIDefaults defaults;
    	defaults = UIManager.getDefaults();
    	defaults.addResourceBundle("fi/dwo/client/gui/resources/swing");
// standaard Tooltip geel
    	UIManager.put("ToolTip.background", new ColorUIResource(255, 247, 200));
        
        String lang = getParameter("language");
        System.out.println(lang);
        if ((lang != null) && (!lang.equals(""))) {
            TextMapper.setLanguage(lang);
            fi.dwo.parameters.system.TextMapper.setLanguage(lang);
        }
        
        boolean guestUser = false; // Wim: teruggezet
        String guestUserString = getParameter("guestUser");
        if(guestUserString!=null && guestUserString.equals("true")) {
        	guestUser = true;
        }
        
        String scoViewNrString = getParameter("scoViewNr");
        if(scoViewNrString!=null && (!scoViewNrString.equals(""))) {
        	try	{
        		scoViewNr = Integer.parseInt(scoViewNrString);
            }catch(Exception e){}
        }
        
        String courseViewNrString = getParameter("courseViewNr");
        if(courseViewNrString!=null && (!courseViewNrString.equals(""))) {
        	try	{
        		courseViewNr = Integer.parseInt(courseViewNrString);
            }catch(Exception e){}
        }
        
        boolean umpc = false;
        String umpcString = getParameter("umpc");
        if(umpcString!=null && umpcString.equals("true")) {
        	umpc = true;
        }
        
        String key = getParameter("key");
        if(key == null) {
            key = "";
        }
        DwoHelper.setKey(key);
        DwoHelper.setAu(new AppletUtil(this));
        DwoHelper.setApplet(this);
        DwoHelper.setUmpc(umpc);
        
        if(!DwoHelper.isApplication()) {
        	dwoProfileID = 1; 
        	String dwoProfileString = getParameter("profile");
             if ((dwoProfileString != null) && (!dwoProfileString.equals(""))) {
                 try	{
                 	dwoProfileID = Integer.parseInt(dwoProfileString);
                	}catch(Exception e){}
             }
             JVMChecker jvmChecker = new JVMChecker(this);
             jvmChecker.check();
        }
       
        try {
			dwoProfile = (DwoProfile)PersistenceFacade.instance().get(dwoProfileID,DwoProfile.class);
		} catch (PersistenceException e) {
		}
		GuiConstants.setDwoProfile(dwoProfileID);
        
		
        /* ToolTipManager ttm = */ new ToolTipManager(this);
        GuiCreator gc = new GuiCreator(this);
        try {
			PersistenceFacade.instance().reConnect();
		} catch (PersistenceException e) {
		}
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);

        this.setLayout(new BorderLayout());
        this.setLayout(null);
        
        if(userName!=null && passWord!=null) {
	        try {
	            GuiCreator.instance().login(userName, passWord);
	            return;
	        } catch (LoginException exc) {
	            DwoMessageDialog.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), DwoMessageDialog.ERROR_MESSAGE);
	        }
        }
        else if(guestUser) {
        	try {
	            GuiCreator.instance().login();
	            return;
	        } catch (LoginException exc) {
	            DwoMessageDialog.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), DwoMessageDialog.ERROR_MESSAGE);
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
// einde
        
        panel = gc.getWelcomePanel();
        panel.setVisible(false);
        panel.setSize(this.getSize());
        panel.setLocation(0, 0);
        add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        
        
    }
    
    public int getCourseViewNr() {
    	return courseViewNr;
    }
    
    public int getScoViewNr() {
    	return scoViewNr;
    }
    

    /**
     * Overides the Applet.paint method. Draws a wait string, and calls the
     * super. If the mainpanel is made invisible, nothing is showed above the
     * wait string, so the wait string is showed.
     */
    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getSize().width, getSize().height);
        String text = waitText;
        text += "...";
        g.setFont(GuiConstants.HEADER_TEXT);
        g.setColor(Color.black);
        FontMetrics fm = this.getFontMetrics(GuiConstants.HEADER_TEXT);
        int x = (getSize().width / 2) - (fm.stringWidth(text) / 2);
        int y = (getSize().height / 2) - (fm.getHeight() / 2);
        g.drawString(text, x, y);
        super.paint(g);
    	
    }

    /**
     * Sets the current panel of the applet.
     * 
     * @param p The panel to set.
     * @see fi.dwo.client.domain.DwoIF#setPanel(java.awt.Panel)
     */
    public void setPanel(Panel p) {
        if(panel != null) {
	        panel.setVisible(false);
	        this.remove(panel);
	       // panel.setVisible(true);
        }
        this.panel = p;
        panel.setVisible(false);
        this.add(panel, 0);
        panel.setVisible(true);
        panel.requestFocus();
        
    }

    /**
     * Returns the LMS value for the specified sco and the specified user.
     * 
     * @param sco The sco wherefrom the LMS value is asked.
     * @param user The usere wherefrom the LMS value is asked.
     * @param iDataModelElement The parameter to ask for.
     * @return The value representing for the specified sco, user and parameter.
     */
    public String LMSGetValue(Sco sco, User user, String iDataModelElement) {
        if(iDataModelElement.equals(SCORM12APIInterface.USER_GROUP)) {
            if(currentUser == null || currentUser instanceof Guest) {
                return SCORM12APIInterface.UG_GUEST;
            } else if(currentUser instanceof Teacher) {
                return SCORM12APIInterface.UG_TEACHER;
            } else {
                return SCORM12APIInterface.UG_STUDENT;
            }
            
        } else {
	        try {
	            return PersistenceFacade.instance().LMSGetValue(sco, user, iDataModelElement);
	        } catch (PersistenceException e) {
	            DwoMessageDialog.showMessageDialog(this, e.getMessage());
	            return "";
	        }
        }

    }

    /**
     * Sets the LMS value for the specified sco for the current user.
     * 
     * @param sco The sco wherefrom the LMS value is set.
     * @param iDataModelElement The dataModeElement to set.
     * @param iValue The new value for the dataModeElement.
     * @return String representing a boolean
     *         <ul>
     *         <li><code>true</code> result indicates that the LMSSetValue()
     *         was successful</li>
     *         <li><code>false</code> result indicates that the LMSSetValue()
     *         was unsuccessful</li>
     *         </ul>
     */
    public String LMSSetValue(Sco sco, String iDataModelElement, String iValue) {
        try {
            return PersistenceFacade.instance().LMSSetValue(sco, currentUser, iDataModelElement, iValue);
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return false + "";
        }

    }

    /**
     * Indicate that a sco has been ended, so the data can be cleared.
     * 
     * @param sco The sco that is ended.
     */
    public void endSco(Sco sco) {
        MapperCreator.instance(Applet.class).removeObject(sco.getAppletID());
    }

    /**
     * Stops the current applet. Indicates at the current course that the applet
     * will be stopped.
     */
    public void stop() {
    	this.setWait();
        super.stop();
        if (currentCourse != null) {
            currentCourse.end();
        }
        logoff();
    	this.setReady();
    }

    /**
     * The main method of the class.
     * 
     * @param args
     * @throws Exception 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     */
    public static void main(String[] args) throws Exception {
    	//String  lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
    	//lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    	//lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    	//lookAndFeel = UIManager.getSystemLookAndFeelClassName();
    	//UIManager.setLookAndFeel(lookAndFeel);
    	int width = 805;
        int height = 615;
        MainFrame mf = new MainFrame(new DWO(args), width, height);
        mf.setTitle("DWO");
        mf.pack();
        mf.show();
        mf.setSize(width + 10, height + 20);
    }

    /**
     * Renames the specified class.
     * 
     * @param schoolClass The class to rename.
     * @param newName The new name for the class.
     * @return If the class is successfully renamed it returns true. Otherwise
     *         it returns false.
     * @see fi.dwo.client.domain.DwoIF#renameClass(fi.dwo.client.domain.SchoolClass,
     *      java.lang.String)
     */
    public boolean renameClass(SchoolClass schoolClass, String newName) {
        try {
            PersistenceFacade.instance().renameClass(schoolClass, newName);
            schoolClass.setClassName(newName);
            return true;
        } catch (ClassException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /**
     * Shows a wait cursor and the default wait message to indicate that the
     * user must wait for a while.
     * 
     * @see fi.dwo.client.domain.DwoIF#setWait()
     */
    public void setWait() {
        setWait(TextMapper.getText(TextMapper.GUI_WAIT_A_MOMENT));
    }

    /**
     * Shows a wait cursor and the specified wait message to indicate that the
     * user must wait for a while.
     * 
     * @see fi.dwo.client.domain.DwoIF#setWait()
     */
    public void setWait(String waitText) {
        if(nestedWait == 0) {
	        setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        this.waitText = waitText;
	        if (panel != null) {
	            panel.setVisible(false);
	        }
	        if(this.getGraphics()!=null) paint(this.getGraphics());
        }
        nestedWait++;
    }

    /**
     * Hides the wait cursor and the message what was showed up with
     * <code>setWait()</code>
     * 
     * @see fi.dwo.client.domain.DwoIF#setReady()
     */
    public void setReady() {
        if(nestedWait == 1) {
	        if (panel != null) {
	            panel.setVisible(true);
	            panel.requestFocus();
	        }
	        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        nestedWait--;
    }

    /**
     * Clears all the information of the current user out of the memory,
     * so no cashing problems can appear.
     *
     * @see fi.dwo.client.domain.DwoIF#clearCurrentUserData()
     */
    public void clearCurrentUserData() {
        MapperCreator.instance(User.class).removeObject(currentUser.getUserID());
        currentUser = null;
        currentCourse = null;
        courseList = null;
        resultsModule = null;        
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getEditableCourses()
     */
    public Course[] getEditableCourses() {
        /*if(currentUser instanceof Teacher) {
	        try {
	            courseList = PersistenceFacade.instance().getEditableCourses((Teacher) currentUser);
	            return courseList;
	        } catch (PersistenceException e) {
	            DwoMessageDialog.showMessageDialog(this, e.getMessage());
	            return null;
	        }
        } 
        else if(currentUser instanceof Admin) {
	        try {
	            courseList = PersistenceFacade.instance().getEditableCourses((Admin) currentUser);
	            return courseList;
	        } catch (PersistenceException e) {
	            DwoMessageDialog.showMessageDialog(this, e.getMessage());
	            return null;
	        }
        }else {
            return null;
        }*/
        try {
	        courseList = PersistenceFacade.instance().getEditableCourses(currentUser);
	        return selectDwoProfileCourses(courseList);
	    } catch (PersistenceException e) {
	        DwoMessageDialog.showMessageDialog(this, e.getMessage());
	        return null;
	    }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addCourse(java.lang.String, java.lang.String)
     */
    public Course addCourse(String name, String description) {
        try {
            return PersistenceFacade.instance().addCourse(currentUser.getSchool(), name, description, dwoProfile);
        } catch(CourseException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;            
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#updateCourse(fi.dwo.client.domain.Course)
     */
    public boolean updateCourse(Course course) {
        try {
            return PersistenceFacade.instance().updateCourse(course);
        } catch(CourseException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return false;            
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#addSco(int, java.lang.String, java.lang.String)
     */
    public Sco addSco(Course course, AppletConfig appletConfig, String name, String description) {
        try {
            return PersistenceFacade.instance().addSco(course, appletConfig, name, description);
        } catch(ScoException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;            
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#updateSco(fi.dwo.client.domain.Sco)
     */
    public boolean updateSco(Sco sco) {
        try {
            return PersistenceFacade.instance().updateSco(sco);
        } catch(ScoException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return false;            
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteCourse(fi.dwo.client.domain.Course)
     */
    public boolean deleteCourse(Course course) {
        try {
            return PersistenceFacade.instance().deleteCourse(course);
        } catch(CourseException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#deleteSco(fi.dwo.client.domain.Sco)
     */
    public boolean deleteSco(Sco sco) {
        try {
            return PersistenceFacade.instance().deleteSco(sco);
        } catch(ScoException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getAppletConfig()
     */
    public AppletConfig[] getAppletConfig() {
        try {
            AppletConfig[] ac = (AppletConfig[]) PersistenceFacade.instance().get(AppletConfig.class, getLocale());
//            for(int i = 0; i < ac.length; i++) {
//                System.out.println("AppletConfig: " + ac[i].getAppletID() + "; " + ac[i].getName() + "; " + ac[i].getLaunchdata());
//            }
            return  ac;
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#getSchools()
     */
    public School[] getSchool() {
        try {
            School[] sc = (School[]) PersistenceFacade.instance().get(School.class);
            return  sc;
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.AppletConfig)
     */
    public ScoPanel previewSco(AppletConfig appletConfig) {
        Sco dummy = new Sco();
        dummy.setAppletID(appletConfig.getAppletID());
        dummy.setName(appletConfig.getName());
        dummy.setLaunchdata((Hashtable)new StringCodeObject(appletConfig.getLaunchdata()).toObject());
        dummy.setLessonMode(Sco.BROWSE);
        return dummy.getScoPanel(this, null);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.DwoIF#previewSco(fi.dwo.client.domain.Sco)
     */
    public ScoPanel previewSco(Sco sco) {
    	sco.setLessonMode(Sco.BROWSE);
        return sco.getScoPanel(this, null);
        //return sco.getNewScoPanel(this, null);
    }
    
    
    
    public String LMSInitialize(String iParam) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSInitialize(iParam);
        }
        else return null;
    }

    public String LMSFinish(String iParam) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSFinish(iParam);
        }
        else return null;
    }

    public String LMSGetValue(String iDataModelElement) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSGetValue(iDataModelElement);
        }
        else return null;
    }

    public String LMSSetValue(String iDataModelElement, String iValue) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSSetValue(iDataModelElement, iValue);
        }
        else return null;
    }

    public String LMSCommit(String iParam) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSCommit(iParam);
        }
        else return null;
    }

    public String LMSGetLastError() {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSGetLastError();
        }
        else return null;
    }

    public String LMSGetErrorString(String iErrorCode) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSGetErrorString(iErrorCode);
        }
        else return null;
    }

     public String LMSGetDiagnostic(String iErrorCode) {
        if(currentCourse!=null && currentCourse.getCurrentSco()!=null)
        {	return currentCourse.getCurrentSco().LMSGetDiagnostic(iErrorCode);
        }
        else return null;
    }

     /**
      * Geef mij een gebruiker buitenom.
      * 
      * @return a user.
      */
     private User getInitialUser()
     {
 		this.fidentity = Fidentity.getInstance(this);
        String username = fidentity.getUid();
 System.out.println("[" + username + "]");
        if(username == null||"".equals(username))
            return null;
        System.out.println(fidentity.getRole());
        System.out.println(fidentity.getSchoolUid());
        String className = fidentity.getClassName();
		System.out.println(className);
        if ("school".equals(fidentity.getRole()))
        {
            System.out.println("Guest from school " + fidentity.getSchoolUid());
            return null;
        }
        
        
        User u =  null; // Guest.instance();
        try
     {
         u = PersistenceFacade.instance().login(username);
         u.setFirstname(fidentity.getFirstName());
         u.setMiddleName(fidentity.getMiddleName());
         u.setLastName(fidentity.getSurName());
         u.setEmail(fidentity.getEmailAddress());
         
         /* TODO if user geen lid van school en fidentity.getBrin() != null
          * meld de user aan bij school
          */
         if(null != fidentity.getSchoolUid())
         {
        	 School school = u.getSchool();
        	 if( school == null ||
        	     !
        	     (school.getSchoolLogin().equals(fidentity.getSchoolUid())||
        	      fidentity.getSchoolUid().equals(String.valueOf(school.getSchoolID()))
        	     )
        	 )
        	 {
        		 try {
					addInitialUserToSchool(u);
				} catch (RegisterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 u = PersistenceFacade.instance().login(username);
                 school = u.getSchool();
        	 }
        	 setInitialUserInClass(className, u, school);
         } else
         {	 /* TODO als user lid en getBrin == null, meld user af! */
         }
         
         u.setLogout(!fidentity.isForeign()); // op verzoek van Peter, logout als          									  // een eigen account.
         u.setReadonly(false); // voor de klas keuze
         return u;
     } catch (LoginException e)
     {   String msg = TextMapper.getText(TextMapper.EXL_UNKNOWN_USER);
         if( msg.equals(e.getMessage()) )
         {  
             try
             {
                 if(fidentity.isRegistered()) 
                 {   
                     PersistenceFacade.instance().register(username, 
                             null, /* no password! */
                             fidentity.getFirstName(), 
                             fidentity.getMiddleName(),
                             fidentity.getSurName(), fidentity.getEmailAddress());
                     u = PersistenceFacade.instance().login(username);
                     if (fidentity.getBrin()!=null)
                     {
 System.out.println(fidentity.getBrin());
 System.out.println(fidentity.getSchoolUid());

                         addInitialUserToSchool(u);
                     }
                     // u kan nu een Teacher zijn...
                     u = PersistenceFacade.instance().login(username);
                     setInitialUserInClass(className, u, u.getSchool());
                     u.setLogout(!fidentity.isForeign()); // op verzoek van peter
                     u.setReadonly(true); // TODO is dit wel
                                                             // ok?
                 } else { 
                     u = new Guest() { 
                         public String getName() { 
                             return fidentity.getName();
                         }
                     };
                     u.setLogout(false); // fi-ers en uu-ers.
                 }
                 return u;
             } catch (RegisterException e1)
             { e1.printStackTrace();
             } catch (LoginException e2)
             { 
                 e2.printStackTrace();
             }
             
         }
     }
         return null;
     }

	/**
	 * Zet een gebruiker in een klas.
	 * @param className naam van klas
	 * @param u de gebruiker
	 * @param school die van u
	 */
	private void setInitialUserInClass(String className, User u, School school) {
		SchoolClass schoolClass = u.getInClass();
		 if ( className != null && 
		 	  (schoolClass == null || !schoolClass.getName().equals(className)))
		 {
			 SchoolClass[] classes = school.getClassList();
			 for (int i = 0; i < classes.length; i++) {
				if(className.equals(classes[i].getName()))
				{
					u.setInClass(classes[i]);
					try {
						PersistenceFacade.instance().changeAccount(u,null,null,u.getFirstname(),u.getMiddleName(),u.getLastName(),u.getEmail(),u.getInClass());
					} catch (RegisterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		 }
	}

	/**
	 * @param u
	 * @throws RegisterException
	 */
	private void addInitialUserToSchool(User u) throws RegisterException {
		try
		 {
		     Group group = findGroup(fidentity.getRole());
		     if(group!=null)
		     {
System.out.println(group.getName() + " " + group.getGroupID());
 				String schoolUid = fidentity.getSchoolUid();
System.out.println(schoolUid);
 				 String schoolname   = "";
		         String schoolpasswd = "";
		         School[] school = (School[]) PersistenceFacade.instance().get(School.class);
		         for (int i = 0; i < school.length; i++)
		         {
		             if(school[i].getSchoolLogin().equals (schoolUid) ||
		            	schoolUid.equals(String.valueOf(school[i].getSchoolID()))     
		             )
		             {
		                 schoolpasswd = school[i].getPasswd(group.getGroupID());
		                 schoolname   = school[i].getSchoolLogin();
		                 System.out.println(school[i].getSchoolLogin() + " " + group.getName() + " " + schoolpasswd);
		                 break;
		             }
		         }
		         PersistenceFacade.instance().addToSchool(u, schoolname, group, schoolpasswd);
		         MapperCreator.instance(User.class).removeAllObjects();
		     }
		 } catch (PersistenceException e1)
		 {
		     // TODO Auto-generated catch block
		     e1.printStackTrace();
		 }
	}
     /**
      * Converteer een role naar een Group. De namen van de group zijn niet
      * gelijk aan die van de 'role' (entree.kennisnet.nl). Er moet daarom
      * gemapped worden.
      * 
      * @param role
      * @return de Group die role representeerd.
      */
     private Group findGroup(String role)
     {
         if (role == null)
             return null;
         // TODO is deze mapping compleet?
         if ("docent".equals(role))
             role = "TEACHER";
         if ("leerling".equals(role))
             role = "STUDENT";
         //
         Group[] groups = getGroups();
         for (int i = 0; i < groups.length; i++)
         {
             Group group = groups[i];
             if (role.equalsIgnoreCase(group.getName()))
                 return group;
         }
         return null;
     }

	public boolean deleteSchool(School sc) {
		try {
			return PersistenceFacade.instance().deleteSchool(sc);
		} catch(SchoolException e) {
			DwoMessageDialog.showMessageDialog(this, e.getMessage());
			return false;
		}
	}

	
	/**
	 * Verwissel de sequencenrs van twee Sco's.
	 * De Sco's moeten tot dezelfe Course behoren.
	 * @param sco1 Sco
	 * @param sco2 Sco
	 * @return boolean: succes of gefaald
	 */
	public boolean swapSco(Sco sco1, Sco sco2)
	{
		try {
			return PersistenceFacade.instance().swapScoSequenceNr(sco1, sco2);
		} catch (ScoException e) {
			DwoMessageDialog.showMessageDialog(this, e.getMessage());
			return false;
		}
	}

	public ResultsModuleIF getUserResultsModule(Course course) {
		if(currentUser instanceof Guest)
			return null;
        return new UserResultsModule(course, currentUser, this);
	}

	/* (non-Javadoc)
	 * @see java.applet.Applet#getParameter(java.lang.String)
	 */
	public String getParameter(String name) {
		if("language".equals(name)&& languageOveride != null)
			return languageOveride;
			
		return super.getParameter(name);
	}
	
}