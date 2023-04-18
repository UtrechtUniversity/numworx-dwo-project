package nl.numworx.oauth2client.server;

import javax.servlet.http.HttpServletRequest;

public class Origin {

	public static final String ALLOW_ORIGIN = System.getProperty("ALLOW_ORIGIN", "*");
	public static final String[] ORIGINS = ALLOW_ORIGIN.split("\\s+");
	
	public static String of(HttpServletRequest request) {
		String origin = request.getHeader("Origin");
		for (String allow : ORIGINS) {
			if (allow.equals(origin)|| "*".equals(allow)) return allow;
		}
		return ORIGINS[0];
	}

	public static String of(String origin) {
		for (String allow : ORIGINS) {
			if (origin.startsWith(allow)|| "*".equals(allow)) return allow;
		}
		return ORIGINS[0];
	}
}
