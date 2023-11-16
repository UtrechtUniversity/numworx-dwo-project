package fi.dwo.server;

public class BUILD {
	public static String buildNumber = "${buildNumber}";
	public static String version;
    public static String timeStamp = "${timestamp}";
    public static String javaClient = "${DWOClientVersion}";
    public static String htmlClient = "${DWOGwtClientVersion}";

    static {
    	version = "${project.version}";
    }
}
