package nl.uu.fi.dwo.mobile.client.sco;

public class SCORM_2004_API implements Scorm2004IF {

	public native String Initialize() /*-{
		return $wnd.doInitialize()
	}-*/;
	
	public native String Terminate() /*-{
		return $wnd.doTerminate()
	}-*/;
	
	public native String GetValue(String name) /*-{
		return $wnd.doGetValue(name)
	}-*/;

	public native String SetValue(String name, String value) /*-{
		return $wnd.doSetValue(name, value)
	}-*/;

	public native String GetLastError() /*-{
		return $wnd.doGetLastError()
	}-*/;

	public native String Commit() /*-{
		return $wnd.doCommit()
	}-*/;
}
