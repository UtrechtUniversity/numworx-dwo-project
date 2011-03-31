// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Admin.java

package fi.dwo.client.domain;

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
	public boolean hasRight(char right) {
		return true;
	}

	public boolean hasIconizer() {
		return true;
	}

    

}