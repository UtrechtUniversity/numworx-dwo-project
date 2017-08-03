package nl.uu.fi.dwo.mobile.client.sco;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.Role;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TinCanAPI2 extends SCORM_guest implements Scorm2004IF {
	
	private native static void script(String item) /*-{
		$wnd.script(item)	
	}-*/;
	
	static {
		script("scripts/lz-string.js");
		script("scripts/tincan-1.0.1.js"); 
//		script("scripts/Content_Api-0.2.13.js"); overwrites tincan prototype
		script("scripts/xapi2.js");
	}
	
	private static final String MODULEDATA_RESPONSE = "http://bao.mijnklas.nl/xapi/activities/get-moduledata-response";
	private static final String NAVIGATE_VERB = "http://bao.mijnklas.nl/xapi/verbs/navigate";
	private static final String MODULEDATA_VERB = "http://bao.mijnklas.nl/xapi/verbs/moduleData";
	private static final Logger LOG = Logger.getLogger("TinCanAPI");
	private AsyncCallback<Void> callback;
	private String moduleData = "";
	private float scoreScaled = 0.0f;
	private boolean completion = false;
	private boolean success = false;
	private long    startTime  = System.currentTimeMillis();
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
		if (null == moduleData) moduleData = "";
		this.moduleData = moduleData;
	}

	/**
	 * @return the scoreScaled
	 */
	public float getScoreScaled() {
		return scoreScaled;
	}
	/**
	 * @param scoreScaled the scoreScaled to set
	 */
	public void setScoreScaled(double scoreScaled) {
		this.scoreScaled = (float) scoreScaled;
	}
	
	public void setScoreScaled(float scoreScaled) {
		this.scoreScaled = scoreScaled;
	}

	
	@Override
	public String Commit() {
		success = scoreScaled > 0.99f;
		sendAnswerAndModuleDataStatements(success, getDuration(), scoreScaled, completion, moduleData);
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
				LOG.info("setExitStatus(" + value  + " ) " + equals);
//				if(equals)
//					setCompletion(equals);
			}
			else if(Memento.COMPLETION_STATUS.equals(name))
			{
				boolean equals = Memento.COMPLETED.equals(value);
				LOG.info("setCompletionStatus(" + value  + " ) " + equals);
				if (equals)
					setCompletion(equals);
			}
			else
				return "false"; // not recognized
		} catch (Exception e) {
			LOG.severe("setValue " + name + " :" + e);
			return "false";
		}
		return "true";
	}

	@Override
	public String Terminate() {
		sendModuleDataStatement(moduleData);
		if(isCompletion()) sendCompletedStatement(getDuration(), getScoreScaled());
		sendTerminatedStatement();
		return super.Terminate();
	}

	private static native void sendTerminatedStatement() /*-{
		$wnd.sendTerminatedStatement();
	}-*/;

	private static native void sendCompletedStatement(String duration, float scoreScaled) /*-{
		$wnd.sendCompletedStatement(duration, scoreScaled)
	}-*/;

	private static native void sendAnsweredStatement(boolean succes, String duration, double scoreScaled, boolean completion) /*-{
		$wnd.sendAnsweredStatement(succes, duration, scoreScaled, completion)
	}-*/; 

	private static native void sendModuleDataStatement(String moduledata) /*-{
		$wnd.sendModuleDataStatement(moduledata)
	}-*/; 

	private static native void sendAnswerAndModuleDataStatements(boolean succes, String duration, double scoreScaled, boolean completion, String moduledata) /*-{
		$wnd.sendAnswerAndModuleDataStatements(succes, duration, scoreScaled, completion, moduledata)
	}-*/; 
		
	
// Dit kan in GWT zelf?	
	private static native void Initialize0(TinCanAPI2 api) /*-{
		( function(api) {
			
			function waitForXapi() {
				if ( $wnd.sendModuleDataRequest ) 
			    {			
					$wnd.xapi = function (msg) { 
						api.@nl.uu.fi.dwo.mobile.client.sco.TinCanAPI2::onMessage(Ljava/lang/String;)(msg)
					}
					$wnd.sendModuleDataRequest();
			    } else {
			    	console.log("wait for sendModuleDataRequest")
			    	setTimeout(waitForXapi, 100)
			    }
			}			
			setTimeout(waitForXapi, 20)
		})(api)
	}-*/;
	

	
	void onMessage(String s) {
		setModuleData(s);
		startTime = System.currentTimeMillis();
		if(callback!=null)
			callback.onSuccess(null);
		callback = null;
	}

	@Override
	public String Initialize() {
		return super.Initialize();
	}

	@Override
	public void Initialize(AsyncCallback<Void> callback) {
		this.callback = callback;
		if(true)
			Initialize0(this);
		else {
			Initialize0(this);
			startTime = System.currentTimeMillis();
			callback.onSuccess(null);
		}
	}

	@Override
	public Role getRole() {
		return super.getRole();
	}

	private float toScore(String value) {
		float raw = Float.parseFloat(value);
		return (raw/100.0f);
	}

	private String format(long millis)
	{
		return "PT" + (millis / 1000.0F) + "S";
	}

	private String getDuration() {
		return duration = format(System.currentTimeMillis() - startTime);
	}
	
}
