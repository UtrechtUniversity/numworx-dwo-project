package nl.uu.fi.dwo.mobile.client.sco;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.Role;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TinCanAPI extends SCORM_guest implements Scorm2004IF {
	private static final Logger LOG = Logger.getLogger("TinCanAPI");
	private AsyncCallback<Void> callback;
	private String moduleData = "";
	private String scoreScaled = "0.0";
	private boolean completion = false;
	private long    startTime;
	private String duration;
	/**
	 * @return the completion
	 */
	public boolean isCompletion() {
		return completion;
	}

	/**
	 * @param completion the completion to set
	 */
	public void setCompletion(boolean completion) {
		this.completion = completion;
	}

	/**
	 * @param moduleData the moduleData to set
	 */
	public void setModuleData(String moduleData) {
		this.moduleData = moduleData;
	}

	/**
	 * @return the scoreScaled
	 */
	public String getScoreScaled() {
		return scoreScaled;
	}
	/**
	 * @param scoreScaled the scoreScaled to set
	 */
	public void setScoreScaled(String scoreScaled) {
		this.scoreScaled = scoreScaled;
	}

	
	@Override
	public String Commit() {
		
		return super.Commit();
	}

	@Override
	public String GetValue(String name) {
		if(Memento.SUSPEND_DATA.equals(name))
			try {
				return getModuleData();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "getValue " + name, e);
			}
		return super.GetValue(name);
	}

	private String getModuleData() {	
		return moduleData;
	}

	@Override
	public String GetLastError() {
		return super.GetLastError();
	}

	@Override
	public String SetValue(String name, String value) {
		try {
			if(Memento.SCORE_RAW.equals(name))
				setScoreScaled(toScore(value));
			else if(Memento.SUSPEND_DATA.equals(name))
				setModuleData(value);
			else if(Memento.EXIT_STATUS.equals(name))
			{
				final boolean equals = Memento.EXIT_NORMAL.equals(value);
				LOG.info("setCompleted(" + value  + " ) " + equals);
				if(equals)
					setCompletion(equals);
			}
			else
				return "false";
		} catch (Exception e) {
			LOG.severe("setValue " + name + " :" + e);
			return "false";
		}
		return "true";
	}

	@Override
	public String Terminate() {
		Commit();
		return super.Terminate();
	}

	private static native void sendAnsweredStatement() /*-{
		$wnd.sendAnsweredStatement()
	}-*/; 

	private static native void sendModuleDataStatement() /*-{
		$wnd.sendModuleDataStatement()
	}-*/; 

	private static native void sendAnswerAndModuleDataStatements() /*-{
		$wnd.sendAnswerAndModuleDataStatements()
	}-*/; 
	
// Dit kan in GWT zelf?	
	private static native void Initialize0(TinCanAPI api) /*-{
		$wnd.xapi = function (msg) { 
			api.@nl.uu.fi.dwo.mobile.client.sco.TinCanAPI::onMessage(Lcom/google/gwt/core/client/JavaScriptObject;)(msg)
		} 
		$wnd.sendModuleDataRequest(); 
		
	}-*/;
	
	static class Statement extends JavaScriptObject {
		String getJson() {
			return "";
		}

		native String getVerb() /*-{
			return this.verb.id;
		}-*/;
	}
	
	
	void onMessage(JavaScriptObject obj) {
		Statement s = obj.cast();
		setModuleData(s.getJson());
		startTime = System.currentTimeMillis();
		callback.onSuccess(null);callback = null;
	}
	
	@Override
	public String Initialize() {
		return super.Initialize();
	}

	@Override
	public void Initialize(AsyncCallback<Void> callback) {
		this.callback = callback;
		Initialize0(this);
	}

	@Override
	public Role getRole() {
		return super.getRole();
	}

	private String toScore(String value) {
		double raw = Double.parseDouble(value);
		return String.valueOf(raw/100.0);
	}

	private String format(long millis)
	{
		return "PT" + (millis / 1000.0F) + "S";
	}

	private String getDuration() {
		return duration = format(System.currentTimeMillis() - startTime);
	}
	
}
