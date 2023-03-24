package nl.uu.fi.dwo.lms.gwtclient.gwt;

public interface BUILD {
	String buildNumber = "${buildNumber}";
	String version = "${project.version}";
        String timeStamp = "${timestamp}";
}
