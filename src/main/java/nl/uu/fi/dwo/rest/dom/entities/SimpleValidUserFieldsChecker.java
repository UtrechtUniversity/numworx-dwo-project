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
	}
	
// GWT compatible, zonder java.util.regex 
	
	public static boolean isValidEmail(String email) {
/*
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
 */
		if (email == null || email.length() < 3) return false; // minumum a@b
		int alpha = email.indexOf('@');
		if (alpha <= 0 || alpha >= email.length()-1) return false;
		String local = email.substring(0, alpha);
		String domain = email.substring(alpha+1);
		for(char c : domain.toCharArray()) {
			boolean ok = inrange(c, 'a','z') || inrange(c, 'A', 'Z') || inrange(c, '0', '9') || c == '-' || c == '.';
			if(!ok) return false;
		}
		for(char c: local.toCharArray()) {
			boolean ok = 
					c == '!' ||
					inrange(c, '#' , '\'') ||
					c == '*' ||
					c == '+' ||
					inrange(c, '-', '9') ||
					c == '=' ||					
					inrange(c, '?', 'Z') ||
					inrange(c, '^', '~');
			if (!ok)
				return false;
		}
		return true;
	}

	/**
	 * In range b-e inclusief
	 * @param c char
	 * @param b start
	 * @param e end
	 * @return boolean
	 */
	private static boolean inrange(char c, char b, char e ) {
		return b <= c && c <= e;
	}
/** GWT compatible, zonder java.util.regex 
 *  alleen alfanumeriek, - _ en .
 * @param username
 * @return
 */
	public static boolean isValidUserName(String username) {
//       return username.matches("[A-Za-z0-9_.-]+");
      if (username == null || username.length() < 2) { // minimum lengte 2
          return false;
      }
      char[] chars = username.toCharArray();
      for (char c: chars) {
// valid character?
          boolean ok = inrange(c, 'a','z') || inrange(c, 'A', 'Z') || inrange(c, '0', '9') || c == '-' || c == '.' || c == '_' ;
          if (!ok) {
              return false;
          }
      }
      return true;
	}

}
