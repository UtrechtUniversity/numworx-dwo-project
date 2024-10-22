package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class DWOLogger implements Logging {
	
	
	static final String LOG_OBJECTIVES = "logObjectives";
	static final String  SM_OBJECTIVES =  "smObjectives";
	private static final String LOGKEY_ANSWER = "logAnswer";
	private static final String LOGKEY_MAXSCORE = "logMaxScore";
	private static final String START = "0";
	private static final String SEPARATOR = "  ;  ";
	private static final String LOG_ERROR_COUNT = "logErrorCount";
	public static final String LOG_ATTEMPTS = "logAttempts";
	static final String LOG_ATTEMPTS_COUNT = "logAttemptsCount";
	private static final String LOGKEY_SCORE = "logScore";
	private String logID;
	private Logging delegate;
	private Memento memento;
	private JSONObject map;
	private JSONArray  attempts;
	private JSONNumber maxScore;
	private JSONString logIDLabel;
	private int errorCount, attemptsCount;
	private JSONBoolean teltMee;
	private boolean logOption = true;

	public DWOLogger(ActivityComponent a) {
		this(a.getLogging(), a.memento());
	}
	
	private DWOLogger(Logging delegate, Memento m) {		
		memento = m;
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
	private Map<String,?> last;
	
	@Override
	public void log(Map<String, ?> parameters) {
		if (parameters.equals(last))
		{
			java.util.logging.Logger.getLogger("DWOLogger").warning("logging duplicate " + last);
			return; // duplicate the simple way
		}
		boolean isAttempt = last != null;
		last = new HashMap<>(parameters);
		
		String formula = (String)parameters.get("response");
		if (formula == null)
			formula = "";
		String attempt = buildAttempt(parameters);
		JSONNumber score = getScore(parameters);
		boolean error = Boolean.FALSE.equals(parameters.get("success"));
		
		JSONString formulaString = new JSONString(formula);
		JSONValue old = map.put(LOGKEY_ANSWER, formulaString);
		if (!isAttempt) isAttempt = !Objects.equals(formulaString, old);
		old = null;
		if (score != null)
			old = map.put(LOGKEY_SCORE, score);
		if (!isAttempt) isAttempt = !Objects.equals(score, old);
		if(maxScore != null)
			map.put(LOGKEY_MAXSCORE, maxScore);
		if(logIDLabel != null)
			map.put("logIDLabel", logIDLabel);
		if(teltMee != null)
			map.put("teltMee", teltMee);
		//if (!isAttempt) return; // duplicate the complex way FIXME DOES NOT WORK! isAttempt is always false
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
	@Override
	public void updateLog(Map<String, ?> parameters) {
		String formula = (String)parameters.get("response");
		if (formula != null) map.put(LOGKEY_ANSWER, new JSONString(formula));
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
		if (delegate != null) 
		  delegate.updateLog(parameters);
	}
	
	private JSONNumber getScore(Map<String, ?> parameters) {
		Map map = (Map) parameters.get("score");
		if(map == null) return null;
		Number n = (Number) map.get("raw");
		if(n == null) {
			n = (Number) map.get("scaled");
			if (n != null && maxScore != null) {
				return new JSONNumber(n.doubleValue()*maxScore.doubleValue());
			}
			return null;
		}
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
		map = memento != null ? memento.getLogState(string) : new JSONObject();
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
        if(delegate != null)
          delegate.setMaxScore(max);
	}
	
	@Override
	public void setLogObjectives(boolean[][] logObjectives) {
		if(logObjectives != null)
			map.put(LOG_OBJECTIVES, JSONUtilities.toJSONArray(logObjectives));
		if(delegate != null)
			delegate.setLogObjectives(logObjectives);
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
	
	public String getLogIDLabel()
	{
		return logIDLabel.stringValue();
	}
	
	@Override
	public void getStateHook(Map<String,Object> state) {
		if(attempts != null)
			state.put("log", attempts);
		if (delegate != null) {
			delegate.getStateHook(state);
		}
	} // put objects into state

	@Override
	public void setStateHook(Map<String,Object> state) {
		if (delegate != null) {
			delegate.setStateHook(state);
		}
	} // get objects from state (if global logging on)

	public Logging getLogger() {
		return delegate == null ? this : delegate;
	}

	public String getLogID() {
		return logID;
	}

  @Override
  public void setSMObjectives(String[] objectives) {
    if(objectives != null)
      map.put(SM_OBJECTIVES, JSONUtilities.toJSONArray(objectives));
    if (delegate != null) {
      delegate.setSMObjectives(objectives);
    }
  }
  @Override
  public String[] getSMObjectives() {
	  return delegate != null ? delegate.getSMObjectives(): null;
  }

	public void setLogOption(boolean logOption) {
		this.logOption = logOption;
		if (delegate != null) delegate.setLogOption(logOption);
	}

	@Override
	public void setSMForeknowledge(String[] foreknowledge) {
		if (delegate != null) {
			delegate.setSMForeknowledge(foreknowledge);
		}
	}
	@Override
	public String[] getSMForeknowledge() {
		return delegate != null ? delegate.getSMForeknowledge(): null;
	}

	@Override
	public void setSMGuess(Number smGuess) {
		if (delegate != null) {
			delegate.setSMGuess(smGuess);
		}
	}
	
}
