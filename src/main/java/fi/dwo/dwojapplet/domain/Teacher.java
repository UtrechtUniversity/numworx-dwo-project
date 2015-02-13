// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Teacher.java

package fi.dwo.dwojapplet.domain;

import java.util.Arrays;

import fi.dwo.dwojapplet.gui.GuiCreator;

/**
 * This class is responsible for the Teacher data. It extends <code>User</code>, 
 * so the main functionality is definied in <code>User</code>.
 * @author M.J.B. Kupers
 *  
 */
public class Teacher extends User {
    private SchoolClass classList[];

    /**
     * Creates a new Teacher Object
     *  
     */
    public Teacher() {

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
     * Return a list of classes of the teacher.
     * 
     * @return An array of the classes of the teacher.
     *  
     */
    public SchoolClass[] getClasses() {
        return classList;
    }

    /**
     * Sets a list of classes of the teacher.
     * 
     * @param classList The list of classes of the teacher.
     */
    public void setClasses(SchoolClass[] classList) {
        this.classList = classList;
    }

    /**
     * Deletes a class from the list of classes.
     * 
     * @param schoolClass The class to delete.
     */
    public void deleteClass(SchoolClass schoolClass) {
    	if(classList.length == 0)
    		return;	// FIXME: komt voor dat classList.lenght = 0 bij te snel drukken?
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
     * Geeft het recht op: Scormexport, AppletExport, etc.
     * @param right
     * @return
     */
    public boolean hasRight(char right)
    {
    	String  id;
    	//id = "[" + GuiCreator.instance().getDWO().getDwoProfile().getID() + "]";
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
    	return rights.substring(index, end).indexOf(right)>=0;
    }

	public boolean hasIconizer() {
		return true;
	}

}