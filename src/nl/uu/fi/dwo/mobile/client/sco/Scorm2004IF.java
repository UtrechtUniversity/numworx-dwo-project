package nl.uu.fi.dwo.mobile.client.sco;

public interface Scorm2004IF {

	String Commit();

	String GetValue(String name);

	String GetLastError();

	String SetValue(String name, String value);

	String Terminate();
	
	String Initialize();

}
