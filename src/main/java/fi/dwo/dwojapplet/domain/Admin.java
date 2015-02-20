package fi.dwo.dwojapplet.domain;

/**
 * This class is responsible for the Admin data. It extends <code>User</code>, 
 * so the main functionality is definied in <code>User</code>.
 * @author M.J.B. Kupers
 *  
 */
public class Admin extends User {
   
    /**
     * Creates a new Admin Object
     *  
     */
    public Admin() {

    }

	/** Een admin mag alles.
	 * @see fi.dwo.client.domain.User#hasRight(char)
	 */
    @Override
	public boolean hasRight(char right) {
		return true;
	}

    @Override
	public boolean hasIconizer() {
		return true;
	}

    

}