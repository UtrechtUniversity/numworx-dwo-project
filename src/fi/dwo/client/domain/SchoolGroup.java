// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Group.java

package fi.dwo.client.domain;

/**
 * This class is responsible for the Group data.<br>
 * A group is the possible usergroup of the user (e.g. Teacher or Student).<br>
 * This is only used when at the registration-part of the DWO.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class SchoolGroup {
	
	public static final int STUDENT = 1;
	public static final int TEACHER = 2;
	public static final int ADMIN = 3;
	public static final int DIGICODE = 4;
	public static final int SCHOOLADMIN = 5;
	public static final int LENGTH = 6; // for arrays
	
    private int schoolGroupID;

    private int schoolID;
    
    private int groupID;
    
    private String passwd;

    /**
     * Creates a new instance of a Group Object.
     *  
     */
    public SchoolGroup() {

    }

    /**
     * Returns the current GroupID
     * 
     * @return The groupID.
     */
    public int getGroupID() {
        return groupID;
    }

    /**
     * Sets the groupID
     * 
     * @param groupID The groupID to set.
     */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
    
    /**
     * Returns the current schoolGroupID
     * 
     * @return The schoolGroupID.
     */
    public int getSchoolGroupID() {
        return schoolGroupID;
    }

    /**
     * Sets the schoolGroupID
     * 
     * @param groupID The schoolGroupID to set.
     */
    public void setSchoolGroupID(int schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }
    
    /**
     * Returns the current schoolID
     * 
     * @return The schoolID.
     */
    public int getSchoolID() {
        return schoolID;
    }

    /**
     * Sets the schoolID
     * 
     * @param groupID The schoolID to set.
     */
    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    /**
     * Returns the group-passwd.
     * 
     * @return The group-passwd.
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * Sets the group-passwd
     * 
     * @param name The group-passwd to set.
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}