// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\School.java

package fi.dwo.client.domain;

import java.util.Arrays;

/**
 * This class is responsible for the School data.
 * @author M.J.B. Kupers
 *  
 */
public class School {
    private int schoolID;

    private String name;
    
    private String schoolLogin;

    private SchoolGroup[] schoolGroupList;

    private SchoolClass[] classList;
    
    private String image;
    
    private boolean export;
    private String rights = "";

    /**
     * Creates a new School object.
     *  
     */
    public School() {

    }

    public School(int i) {
		setSchoolID(i);
	}

	/**
     * Returns the name of the school.
     * 
     * @return The name of the school.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the school.
     * 
     * @param name The new name of the school.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the unique-identifier of the school.
     * 
     * @return The unique-identifier of the school.
     */
    public int getSchoolID() {
        return schoolID;
    }

    /**
     * Sets the unique-identifier of the school.
     * 
     * @param schoolID The unique-identifier of the school.
     */
    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }
    
    /**
     * Returns the unique-identifier of the school.
     * 
     * @return The unique-identifier of the school.
     */
    public String getSchoolLogin() {
        return schoolLogin;
    }

    /**
     * Sets the unique-identifier of the school.
     * 
     * @param schoolID The unique-identifier of the school.
     */
    public void setSchoolLogin(String schoolLogin) {
        this.schoolLogin = schoolLogin;
    }
    
    /**
     * Returns the list of schoolGroups specified for the school.
     * 
     * @return The list of schoolGroups specified for the school.
     */
    public SchoolGroup[] getSchoolGroupList() {
        return schoolGroupList;
    }

    /**
     * Sets the list of schoolGroups for the school.
     * 
     * @param schoolGroupList The list of schoolGroups for the school.
     */
    public void setSchoolGroupList(SchoolGroup[] schoolGroupList) {
        this.schoolGroupList = schoolGroupList;
    }
    
    public String getPasswd(int groupID) {
    	if(schoolGroupList != null)
    	{  	for(int i=0 ; i<schoolGroupList.length; i++) {
	    		if(schoolGroupList[i] != null && schoolGroupList[i].getGroupID()==groupID) {
	    			return schoolGroupList[i].getPasswd();
	    		}
	    	}
	    }
    	return null;
    }
    
    
    /**
     * Returns the list of classes specified for the school.
     * 
     * @return The list of classes specified for the school.
     */
    public SchoolClass[] getClassList() {
        return classList;
    }

    /**
     * Sets the list of classes for the school.
     * 
     * @param classList The list of classes for the school.
     */
    public void setClassList(SchoolClass[] classList) {
        this.classList = classList;
    }

    /**
     * Adds a class to the list of classes.
     * 
     * @param c The class to add.
     */
    public void addClass(SchoolClass c) {
        SchoolClass[] sc = new SchoolClass[classList.length + 1];
        for (int i = 0; i < classList.length; i++) {
            sc[i] = classList[i];
        }
        sc[sc.length - 1] = c;
        Arrays.sort(sc);
        classList = sc;
    }
    /**
     * Deletes a class from the list of classes.
     * 
     * @param schoolClass The class to delete.
     */
    public void deleteClass(SchoolClass schoolClass) {
// FIXME komt voor dat classList.length = 0
    	if(classList.length == 0)
    		return;
        SchoolClass[] sc = new SchoolClass[classList.length - 1];
        int difference = 0;
        for (int i = 0; i < classList.length; i++) {
            if (schoolClass == classList[i]) {
                difference++;
            } else {
                sc[i - difference] = classList[i];
            }
        }
        classList = sc;
    }

    /**
     * @return Returns the image.
     */
    public String getImage() {
        return image;
    }
    /**
     * @param image The image to set.
     */
    public void setImage(String image) {
        this.image = image;
    }

	/**
	 * @param export the export to set
	 */
	public void setExport(boolean export) {
		this.export = export;
	}

	/**
	 * @return the export
	 */
	public boolean isExport() {
		return export;
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
	
    public boolean hasRight(char right)
    {
    	return rights.indexOf(right)>=0;
    }

    public String toString() {
    	return getName();
    }
}