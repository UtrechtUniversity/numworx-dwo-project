package nl.uu.fi.dwo.mobile.client.sco;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.Role;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class AssetAPI implements Scorm2004IF {
	private static Logger logger = Logger.getLogger("AssetAPI");

	private static final String DEFAULT_GUID = "017ec54cf2ee4dc5-840256df4139d38a";
	private String guid;
	
	private native String SetInitialize(String GUID, boolean Initialized) /*-{
		return $wnd.SetInitialized(GUID, Initialized)
	}-*/;

	private native String SetCompleted(String GUID, boolean Completed) /*-{
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
	
	private String lastScore = "";
	public AssetAPI() {
		guid = Window.Location.getParameter("guid");
		if(guid == null) guid = DEFAULT_GUID;
	}

	@Override
	public String Commit() {
		SetScore(guid,lastScore); // force commit in Noordhoff software.
		return "";
	}

	@Override
	public String GetValue(String name) {
		if(Memento.SUSPEND_DATA.equals(name))
			try {
				return GetAssetData(guid);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "getValue " + e);
			}
		return "";
	}

	@Override
	public String GetLastError() {
		return "";
	}

	@Override
	public String SetValue(String name, String value) {
		try {
			if(Memento.SCORE_RAW.equals(name))
				SetScore(guid, lastScore = toScore(value));
			else if(Memento.SUSPEND_DATA.equals(name))
				SetAssetData(guid, value);
//			else if(Memento.EXIT_STATUS.equals(name))
//			{
//				final boolean equals = Memento.EXIT_NORMAL.equals(value);
//				logger.info("setCompleted(" + value  + " ) " + equals);
//				if(equals)
//					SetCompleted(guid, equals);
//			} 
			else if ( Memento.COMPLETION_STATUS.equals(name)) {
				boolean equals = Memento.COMPLETED.equals(value);
				logger.info("setCompleted(" + value  + " ) " + equals);
				SetCompleted(guid, equals);
			}
			else
				return "false";
		} catch (Exception e) {
			logger.severe("setValue " + name + " :" + e);
			return "false";
		}
		return "true";
	}

	private String toScore(String value) {
		double raw = Double.parseDouble(value);
		return String.valueOf(raw/100.0);
	}

	@Override
	public String Terminate() {
		Commit();
		return "";
	}

	@Override
	public String Initialize() {
		try {
			SetInitialize(guid, true);
		} catch (Exception e) {
			logger.severe("Initialize:"+e);
		}
		return "";
	}
	
	public void Initialize(final AsyncCallback<Void> callback) { if(callback!=null) callback.onSuccess(null); }

	@Override
	public Role getRole() {
		return Role.Learner;
	}
}
