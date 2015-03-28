/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.sco;

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
		return $wnd.CES.getResponse();
	}-*/;
	
	/**
	 * 
	 */
	public TriforkAPI() {
	}

	@Override
	public String Commit() {
		return "";
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
	public String Terminate() {
		return "";
	}

	@Override
	public String Initialize() {
		return "";
	}

	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }
}
