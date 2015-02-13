// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\User.java

package fi.dwo.dwojapplet.domain;

import java.text.MessageFormat;
import java.util.Date;

import fi.dwo.commons.system.TextMapper;

/**
 * This class is responsible for the User data. 
 * @author M.J.B. Kupers
 *  
 */
public class User implements UserGroup, Comparable {

	
	public static boolean DEFAULT_ICONIZER = false; // profile property
	
    private int userID;

    private String username;

    private String firstname;

    private String middleName;

    private String lastName;

    private String email;

    private SchoolClass inClass;

    private School school;
    
    private long lastLogin = System.currentTimeMillis();
    private long timeZone = 0L;
    /** 
     * Geeft het recht op  SCORM export 
     */
    public static final char SCORM_EXPORT_RIGHT = 's';
    /**
     * Geeft het recht op Applet export
     */
    public static final char APPLET_EXPORT_RIGHT = 'a';
    /**
     * geeft recht op administratie van algemene modules
     */
    public static final char PROFILE_ADMIN_RIGHT = 'p';
    
    /**
     * geeft recht om van klas te veranderen. Dit is een leerlingrecht
     */
    public static final char CHANGE_CLASS_RIGHT = 'c';
    /**
     * geeft recht om modules aan te passen.
     */
    public static final char MODIFY_MODULES_RIGHT = 'm';

    /**
     * geeft recht om leerlingen in klassen te zetten. Een docentrecht.
     */
	public static final char CHANGE_CLASS_RIGHT_TEACHER = 'C';
    
    
    private String rights = "";
    
    /**
     * Creates a new User object.
     *  
     */
    public User() {

    }

    /**
     * Returns the fullname of the user.
     * 
     * @return The fullname of the user.
     *  
     */
    public String getName() {
        if ((middleName == null) || middleName.equals("")) {
            return firstname + " " + lastName;
        } else {
            return firstname + " " + middleName + " " + lastName;
        }
    }
    
    public String getStudentName() {
    	// in Scorm 1.2 format
        if ((middleName == null) || middleName.trim().equals("")) {
            return lastName.trim() + ", " + firstname.trim();
        } else {
            return middleName.trim() + " " + lastName.trim() + ", " + firstname.trim();
        }
    	
    }
    

    /**
     * Returns the unique-identifier for the UserGroup object.
     * 
     * @return The unique-identifier for the UserGroup object.
     */
    public int getID() {
        return userID;
    }

    /**
     * Returns the e-mail address of the user.
     * 
     * @return The e-mail address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the e-mail address of the user.
     * 
     * @param email The new e-mail address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the firstname of the user.
     * 
     * @return The firstname of the user.
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the firstname of the user
     * 
     * @param firstname The firstname of the user.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Returns the current class of the user.
     * 
     * @return The current class of the user.
     */
    public SchoolClass getInClass() {
        return inClass;
    }

    /**
     * Sets the schoolclass of the user.
     * 
     * @param inClass The schoolclass of the user.
     */
    public void setInClass(SchoolClass inClass) {
        this.inClass = inClass;
    }

    /**
     * Returns the lastname of the user.
     * 
     * @return The lastname of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastname of the user.
     * 
     * @param lastName The lastname of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the middlename of the user.
     * 
     * @return The middlename of the user.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middlename of the user.
     * 
     * @param middleName The new middlename of the user.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Returns the school of the user.
     * 
     * @return The school of the user.
     */
    public School getSchool() {
        return school;
    }

    /**
     * Sets the school for the user.
     * 
     * @param school The new school for the user.
     */
    public void setSchool(School school) {
        this.school = school;
    }

    /**
     * Returns the unique-identifier of the user.
     * 
     * @return The unique-identifier of the user.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the unique-identifier of the user.
     * 
     * @param userID The unique-identifier of the user.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Returns the username of the user.
     * 
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Indicates if this is the deepest UserGroup.
     * 
     * @return If this is the deepest UserGroup it returns true. Otherwise it
     *         returns false.
     * @see fi.dwo.client.domain.UserGroup#isDeepestLevel()
     */
    public boolean isDeepestLevel() {
        return true;
    }

    /**
     * Indicates if this is the highest UserGroup.
     * 
     * @return If this is the highest UserGroup it returns true. Otherwise it
     *         returns false.
     * @see fi.dwo.client.domain.UserGroup#isHighestLevel()
     */
    public boolean isHighestLevel() {
        return false;
    }

    /**
     * Returns a title representing the UserGroup object.
     * 
     * @return A title representing the UserGroup object.
     * @see fi.dwo.client.domain.UserGroup#getTitle()
     */
    public String getTitle() {
        String[] arguments = new String[1];
        if (inClass != null) {
            arguments[0] = inClass.getName();
        } else {
            arguments[0] = "";
        }
        return TextMapper.format((TextMapper.UG_STUDENTS_OF_CLASS), arguments);
    }

    /**
     * Returns the name to order the usergroup. In this case, it returns the
     * lastName of the user.
     * 
     * @return The name to order the usergroup.
     * @see fi.dwo.client.domain.UserGroup#getOrderName()
     */
    public String getOrderName() {
        return lastName.toLowerCase();
    }

    /**
     * Returns a typename representing the User.
     * @return A typename representing the User.
     * @see fi.dwo.client.domain.UserGroup#getType()
     */
    public String getType() {
        return TextMapper.getText(TextMapper.UG_USER_TITLE);
    }

    /**
     * Returns a title represents the parent item.
     * @return A title represents the parent item.
     * @see fi.dwo.client.domain.UserGroup#getParentTitle()
     */
    public String getParentTitle() {
    return TextMapper.getText(TextMapper.UG_USER_PARENT);
    }

    /**
     * Returns a title represents the child item.
     * @return A title represents the child item.
     * @see fi.dwo.client.domain.UserGroup#getChildTitle()
     */
    public String getChildTitle() {
        return "";
    }

    /**
     * Returns a title represents the Ascending Order item.
     * @return A title represents the Ascending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderAscTitle()
     */
    public String getOrderAscTitle() {
        return TextMapper.getText(TextMapper.UG_USER_ORDER_ASC);
    }

    /**
     * Returns a title represents the Descending Order item.
     * @return A title represents the Descending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderDescTitle()
     */
    public String getOrderDescTitle() {
        return TextMapper.getText(TextMapper.UG_USER_ORDER_DESC);
    }
// single value, for once and for all.
    private boolean _logout = true;
    private boolean _readonly = false;

	private static User currentUser;
/**
 * Kan deze gebruiker wel uitloggen?
 * @return logout flag
 */
    
    public boolean canLogout()
    {
        return _logout;
    }
    
    void setLogout(boolean logout)
    {
        _logout = logout;
    }
    
    /**
     * Bepaal of van deze gebruiker de inloggegevens mogen worden gewijzigd.
     * @param readonly zet readonly on/off
     */
    void setReadonly(boolean readonly)
    {
        _readonly = readonly;
        
    }
    
    /**
     * Is deze gebruiker readonly. Kan deze gebruiker zijn profiel wijzigen?
     * @return readonly.
     */
    public boolean isReadonly()
    {
        return _readonly;
    }
    

	/**
	 * @return the rights
	 */
	public String getRights() {
		return rights;
	}

	/**
	 * @param rights the rights to set
	 */
	public void setRights(String rights) {
		if(rights == null) rights = "";
		this.rights = rights;
	}
    
	/** Een gast/leerling mag niets.
	 *  Docenten mogen meer.
	 * @see fi.dwo.client.domain.Teacher#hasRight(char)
	 */
	public boolean hasRight(char right) {
		switch(right) {
		case CHANGE_CLASS_RIGHT: return rights.indexOf(CHANGE_CLASS_RIGHT)>=0;
		}
		return false;
	}

	/**
	 * Compare 2 users
	 * @param o1 user 1
	 * @param o2 user 2
	 * @return -1/0/+1
	 */
	public int compareTo(Object o2) {
		User u2 = (User)o2;
		int r;
		r = getLastName().compareToIgnoreCase(u2.getLastName());
		if(r == 0)
		r = getName().compareToIgnoreCase(u2.getName());
		if(r == 0)
			r = getUsername().compareToIgnoreCase(u2.getUsername());
		return r;
	}

	public boolean hasIconizer() {
		if(inClass != null)
			return inClass.hasIconizer();
		return DEFAULT_ICONIZER;
	}

	public void addRight(char right) {
		if(hasRight(right))
			return;
    	String  id;
    	id = "[" + ((DwoIF)DwoHelper.getApplet()).getDwoProfile().getID() + "]";
    	
    	String rights = getRights();
    	int index = rights.indexOf(id);
    	if(index < 0)
    	{
    		id = "[]";
    		index = rights.indexOf(id);
    		if(index < 0)
    		{
    			//return false;
    			id=""; 
    			index = 0;
    		}
    	}
    	int end = rights.indexOf('[', index + id.length());
    	if(end < 0) end = rights.length();
    	rights = rights.substring(0,end) + right + rights.substring(end);
    	setRights(rights);
	}

	public static void setCurrentUser(User currentUser) {
		User.currentUser = currentUser;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	/**
	 * @return the lastLogin
	 */
	public long getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
		timeZone = lastLogin-System.currentTimeMillis();
	}

	/**
	 * @return the timeZone
	 */
	public long getTimeZone() {
		return timeZone;
	}

	public String toString() {
		return getName();
	}
}