package nl.uu.fi.dwo.mobile.utils;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;

public class DWOLogger implements Logging {
	
	
	private static final String START = "0";
	private static final String SEPARATOR = "  ;  ";
	private static final String LOG_ERROR_COUNT = "logErrorCount";
	private static final String LOG_ATTEMPTS = "logAttempts";
	private String logID;
	private Logging delegate;
	private Memento memento;
	private JSONObject map;
	private JSONArray  attempts;
	private JSONNumber maxScore;
	private int errorCount, attemptsCount;

	public DWOLogger(Logging delegate) {		
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
		boolean error = Boolean.TRUE.equals(parameters.get("success"));
		
		map.put("logAnswer", new JSONString(formula));
		if (score != null)
			map.put("logScore", score);
		if(maxScore != null)
			map.put("logMaxScore", maxScore);
		if (error) errorCount++;
		map.put(LOG_ERROR_COUNT, new JSONNumber(errorCount));
		attempts.set(attemptsCount, new JSONString(attempt));
		attemptsCount ++;
		map.put("logAttemptsCount", new JSONNumber(attemptsCount));
		
	}

	private JSONNumber getScore(Map<String, ?> parameters) {
		Map map = (Map) parameters.get("score");
		if(map == null) return null;
		Number n = (Number) map.get("raw");
		if(n == null) return null;
			return new JSONNumber(n.doubleValue());
	}
	
	private String buildAttempt(Map<String, ?> parameters) {
		String s = (String) parameters.get("formula");
		String fbTekst = (String) parameters.get("feedback");
		Object stapNr = parameters.get("step");
		if(stapNr == null) stapNr = START;
		Object goedFout = parameters.get("success"); // goed/fout/half/""
		if(s == null)
			s = "";
		if(fbTekst == null) 
			fbTekst = "";

		s += SEPARATOR;
		s += new Date();
		s += SEPARATOR;
		
		s +=  "Regelnummer = " + stapNr;
		s += SEPARATOR;
		if (stapNr == START)
			s = s + "start";
		else
			s = s + goedFout;
		s = s + SEPARATOR;
		s = s + "score = " + getScore(parameters);
		s = s + SEPARATOR;
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

}
