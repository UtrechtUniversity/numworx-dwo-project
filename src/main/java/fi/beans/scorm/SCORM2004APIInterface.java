package fi.beans.scorm;

public interface SCORM2004APIInterface {
	String Initialize(String dummy);
	String Commit(String dummy);
	String Terminate(String dummy);
	
	String GetValue(String key);
	String SetValue(String key, String value);
	
	String GetLastError();
	String GetDiagnostic(String iErrorCode);
	String GetErrorString(String iErrorCode);
}
