package fi.dwo.dwojapplet;

public interface BUILD {
	String buildNumber = "${buildNumber}";
	String version = "${project.version}";
        String timeStamp = "${timestamp}";
}
