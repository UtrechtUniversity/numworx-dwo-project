package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
		return $wnd.doLMSSetValue(name, value)
	}-*/;

	@Override
	public native String Initialize() /*-{
		return $wnd.doLMSInitialize()
	}-*/;

	@Override
	public native String Terminate() /*-{
		return $wnd.doLMSFinish()
	}-*/;
	
	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }

	@Override
	public Role getRole() {
		try {
			String dworole = GetValue("USER_GROUP");
			if("UG_TEACHER".equals(dworole))
				return Role.Instructor;
		} catch(Exception _) {}
		
		return Role.Learner;
	}

	


}
