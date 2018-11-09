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
	
	private native String Initialize0() /*-{
		return $wnd.doInitialize()
	}-*/;
	
	private native String Terminate0() /*-{
		return $wnd.doTerminate()
	}-*/;

  public Promise<String> Terminate() {
    try {
      return Promises.resolved(Terminate0());
    } catch (Exception e) {
      return Promises.failed(e);
    }
  }
	
	private native String GetValue0(String name) /*-{
		return $wnd.doGetValue(name)
	}-*/;

	public String GetValue(String name) {
	  try {
	    return GetValue0(name);
	  } catch(Exception e) {
	    return "";
	  }
	}
	
	
	private native String SetValue0(String name, String value) /*-{
		return $wnd.doSetValue(name, value)
	}-*/;
	
	public String SetValue(String name, String value) {
	  try { 
	    return SetValue0(name, value);
	  } catch(Exception e) {
	    return "false";
	  }
	}

	public native String GetLastError() /*-{
		return $wnd.doGetLastError()
	}-*/;

	private native String Commit0() /*-{
		return $wnd.doCommit()
	}-*/;

  public Promise<String> Commit() {
    try {
      return Promises.resolved(Commit0());
    } catch (Exception e) {
      return Promises.failed(e);
    }
  }
	
	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }

	@Override
	public Role getRole() {
		try {
			String dworole = GetValue("USER_GROUP");
			if("UG_TEACHER".equals(dworole))
				return Role.Instructor;
		} catch(Exception unused) {}
		
		return Role.Learner;
	}

	@Override
	public void setScoID(String unitId) {
	}

	@Override
	  public Promise<String> Initialize() {   
	    try {
	      return Promises.resolved(Initialize0());
	    } catch (Exception e) {
	      return Promises.failed(e);
	    }
	  }


}
