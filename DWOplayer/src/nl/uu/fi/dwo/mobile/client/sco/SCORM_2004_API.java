package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import java.util.Collection;
import java.util.Map;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.ScriptInjector.FromUrl;
import com.google.gwt.user.client.rpc.AsyncCallback;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;

public class SCORM_2004_API implements Scorm2004IF {

	private static Promise<?> loaded;
	
	private static native String getBase() /*-{
		return $wnd.deploy;
	}-*/;
	
	private static Promise<?> script(String source) {
		source = getBase()+source;
		Deferred<Void> defer = new Deferred<>();
		FromUrl fromUrl = ScriptInjector.fromUrl(source);
		fromUrl.setCallback(new Callback<Void, Exception>() {
			
			@Override
			public void onSuccess(Void result) {
				defer.resolve(result);
			}
			
			@Override
			public void onFailure(Exception reason) {
				defer.fail(reason);
			}
		});
		fromUrl.setWindow(ScriptInjector.TOP_WINDOW);
		fromUrl.inject();
		return defer.getPromise();
	}
	
	static {
		loaded = Promises.resolved(null);
		//script("scripts/SCORM_2004_APIWrapper.js"); 
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
	      return loaded.map(p -> Initialize0());
	    } catch (Exception e) {
	      return Promises.failed(e);
	    }
	  }

	@Override
	public String getAuthorization() {
		String authorization = GetValue("dme.authorization");
		if (authorization.isEmpty()) return "None";
		return authorization;
	}

	@Override
	public String getRefreshToken() {
		String authorization = GetValue("dme.refresh-token");
		if (authorization.isEmpty()) return null;
		return authorization;
	}

	
	private static native boolean hasGetValueAsync() /*-{
		return typeof $wnd.getAPIHandle().GetValueAsync !== 'undefined';
	}-*/;
	
	private static native void getValueAsync(String name, CallResolve callback) /*-{
		$wnd.getAPIHandle().GetValueAsync(name, 
			{ "resolve" : function(value) {
				callback.@nl.uu.fi.dwo.mobile.client.sco.SCORM_2004_API.CallResolve::resolve(Ljava/lang/String;)(value)
			}})
	}-*/;
	
	@FunctionalInterface interface CallResolve {
		void resolve(String value);
	}
	
	
	@Override
	public Promise<String> getValuePromise(String name) {
		if (hasGetValueAsync()) {
			Deferred<String> d = new Deferred<>();
			CallResolve callback = d::resolve;
			getValueAsync(name, callback);
			return d.getPromise();
		}
		return Scorm2004IF.super.getValuePromise(name);
	}

	@Override
	public Promise<Map<String, String>> getValuesPromise(Collection<String> names) {
		// TODO Auto-generated method stub
		return Scorm2004IF.super.getValuesPromise(names);
	}


}
