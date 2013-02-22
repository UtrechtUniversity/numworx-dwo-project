package nl.uu.fi.dwo.mobile.client.sco;

public class SCORM_12_API implements Scorm2004IF {

	public native String Commit() /*-{
		return $wnd.doLMSCommit()
	}-*/;

	public native String GetLastError() /*-{
		return $wnd.doLMSGetLastError()
	}-*/;

	public native String GetValue(String name) /*-{
		return $wnd.doLMSGetValue(name)
	}-*/;

	public native String SetValue(String name, String value)/*-{
		return $wnd.doSetValue(name, value)
	}-*/;

	@Override
	public native String Initialize() /*-{
		return $wnd.doLMSInitialize()
	}-*/;

	@Override
	public native String Terminate() /*-{
		return $wnd.doLMSFinish()
	}-*/;
}
