package nl.uu.fi.dwo.mobile.client.sco;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class AssetAPI implements Scorm2004IF {

	private static final String DEFAULT_GUID = "017ec54cf2ee4dc5-840256df4139d38a";
	private String guid;
	
	private native String SetInitialize(String GUID, Boolean Initialized) /*-{
		return $wnd.SetInitialized(GUID, Initialized)
	}-*/;

	private native String SetCompleted(String GUID, Boolean Completed) /*-{
	return $wnd.SetCompleted(GUID, Completed)
	}-*/;

	private native String SetScore(String GUID, String Score) /*-{
		return $wnd.SetScore(GUID, Score)
	}-*/;
	
	private native String SetAssetData(String GUID, String Data) /*-{
		return $wnd.SetAssetData(GUID, Data)
	}-*/;
	
	private native String GetAssetData(String GUID) /*-{
		return $wnd.GetAssetData(GUID)
	}-*/;
	
	
	public AssetAPI() {
		guid = Window.Location.getParameter("guid");
		if(guid == null) guid = DEFAULT_GUID;
	}

	@Override
	public String Commit() {
		return "";
	}

	@Override
	public String GetValue(String name) {
		if(Memento.SUSPEND_DATA.equals(name))
			return GetAssetData(guid);
		return "";
	}

	@Override
	public String GetLastError() {
		return "";
	}

	@Override
	public String SetValue(String name, String value) {
		if(Memento.SCORE_RAW.equals(name))
			SetScore(guid, toScore(value));
		else if(Memento.SUSPEND_DATA.equals(name))
			SetAssetData(guid, value);
		else if(Memento.COMPLETION_STATUS.equals(name))
			SetCompleted(guid, Memento.COMPLETE.equals(value));
		return "";
	}

	private String toScore(String value) {
		double raw = Double.parseDouble(value);
		return String.valueOf(raw/100.0);
	}

	@Override
	public String Terminate() {
		return "";
	}

	@Override
	public String Initialize() {
		SetInitialize(guid, Boolean.TRUE);
		return "";
	}
	
	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }
}
