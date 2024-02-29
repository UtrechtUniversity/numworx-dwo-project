package fi.dwo.server;

public class BUILD {
	public static String buildNumber = "${buildNumber}";
	public static String version;
    public static String timeStamp = "${timestamp}";
    public static String javaClient = "${project.version}";
    public static String htmlClient = "${project.version}";

    static {
    	version = "${project.version}";
    }
}
