/**
 * 
 */
package nl.uu.fi.dwo.mobile.client.sco;

/**
 * @author peterboon
 *
 */
public class TriforkAPI implements Scorm2004IF {

	private native void setResponse(String data) /*-{
		CES.setResponse(data);
	}-*/;
	
	private native String getResponse() /*-{
		return CES.getResponse();
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
			setResponse(value);
		return "";
	}

	@Override
	public String Terminate() {
		return "";
	}

	@Override
	public String Initialize() {
		return "";
	}

}
