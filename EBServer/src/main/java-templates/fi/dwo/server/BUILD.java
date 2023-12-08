package fi.dwo.server;

public class BUILD {
	public static String buildNumber = "${buildNumber}";
	public static String version;
    public static String timeStamp = "${timestamp}";
    public static String javaClient = "${DWOJClientVersion}";
    public static String htmlClient = "${DWOGwtClientVersion}";

    static {
    	version = "${project.version}";
    	javaClient = fi.dwo.dwojapplet.BUILD.version;
    	htmlClient = nl.uu.fi.dwo.lms.gwtclient.gwt.BUILD.version;
    }
}
