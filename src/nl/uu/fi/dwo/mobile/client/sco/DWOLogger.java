package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class DWOLogger implements Logging {
	
	
	private static final String LOGKEY_ANSWER = "logAnswer";
	private static final String LOGKEY_MAXSCORE = "logMaxScore";
	private static final String START = "0";
	private static final String SEPARATOR = "  ;  ";
	private static final String LOG_ERROR_COUNT = "logErrorCount";
	public static final String LOG_ATTEMPTS = "logAttempts";
	private static final String LOG_ATTEMPTS_COUNT = "logAttemptsCount";
	private static final String LOGKEY_SCORE = "logScore";
	private String logID;
	private Logging delegate;
	private Memento memento;
	private JSONObject map;
	private JSONArray  attempts;
	private JSONNumber maxScore;
	private JSONString logIDLabel;
	private boolean[][] logObjectives;
	private int errorCount, attemptsCount;
	private JSONBoolean teltMee;

	public DWOLogger() {
		this(DWOplayer.PARAMETERS.getLogging());
	}
	
	DWOLogger(Logging delegate) {		
		memento = Memento.instance();
		this.delegate = delegate;
	}
/*
 			logMap.put("logAnswer", formule);
			logMap.put("logScore", new Integer(score));
			logMap.put("logMaxScore", new Integer(scoreMax));
			logMap.put("logErrorCount", new Integer(errorCount));
			logMap.put("logAttemptsCount", new Integer(attemptsCount));
			logMap.put("logAttempts", log);

 */
	@Override
	public void log(Map<String, ?> parameters) {
		String formula = (String)parameters.get("response");
		String attempt = buildAttempt(parameters);
		JSONNumber score = getScore(parameters);
		boolean error = Boolean.FALSE.equals(parameters.get("success"));
		
		map.put(LOGKEY_ANSWER, new JSONString(formula));
		if (score != null)
			map.put(LOGKEY_SCORE, score);
		if(maxScore != null)
			map.put(LOGKEY_MAXSCORE, maxScore);
		if(logIDLabel != null)
			map.put("logIDLabel", logIDLabel);
		if(teltMee != null)
			map.put("teltMee", teltMee);
		if (error) errorCount++;
		map.put(LOG_ERROR_COUNT, new JSONNumber(errorCount));
		attempts.set(attemptsCount, new JSONString(attempt));
		attemptsCount ++;
		map.put(LOG_ATTEMPTS_COUNT, new JSONNumber(attemptsCount));
		
		if(delegate != null) {
			delegate.log(parameters);
		}
	}

	/** Save last answer. No attempt
	 * 
	 * @param parameters
	 */
	public void updateLog(Map<String, ?> parameters) {
		String formula = (String)parameters.get("response");
		map.put(LOGKEY_ANSWER, new JSONString(formula));
		JSONNumber score = getScore(parameters);
		if (score != null)
			map.put(LOGKEY_SCORE, score);
		if(maxScore != null)
			map.put(LOGKEY_MAXSCORE, maxScore);
		if(logIDLabel != null)
			map.put("logIDLabel", logIDLabel);
		if(teltMee != null)
			map.put("teltMee", teltMee);
		map.put(LOG_ERROR_COUNT, new JSONNumber(errorCount));
		map.put(LOG_ATTEMPTS_COUNT, new JSONNumber(attemptsCount));
		if(logIDLabel != null)
			map.put("logIDLabel", logIDLabel);
	}
	
	
	
	
	private JSONNumber getScore(Map<String, ?> parameters) {
		Map map = (Map) parameters.get("score");
		if(map == null) return null;
		Number n = (Number) map.get("raw");
		if(n == null) return null;
			return new JSONNumber(n.doubleValue());
	}
	
	private String getGoedFout(Object success) {
		if(success == null) 
			return "half";
		if(Boolean.TRUE.equals(success)) 
			return "goed";
		if(Boolean.FALSE.equals(success))
			return "fout";
		return success.toString();
	}
	
	private String buildAttempt(Map<String, ?> parameters) {
		String s = (String) parameters.get("formula");
		if(s == null) s = (String) parameters.get("response");
		Object fbTekst = parameters.get("feedback");
		Object stapNr = parameters.get("step");		
		Object goedFout = getGoedFout(parameters.get("success")); // goed/fout/half/""
		if(s == null)
			s = "";
		if(fbTekst == null) 
			fbTekst = "";

		s += SEPARATOR;
		if (START.equals(stapNr))
			s = s + "start";
		else
			s = s + goedFout;
		s = s + SEPARATOR;
		s = s + "score = " + getScore(parameters);
		s = s + SEPARATOR;
		s += new Date();
		s += SEPARATOR;
		if(stapNr != null) s +=  "Regelnummer = " + stapNr;
		s += SEPARATOR;
		s = s + fbTekst;
		return s;
	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		if(delegate != null)
			delegate.setCommunicationRoot(comRoot);
	}

	@Override
	public void setLogID(String string) {
		logID = string;
		if(delegate != null)
			delegate.setLogID(string);
		attempts = null;
		map = memento.getLogState(string);
		JSONValue value = map.get(LOG_ATTEMPTS);
		if(value != null)
			attempts =  value.isArray();
		if(attempts == null) {
			attempts = new JSONArray();
			map.put(LOG_ATTEMPTS, attempts);
		}
		errorCount = 0;
		attemptsCount = attempts.size();
		value = map.get(LOG_ERROR_COUNT);
		if(value != null) {
			JSONNumber number = value.isNumber();
			if(number != null) {
				errorCount = (int) number.doubleValue();
			}
		}
		
	}

	@Override
	public void setClassName(String string) {
		if(delegate != null)
			delegate.setClassName(string);
	}

	public void setMaxScore(int max) {
		maxScore = new JSONNumber(max);
	}
	
	public void setLogObjectives(boolean[][] logObjectives) {
		this.logObjectives = logObjectives;
		if(logObjectives != null)
			map.put("logObjectives", JSONUtilities.toJSONArray(logObjectives));
	}
	
	public void setTeltMee(boolean teltMee) {
		this.teltMee = JSONBoolean.getInstance(teltMee);
	}
	
	public void setLogIDLabel(String label) {
		if(label != null)
			logIDLabel = new JSONString(label);
		else
			logIDLabel = null;
	}
	
	public void getStateHook(Map<String,Object> state) {
		if(attempts != null)
			state.put("log", attempts);
	} // put objects into state

	public void setStateHook(Map<String,Object> state) {} // get objects from state (if global logging on)

	public Logging getLogger() {
		return delegate == null ? this : delegate;
	}
}
