package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_2004_API implements Scorm2004IF {

	private native static void script(String item) /*-{
		$wnd.script(item)	
	}-*/;
	
	static {
		script("scripts/SCORM_2004_APIWrapper.js"); 
	}

	
	public native String Initialize() /*-{
		return $wnd.doInitialize()
	}-*/;
	
	private native String Terminate0() /*-{
		return $wnd.doTerminate()
	}-*/;
	public Promise<String> Terminate() {
		return Promises.resolved(Terminate0());
	}
	
	public native String GetValue(String name) /*-{
		return $wnd.doGetValue(name)
	}-*/;

	public native String SetValue(String name, String value) /*-{
		return $wnd.doSetValue(name, value)
	}-*/;

	public native String GetLastError() /*-{
		return $wnd.doGetLastError()
	}-*/;

	private native String Commit0() /*-{
		return $wnd.doCommit()
	}-*/;

	public Promise<String> Commit() {
		return Promises.resolved(Commit0());
	}
	
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

	@Override
	public void setScoID(String unitId) {
		// TODO Auto-generated method stub
		
	}


}
