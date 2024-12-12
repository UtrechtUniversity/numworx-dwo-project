package fi.dwo.dwojapplet;

public interface BUILD {
// runtime, not compile time
	String buildNumber = new String("${buildNumber}");
	String version = new String("${project.version}");
    String timeStamp = new String("${osgi.qualifier}");
}
