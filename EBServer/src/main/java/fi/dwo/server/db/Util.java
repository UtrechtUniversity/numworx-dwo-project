package fi.dwo.server.db;

import java.util.regex.Pattern;

public class Util {
	public static boolean illegal(String q) {
		if (q == null) return true;
		if (! Pattern.matches("[%#:a-zA-Z0-9/=&:.]*", q))
			return true;
		return false;
	}

}
