/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.sco;

import nl.uu.fi.dwo.interaction.client.Role;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author peterboon
 *
 */
public class TriforkAPI implements Scorm2004IF {

	private native void setResponse(String data) /*-{
		$wnd.CES.setResponse(data);
	}-*/;
	
	private native String getResponse() /*-{
		var s =  $wnd.CES.getResponse();
		return s;
	}-*/;
	
	native void setJSResponse(JavaScriptObject obj) /*-{
		$wnd.CES.setResponse(obj)
	}-*/;
	
	native JavaScriptObject getJSResponse() /*-{
		var s = $wnd.CES.getResponse();
		return s;
	}-*/;
	
	/**
	 * 
	 */
	public TriforkAPI() {
	}

	@Override
	public Promise<String> Commit() {
		return Promises.resolved("");
	}

	@Override
	public String GetValue(String name) {
		if(Memento.SUSPEND_DATA.equals(name))
			return getResponse();
		return "";
	}

	@Override
	public String GetLastError() {
		return "";
	}

	@Override
	public String SetValue(String name, String value) {
		if(Memento.SUSPEND_DATA.equals(name))
		{
			setResponse(value);
			return "true";
		}
		return "false";
	}

	@Override
	public Promise<String> Terminate() {
		return Promises.resolved("");
	}

	@Override
	public Promise<String> Initialize() {
      return Promises.resolved("");
	}

	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }

	@Override
	public Role getRole() {
		return Role.Learner;
	}

	@Override
	public void setScoID(String unitId) {
		// TODO Auto-generated method stub
		
	}
}
