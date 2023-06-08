package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_12_API implements Scorm2004IF {

	private native String Commit0() /*-{
		return $wnd.doLMSCommit()
	}-*/;

	public Promise<String> Commit() {
		return Promises.resolved(Commit0());
	}
	
	public native String GetLastError() /*-{
		return $wnd.doLMSGetLastError()
	}-*/;

	public native String GetValue(String name) /*-{
		return $wnd.doLMSGetValue(name)
	}-*/;

	public native String SetValue(String name, String value)/*-{
		return $wnd.doLMSSetValue(name, value)
	}-*/;

	public native String Initialize0() /*-{
		return $wnd.doLMSInitialize()
	}-*/;

	@Override
	public Promise<String> Terminate() {
		return Promises.resolved(Terminate0());
	}
	
	private native String Terminate0() /*-{
		return $wnd.doLMSFinish()
	}-*/;
	
	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }

	@Override
	public Role getRole() {
		try {
			String dworole = GetValue("USER_GROUP");
			if("UG_TEACHER".equals(dworole))
				return Role.Instructor;
		} catch(Exception oops) {}
		
		return Role.Learner;
	}

	@Override
	public void setScoID(String unitId) {
		// TODO Auto-generated method stub
		
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
