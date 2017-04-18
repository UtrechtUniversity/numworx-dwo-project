package nl.uu.fi.dwo.rest.dom.entities;

public class SimpleValidUserFieldsChecker {

	public static boolean isValidPassword(String password) {
	    if (password == null) {
	        return false;
	    }
	    if(password.startsWith(" ")|| password.endsWith(" ")){
	        return false;
	    }
	    if (password.length()<5) {
	        return false;
	    }
	    if (password.length() >= 128) {
	        return false;
	    }
	    return true;
	}

	/**
	 * Verify required fields are filled.
	 *
	 * @param fields
	 * @return
	 */
	public static boolean isEmptyOrNull(String... fields) {
	    for (int i = 0; i < fields.length; i++) {
	        String field = fields[i];
	        if (field == null || field.trim().isEmpty()) {
	            return false;
	        }
	    }
	    return true;
	}

	SimpleValidUserFieldsChecker() {
		// TODO Auto-generated constructor stub
	}
	
// GWT compatible, zonder java.util.regex 
	
	public static boolean isValidEmail(String newEmail) {
		// TODO Auto-generated method stub
		return true;
	}

// GWT compatible, zonder java.util.regex 
	public static boolean isValidUserName(String username) {
		return true;
	}

}
