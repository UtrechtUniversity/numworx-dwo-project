package nl.uu.fi.dwo.mobile.utils;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;

public class LogBuilder {
	
	private ActivityComponent activity;
	public LogBuilder(ActivityComponent activity) {
    this.activity = activity;
  }

  private boolean logOption;
	private String logID;
	private String[] smObjectives, smForeknowledge;
	private boolean[][] logObjectives;
	private String className = "";
	private boolean teltMee = true;
	private Integer maxScore;
	private String logIDLabel;

	public LogBuilder setLaunchData(ObjectMap map) {
		logOption = map.getBoolean("logOption", false);
		teltMee   = map.getBoolean("teltMee", true);
		if (map.containsKey("smObjectives")) {
			smObjectives = map.getStringArray("smObjectives");
		}
		if (map.containsKey("smForeknowledge"))
			smForeknowledge = map.getStringArray("smForeknowledge");
		if (map.containsKey("scoreMax")) {
			maxScore = map.getInt("scoreMax");
		}
		if(map.containsKey("logObjectives")) 
		{
			ObjectList logObjectivesList = map.getObjectList("logObjectives");
			logObjectives = new boolean[logObjectivesList.size()][];
			for(int i = 0; i < logObjectivesList.size(); i++)
			{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
			}
		}
		logID = map.getString("logID");
		logIDLabel = map.getString("logIDLabel");
		return this;
	}
	
	public LogBuilder setLaunchData(Map<String,?> map) {
		return setLaunchData(JSONUtilities.wrapMap(map));
	}
	
	public Logging build() {
		
		Logging logging = null;
		
		if (logOption || smObjectives != null) {
			String logID = logOption ? this.logID : null;
			DWOLogger dwoLogger = new DWOLogger(activity);					
			dwoLogger.setTeltMee(teltMee);
			dwoLogger.setLogIDLabel(logIDLabel);

			logging = dwoLogger;			
			if (maxScore != null) {
				logging.setMaxScore(maxScore.intValue());
			}
			logging.setLogID( logID);
			logging.setClassName(className);
			logging.setLogObjectives(logObjectives);
			logging.setSMObjectives(smObjectives);
			logging.setSMForeknowledge(smForeknowledge);
			logging.setLogOption(logOption);
		}		
		return logging;
	}

	public boolean isLogOption() {
		return logOption;
	}

	public LogBuilder setLogOption(boolean logOption) {
		this.logOption = logOption;
		return this;
	}

	public String getLogID() {
		return logID;
	}

	public LogBuilder setLogID(String logID) {
		this.logID = logID;
		return this;
	}

	public String[] getSmObjectives() {
		return smObjectives;
	}

	public LogBuilder setSmObjectives(String[] smObjectives) {
		this.smObjectives = smObjectives;
		return this;
	}
	public LogBuilder setSmForeknowledge(String[] smForeknowledge) {
		this.smForeknowledge = smForeknowledge;
		return this;
	}

	public boolean[][] getLogObjectives() {
		return logObjectives;
	}

	public LogBuilder setLogObjectives(boolean[][] logObjectives) {
		this.logObjectives = logObjectives;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public LogBuilder setClassName(String className) {
		this.className = className;
		return this;
	}

	public boolean isTeltMee() {
		return teltMee;
	}

	public LogBuilder setTeltMee(boolean teltMee) {
		this.teltMee = teltMee;
		return this;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public LogBuilder setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
		return this;
	}

	public LogBuilder setLogIDLabel(String string) {
		logIDLabel = string;
		return this;
	}
}
