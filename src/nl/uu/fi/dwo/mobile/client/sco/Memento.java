package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class om suspend_data in en uit te pakken. JSON format, Javascript is hier
 * first-class citizen.
 * 
 * @author velth101
 * 
 */
public class Memento implements ClosingHandler, CloseHandler<Window>, CBookEventListener
{
	private static final String BEZOCHT = "bezocht";
	private static final String ZELFTOETS_NAGEKEKEN = "zelftoetsNagekeken";
	private static final String TEMPOTOETS_LOCKED = "tempotoetsLocked";
	private static final String TEMPOTOETS_SECONDS_LEFT = "tempotoetsSecondsLeft";
	private static Memento _instance;
	static private Logger logger = Logger.getLogger("Memento");

	static Memento instance() {
		return _instance;
	}
	
	static public native void instalOnBeforeUnload() /*-{
		$wnd.onbeforeunload = @nl.uu.fi.dwo.mobile.client.sco.Memento::unload();
		$wnd.onunload = @nl.uu.fi.dwo.mobile.client.sco.Memento::unload();
	}-*/;

	static void unload()
	{
		logger.severe("unload");
		if (_instance != null)
			_instance.close();
	}

	private static final String OPDR_CONT_STATES = "opdrContStates";
	private static final String STRAFPUNTEN = "strafpunten"; // optional!
	private static final String GOED_FOUT = "orGoedFout";
	private static final String GOED_FOUT_ZELFTOETS = "isCorrectZelftoets";
	private static final String SCORES = "orScores"; // TODO correct name? getPagina score gebruikt deze naam
	private static final String SCORES_ZELFTOETS = "scoresZelftoets"; 
	private static final String NAKIJKEN_ZELFTOETS_PENDING = "nakijkenZelftoetsPending"; 
	private static final String ONS_STATE = "onsState";
	private static final String LOG_STATE = "log";
	private static final String AANTAL_NAKIJKEN = "aantalNakijken";

	static final String SUSPEND_DATA = "cmi.suspend_data";
	static final String SCORE_RAW = "cmi.score.raw";
	static final String EXIT_STATUS = "cmi.exit";
	private static final String SESSION_TIME = "cmi.session_time";
	private static final String TOTAL_TIME = "cmi.total_time";
	static final String COMPLETION_STATUS = "cmi.completion_status";
	static final String COMPLETED = "completed";
	
	static final String EXIT_NORMAL = "normal";
	static final String EXIT_SUSPEND = "suspend";
	public static final String LEARNER_ID = "cmi.learner_id";
	public static final String LEARNER_NAME = "cmi.learner_name";
	public static final String LEARNER_PREFERENCE_LANGUAGE = "cmi.learner_preference.language";
	public static final String LOCATION = "cmi.location";
	private static final String REVIEW_DATA = "cmi.comments_from_lms.0.comment";
	
	public static final String LESSON_MODE = "cmi.mode";
	public static final String SHARE_MAP = "shareMap";
	
	private Scorm2004IF api;

	private JSONObject suspendData;
	private JSONObject onsState, shareMap;
	private JSONObject logState;
	private JSONArray opdrContStates, opdrStrafpunten, opdrGoedFout, opdrScores, opdrBezocht;
	/**
	 * Scores (per activiteit, per opdracht/pagina, vgl. opdrScores) die getoond worden 
	 * in een nagekeken zelftoets. Als een antwoord gewijzigd is na
	 * het nakijken van de zelftoets, kan de score van de pagina met de huidige 
	 * ingevulde antwoorden anders zijn dan die opgeslagen in scoresZelftoets.
	 * De score bij de huidige ingevulde antwoorden moet pas getoond
	 * worden als op de nakijk-knop wordt gedrukt.
	 */
	private JSONArray scoresZelftoets;
	/**
	 * Correctheid (per activiteit, per opdracht/pagina, vgl. opdrGoedFout) die getoond wordt
	 * in een nagekeken zelftoets dmv een groen of rood bolletje.
	 * Als een antwoord gewijzigd is na het nakijken van de zelftoets, 
	 * kan de correctheid van de pagina met de huidige
	 * ingevulde antwoorden anders zijn dan die opgeslagen in isCorrectZelftoets.
	 * De correctheid bij de huidige ingevulde antwoorden moet pas getoond
	 * worden als op de nakijk-knop wordt gedrukt.
	 */
	private JSONArray isCorrectZelftoets;
	/**
	 * Een array van booleans (per activiteit, per opdracht/pagina) die aangeeft of er voor de 
	 * betreffende opdracht een zelftoets nakijken pending is. Als de zelftoets is nagekeken
	 * en een opdracht wordt daarna voor het eerst bezocht, dan is pending true. Daarna false.
	 */
	private JSONArray nakijkenZelftoetsPending;
	private JSONBoolean zelftoetsNagekeken, zelftoetsGeenCorr;
	private JSONBoolean tempotoetsLocked;
	private JSONNumber tempotoetsSecondsLeft;

	private String scoreRaw;
	private Date startDate = new Date();

	private Number score;
	private JSONArray aantalNakijken;

	public Memento(Scorm2004IF api)
	{
		this.api = api;
		_instance = this;
		Window.addWindowClosingHandler(this);
		Window.addCloseHandler(this);
		registration = DWOplayer.clientfactory.getEventBus().addHandler(CBookEvent.TYPE, this);
		initialize();
		String value;
		scoreRaw = getValue(SCORE_RAW);
		
		value = getValue(COMPLETION_STATUS);
		eindtoetsVerzegeld = COMPLETED.equals(value);

		String reviewData = null; 
		if (eindtoetsVerzegeld)
			reviewData = getValue(REVIEW_DATA);
		
		try
		{
			value = getValue(SUSPEND_DATA);
			suspendData = (JSONObject) JSONParser.parseStrict(value);
			onsState = (JSONObject) suspendData.get(ONS_STATE);
			opdrContStates = (JSONArray) onsState.get(OPDR_CONT_STATES);
			opdrStrafpunten = (JSONArray) onsState.get(STRAFPUNTEN);
			opdrGoedFout  = (JSONArray) onsState.get(GOED_FOUT);
			isCorrectZelftoets  = (JSONArray) onsState.get(GOED_FOUT_ZELFTOETS);
			opdrScores    = (JSONArray) onsState.get(SCORES);
			scoresZelftoets = (JSONArray) onsState.get(SCORES_ZELFTOETS);
			nakijkenZelftoetsPending = (JSONArray) onsState.get(NAKIJKEN_ZELFTOETS_PENDING);
			opdrBezocht   = (JSONArray) onsState.get(BEZOCHT);
			zelftoetsNagekeken = (JSONBoolean) onsState.get(ZELFTOETS_NAGEKEKEN);
			tempotoetsLocked = (JSONBoolean) onsState.get(TEMPOTOETS_LOCKED);
			tempotoetsSecondsLeft = (JSONNumber) onsState.get(TEMPOTOETS_SECONDS_LEFT);
			aantalNakijken = (JSONArray) onsState.get(AANTAL_NAKIJKEN);
			logState = (JSONObject) suspendData.get(LOG_STATE);
			shareMap = (JSONObject) onsState.get(SHARE_MAP);
			if (reviewData != null && reviewData.length() > 2)
				opdrContStates = mergeReviewData(opdrContStates, reviewData);
			// oldschool toetsLocked
			JSONValue oldReviewData =  suspendData.get("reviewData");
			if(!eindtoetsVerzegeld && oldReviewData != null) {
				JSONValue oldToetsLocked = oldReviewData.isObject().get("toetsLocked");
				if(oldToetsLocked != null) {
					if(oldToetsLocked.isBoolean().booleanValue()) eindtoetsVerzegeld = true;
				}
			}
		}
		catch (Exception e)
		{
			opdrContStates = new JSONArray();
			onsState = new JSONObject();
			onsState.put(OPDR_CONT_STATES, opdrContStates);
			suspendData = new JSONObject();
			suspendData.put(ONS_STATE, onsState);
			shareMap = null;
		}

		instalOnBeforeUnload();
		
		ShareFacade.setSharedState(shareMap);
	}

	private JSONArray mergeReviewData(JSONArray opdrContStates, String reviewData) {
		try {
			JSONValue value = JSONParser.parseStrict(reviewData);
			JSONArray review = value.isObject().get(OPDR_CONT_STATES).isArray();
			int l = Math.min(opdrContStates.size(), review.size());
			for(int i = 0; i < l; i++) {
				JSONArray statei = opdrContStates.get(i).isArray();
				JSONArray reviewi = review.get(i).isArray();
				if(statei == null || reviewi == null ) continue;
				int ll = Math.min(statei.size(), reviewi.size());
				for(int j = 0; j < ll; j++ ) {
					JSONObject statej = statei.get(j).isObject();
					JSONObject reviewj = reviewi.get(j).isObject();
					if(statej == null || reviewj == null ) continue;
					statej = statej.get("interactiePanelStates").isArray().get(5).isObject();
					mergeReviewState(statej, reviewj, 5);
				}
				
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE,"mergeReviewData", e);
		}
		return opdrContStates;
	}

	private void mergeReviewState(JSONObject statej, JSONObject reviewj, int off) {
		if (statej == null || reviewj == null) return;
		if (statej.containsKey("interactiePanelStates")) {
			JSONArray stateArray = statej.get("interactiePanelStates").isArray();
			JSONArray reviewArray = reviewj.get("interactiePanelStates").isArray();
			int l = Math.min(stateArray.size()+off, reviewArray.size());
			for(int i = off; i < l; i++) {
				mergeReviewState( stateArray.get(i-off).isObject(), reviewArray.get(i).isObject(), 0);
			}
		} else {
			Set<String> keys = reviewj.keySet();
			for (String key : keys) {
				statej.put(key, reviewj.get(key));
			}
		}
	}

	String getValue(String key)
	{
		try
		{
			return api.GetValue(key);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE,"getValue", e);
			return "";
		}
	}

	private void initialize()
	{
		try
		{
			this.api.Initialize();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "initialize Scorm API", e);
		}
	}

	public double getScore()
	{
		if (score == null)
		{
			score = new Double(scoreRaw);
		}
		return score.doubleValue();
	}

	public void setScore(double score)
	{
		this.score = score;
		scoreRaw = this.score.toString();
		setValue(SCORE_RAW, scoreRaw);
	}
	
	boolean setValue(String key, String value)
	{
		try
		{	if (!isEindtoetsVerzegeld())
				return "true".equals(api.SetValue(key, value));
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE,"setValue", e);
		}
		return false;
	}

	public void setStrafpunten(int[][] o) {
		opdrStrafpunten = setIntArrayArray(o, opdrStrafpunten, STRAFPUNTEN);
	}
	
	public void setScores(int[][] o) {
		opdrScores = setIntArrayArray(o, opdrScores, SCORES);
	}

	private JSONArray setIntArrayArray(int[][] o, JSONArray source, String key) {
		if(o == null) return source;
		for (int i = 0; i < o.length; i++) {
			int[] oi = o[i];
			for (int j = 0; j < oi.length; j++) {
				int punt = oi[j];
				if(punt != 0) {
					if ( source == null) {
						source = new JSONArray();
						onsState.put(key, source);
					}
					if( i >= source.size() || isNull(source.get(i)) )
						source.set(i, new JSONArray());
					JSONArray array = source.get(i).isArray();
					array.set(j, new JSONNumber(punt));
				} else {
					if(source == null 
							|| source.size() <= i 
							|| isNull(source.get(i)))
						continue;
					JSONArray array = source.get(i).isArray();
					if(j >= array.size()) continue;
					array.set(j, new JSONNumber(0));
				}
			}
			
		}
		return source;
	}
	
	public void setOrGoedFout(boolean[][] o) {
		if(o == null) return;
		for (int i = 0; i < o.length; i++) {
			boolean[] oi = o[i];
			for (int j = 0; j < oi.length; j++) {
				boolean punt = oi[j];
				if(punt != false) {
					if ( opdrGoedFout == null) {
						opdrGoedFout = new JSONArray();
						onsState.put(GOED_FOUT, opdrGoedFout);
					}
					if( i >= opdrGoedFout.size() || isNull(opdrGoedFout.get(i)) )
						opdrGoedFout.set(i, new JSONArray());
					JSONArray array = opdrGoedFout.get(i).isArray();
					array.set(j, JSONBoolean.getInstance(punt));
				} else {
					if(opdrGoedFout == null 
							|| opdrGoedFout.size() <= i 
							|| isNull(opdrGoedFout.get(i)))
						continue;
					JSONArray array = opdrGoedFout.get(i).isArray();
					if(j >= array.size()) continue;
					array.set(j, JSONBoolean.getInstance(false));
				}
			}
			
		}
	}

	public JSONObject getLogState(String logID) {
		if(logID == null)
			return new JSONObject(); // No logging for MC2: Null Pattern
		if(logState == null) 
		{
			logState = new JSONObject();
			suspendData.put(LOG_STATE, logState);
		}
		JSONObject obj = (JSONObject) logState.get(logID);
		if(obj == null) {
			obj = new JSONObject();
			logState.put(logID, obj);
		}
		return obj;
	}
	
	public JSONObject getLogState() {
		return logState;
	}
	
	public void setLogState(String logID, JSONObject value) {
		if(logState == null) 
		{
			logState = new JSONObject();
			suspendData.put(LOG_STATE, logState);
		}
		logState.put(logID, value);
	}
	

	private boolean isNull(JSONValue jsonValue) {
		return jsonValue == null | jsonValue.isNull() == JSONNull.getInstance();
	}

	public void getStrafpunten(int[][] o) {
		getIntArrayArray(o, opdrStrafpunten);
	}
	
	public void getScores(int[][] o) {
		getIntArrayArray(o, opdrScores);
	}

	private void getIntArrayArray(int[][] o, JSONArray source) {
		if(o == null) return;
		if(source == null) {
			for (int i = 0; i < o.length; i++) {
				int[] oi = o[i];
				for (int j = 0; j < oi.length; j++) {
					oi[j] = 0;
				}
			}
		} else {
			for (int i = 0; i < o.length; i++) {
				JSONArray array = getArray(i, source);
				int[] oi = o[i];
				for (int j = 0; j < oi.length; j++) {
					oi[j] = getInt(array, j);
				}
			}
		}
	}
	
	public void getScoresObjectives(int[][][][] o)
	{
		getIntArrayArrayArrayArray(o, (JSONArray) onsState.get("scoresObjectives"));
	}
	
	public void getPossibleMisconceptions(int[][][][] o)
	{
		getIntArrayArrayArrayArray(o, (JSONArray) onsState.get("possibleMisconceptions"));
	}
	
	public void getMeasuredMisconceptions(int[][][][] o)
	{
		getIntArrayArrayArrayArray(o, (JSONArray) onsState.get("measuredMisconceptions"));
	}
	
	
	private void getIntArrayArrayArrayArray(int[][][][] o, JSONArray source) {
		
		if(o == null) return;
		if(source == null) {
			for(int i = 0; i < o.length; i++){
				int[][][] oi = o[i];
				for(int j = 0; j < oi.length; j++){
					int[][] oij = oi[j];
					for(int k = 0; k < oij.length; k++){
						int[] oijk = oij[k];
						for(int l = 0; l < oijk.length; l++){
							oijk[l] = 0;
						}
					}
				}
			}
		}
		else {
			for (int i = 0; i < o.length; i++) {
				JSONArray array1 = getArray(i, source);
				int[][][] oi = o[i];
				for(int j = 0; j < oi.length; j++){
					JSONArray array2 = getArray(j, array1);
					int[][] oij = oi[j];
					for(int k = 0; k < oij.length; k++){
						JSONArray array3 = getArray(k, array2);
						int[] oijk = oij[k];
						for(int l = 0; l < oijk.length; l++){
							oijk[l] = getInt(array3, l);
						}
					}
				}
			}
		}
	}
	
	public void getOrGoedFout(boolean[][] o) {
		if(o == null) return;
		if(opdrGoedFout == null) {
			for (int i = 0; i < o.length; i++) {
				boolean[] oi = o[i];
				for (int j = 0; j < oi.length; j++) {
					oi[j] = false;
				}
			}
		} else {
			for (int i = 0; i < o.length; i++) {
				JSONArray array = getArray(i, opdrGoedFout);
				boolean[] oi = o[i];
				for (int j = 0; j < oi.length; j++) {
					oi[j] = getBoolean(array, j);
				}
			}
		}
	}

	private boolean getBoolean(JSONArray array, int j) {
		if(array == null || j < 0 || j > array.size())
			return false;
		JSONValue value = array.get(j);
		if(value == null) return false;
		JSONBoolean number = value.isBoolean();
		if(number != null)
			return number.booleanValue();
		return false;
	}

	public int getInt(JSONArray array, int j) {
		if(array == null || j < 0 || j > array.size())
			return 0;
		JSONValue jsonValue = array.get(j);
		if(jsonValue == null) 
			return 0;
		JSONNumber number = jsonValue.isNumber();
		if(number != null)
			return (int) number.doubleValue();
		return 0;
	}

	public JSONArray getArray(int i, JSONArray array) {
		if ( array == null || i < 0 || i > array.size())
			return null;
		JSONValue get = array.get(i);
		if(get == null)
			return null;
		return get.isArray();
	}
	
	
	
	public void setOpdrContStates(HashMap<String, Object>[][] o)
	{

		for (int i = 0; i < o.length; i++)
		{
			JSONArray array = new JSONArray();
			HashMap<String, Object>[] oo = o[i];
			boolean fuse = false;
			for (int j = oo.length - 1; j >= 0; j--)
			{
				HashMap<String, Object> ooo = oo[j];
				if (fuse || ooo != null)
				{
					fuse = true;
					array.set(j, JSONUtilities.toJSONObject(ooo));
				}
			}
			opdrContStates.set(i, array);
		}
		setValue(SUSPEND_DATA, suspendData.toString());
	}


	@SuppressWarnings("unchecked")
	public HashMap<String, Object>[][] getOpdrContStates(HashMap<String, Object>[][] o)
	{
		for (int i = 0; i < o.length; i++)
		{
			JSONArray array = (JSONArray) opdrContStates.get(i);
			if (array == null)
				continue;
			HashMap<String, Object>[] oo = o[i];
			if (oo == null)
				o[i] = oo = new HashMap[array.size()];
			int len = array.size();
			len = Math.min(len,oo.length);
			for (int j = 0; j < len; j++)
			{
				JSONValue value = array.get(j);
				logger.fine("getstate " + j + "= " + value);
				oo[j] = value == null ? null : JSONUtilities.wrapMap(value.isObject());
			}
		}
		return o;
	}


	public void flush()
	{
//		System.out.println("START SUSPENDDATA-----------");
//		System.out.println(suspendData.toString());
//		System.out.println("END SUSPENDDATA-----------");
		logger.info("memento flush");
		try
		{
			if(this == _instance) // API break?
				api.Commit();
			else 
				logger.fine("No commit, since we are closing, terminate should follow!");
		}
		catch (Exception e)
		{
		}
	}

	public void close()
	{
		if (this != _instance)
			return;
		_instance = null;
		OpdrNav.immediate(
		new ScheduledCommand() {
			public void execute() {
				logger.info("closing memento");
				if(registration != null) {
					registration.removeHandler();
					registration = null;
				}
				runner.run();
				setSessionTimes();
				try {
					api.Terminate();
					api = null;
				} catch (Exception e) {
				}
			}
		});
	}

	private long parse(String totalStr) {
		return from2004Time(totalStr);
	}

	
	public long from2004Time(String str) {
		
		  // Only gross syntax check is performed here
		  // Months calculated by approximation based on average number
		  // of days over 4 years (365*4+1), not counting the extra days
		  // in leap years. If a reference date was available,
		  // the calculation could be more precise, but becomes complex,
		  // since the exact result depends on where the reference date
		  // falls within the period (e.g. beginning, end or ???)
		  // 1 year ~ (365*4+1)/4*60*60*24*100 = 3155760000 centiseconds
		  // 1 month ~ (365*4+1)/48*60*60*24*100 = 262980000 centiseconds
		  // 1 day = 8640000 centiseconds
		  // 1 hour = 360000 centiseconds
		  // 1 minute = 6000 centiseconds
		  float aV[] = new float[6];
		  boolean bErr = false;
		  boolean bTFound = false;
		  if (str.indexOf("P") != 0) bErr = true;
		  if (!bErr)
		  {
		    String[] aT = new String[] {"Y","M","D","H","M","S"};
		    int p=0, i=0;
		    str = str.substring(1); //get past the P
		    for (i = 0 ; i < aT.length; i++)
		    {
		      if (str.indexOf("T") == 0)
		      {
		        str = str.substring(1);
		        i = Math.max(i,3);
		        bTFound = true;
		      }
		      p = str.indexOf(aT[i]);
		      //alert("Checking for " + aT[i] + "\nstr = " + str);
		      if (p > -1)
		      {
		        // Is this a M before or after T?
		        if ((i == 1) && (str.indexOf("T") > -1) && (str.indexOf("T") < p)) continue;
		        if (aT[i] == "S")
		        {
		          aV[i] = Float.parseFloat(str.substring(0,p));
		        }
		        else
		        {
		          aV[i] = Integer.parseInt(str.substring(0,p));
		        }
		        if (Float.isNaN(aV[i]))
		        {
		          bErr = true;
		          break;
		        }
		        else if ((i > 2) && (!bTFound))
		        {
		          bErr = true;
		          break;
		        }
		        str = str.substring(p+1);
		      }
		    }
		    if ((!bErr) && (str.length() != 0)) bErr = true;
		    //alert(aV.toString())
		  }
		  if (bErr)
		  {
		    //alert("Bad format: " + str)
		    return 0;
		  }
		  return Math.round(aV[0]*31557600000L + aV[1]*2629800000L
		    + aV[2]*86400000 + aV[3]*3600000 + aV[4]*60000
		    + Math.round(aV[5]*1000)
		    );
		}

	private String format(long millis)
	{
		return "PT" + (millis / 1000.0F) + "S";
	}

	/**
	 * @deprecated Use {@link JSONUtilities#toStringArray(Object)} instead
	 */
	public static String[] toStringArray(Object object)
	{
		return JSONUtilities.toStringArray(object);
	}

	/**
	 * @deprecated Use {@link JSONUtilities#toArrayList(Object)} instead
	 */
	public static List<Object> toArrayList(Object object)
	{
		return JSONUtilities.toArrayList(object);
	}

	private Runnable runner = new Runnable()
	{

		@Override
		public void run()
		{
		}
	};
	private boolean eindtoetsVerzegeld;

	public void setUnload(Runnable opdrNav)
	{
		runner = opdrNav;

	}

	@Override
	public void onClose(CloseEvent<Window> event)
	{
		close();
	}

	@Override
	public void onWindowClosing(ClosingEvent event)
	{
		close();
	}

	public void setCurrentActiviteit(int currentActiviteit) {
		onsState.put("activiteitNr", new JSONNumber(currentActiviteit));
		
	}
	public void setCurrentOpdracht(int currentOpdracht) {
		if(!setValue(LOCATION, Integer.toString(currentOpdracht)))
			setCurrentOpdracht_old(currentOpdracht);
	}

	// old style, for use if 'setValue(location)' mislukt
	private void setCurrentOpdracht_old(int currentOpdracht) {
		onsState.put("opdrachtNr", new JSONNumber(currentOpdracht));
	}
	
	public int getCurrentOpdracht() {
		try {
			return Integer.parseInt(getValue(LOCATION));
		} catch(Exception _)
		{
		}
		try {
			return (int) onsState.get("opdrachtNr").isNumber().doubleValue();
		} catch(Exception e) {
			return 0;
		}
	}
	
	public int getCurrentActiviteit() {
		try {
			return (int) onsState.get("activiteitNr").isNumber().doubleValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void setCompletion(boolean complete) {
		setValue(EXIT_STATUS, complete?EXIT_NORMAL:EXIT_SUSPEND);
	}

	public String getLearnerId() {
		return getValue(LEARNER_ID);
	}
	public String getLearnerName() {
		return getValue(LEARNER_NAME);
	}
	
	public String getLanguage() {
		return getValue(LEARNER_PREFERENCE_LANGUAGE);	
	}

	public void getBezocht(boolean[][] bezocht) {
		if(bezocht == null) return;
		if(opdrBezocht == null) {
		} else {
			for (int i = 0; i < bezocht.length; i++) {
				JSONArray array = getArray(i, opdrBezocht);
				boolean[] oi = bezocht[i];
				for (int j = 0; j < oi.length; j++) {
					oi[j] = getBoolean(array, j);
				}
			}
		}
	}
	
	public void setBezocht(boolean[][] bezocht) {
		if(bezocht == null) return;
		for (int i = 0; i < bezocht.length; i++) {
			boolean[] oi = bezocht[i];
			for (int j = 0; j < oi.length; j++) {
				boolean punt = oi[j];
				if(punt != false) {
					if ( opdrBezocht == null) {
						opdrBezocht = new JSONArray();
						onsState.put(BEZOCHT, opdrBezocht);
					}
					if( i >= opdrBezocht.size() || isNull(opdrBezocht.get(i)) )
						opdrBezocht.set(i, new JSONArray());
					JSONArray array = opdrBezocht.get(i).isArray();
					array.set(j, JSONBoolean.getInstance(punt));
				} else {
					if(opdrBezocht == null 
							|| opdrBezocht.size() <= i 
							|| isNull(opdrBezocht.get(i)))
						continue;
					JSONArray array = opdrBezocht.get(i).isArray();
					if(j >= array.size()) continue;
					array.set(j, JSONBoolean.getInstance(false));
				}
			}
			
		}
	}

	public void setZelftoetsNagekeken(boolean zelftoetsNagekeken) {
		this.zelftoetsNagekeken = JSONBoolean.getInstance(zelftoetsNagekeken);
		this.onsState.put(ZELFTOETS_NAGEKEKEN, this.zelftoetsNagekeken);
	}


	public boolean getZelftoetsNagekeken() {
		if (zelftoetsNagekeken == null)
			return false;
		return zelftoetsNagekeken.booleanValue();
	}

	public LessonMode getLessonMode() {
		try {
			String mode = getValue(LESSON_MODE);
			return LessonMode.valueOf(mode);
		} catch (Exception e) {
			return LessonMode.normal;
		}
	}
	
	public Role getRole() {
		return api.getRole();
	}

	public void setAantalNakijken(int[] aantalNakijken) {
		if(aantalNakijken == null)
		{
			this.aantalNakijken = null;
		} else 
		{		
			this.aantalNakijken = new JSONArray();
			for (int i = 0; i < aantalNakijken.length; i++) {
				this.aantalNakijken.set(i, new JSONNumber(aantalNakijken[i]));
			}
		}
		this.onsState.put(AANTAL_NAKIJKEN, this.aantalNakijken);
	}
	
	public int[] getAantalNakijken() {
		if(this.aantalNakijken == null) {
			return null;
		}
		int[] result = new int[aantalNakijken.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (int) aantalNakijken.get(i).isNumber().doubleValue();
		}
		return result;
	}

	public boolean isEindtoetsVerzegeld()
	{
		return this.eindtoetsVerzegeld;
	}
	
	void setShareMap(JSONObject obj) {
		shareMap = obj;
		onsState.put(SHARE_MAP, obj);
	}

	public void setPossibleMisconceptions(int[][][][] possibleMisconceptions) {
		JSONValue result = JSONUtilities.toJSONValue(possibleMisconceptions);
		onsState.put("possibleMisconceptions", result);
	}

	public void setMeasuredMisconceptions(int[][][][] measuredMisconceptions) {
		JSONValue result = JSONUtilities.toJSONValue(measuredMisconceptions);
		onsState.put("measuredMisconceptions", result);
	}

	public void setScoresObjectives(int[][][][] scoresObjectives) {
		JSONValue result = JSONUtilities.toJSONValue(scoresObjectives);
		onsState.put("scoresObjectives", result);
	}

	public void setTempotoetsLocked(boolean tempotoetsLocked)
	{
		this.tempotoetsLocked = JSONBoolean.getInstance(tempotoetsLocked);
		this.onsState.put(TEMPOTOETS_LOCKED, this.tempotoetsLocked);
	}

	public void setTempotoetsSecondsLeft(int seconds)
	{
		this.tempotoetsSecondsLeft = new JSONNumber(seconds);
		this.onsState.put(TEMPOTOETS_SECONDS_LEFT, this.tempotoetsSecondsLeft);
	}
	
	public int[][] getScoresZelftoets()
	{
		if (scoresZelftoets == null)
		{
			return null;
		}
		
		int aantalActiviteiten = 0;
		int aantalOpdrachten = 0;
		
		aantalActiviteiten = scoresZelftoets.size();
		
		for (int i = 0; i < scoresZelftoets.size(); i++)
		{
			JSONArray array = scoresZelftoets.get(i).isArray();
			aantalOpdrachten = Math.max(array.size(), aantalOpdrachten);
		}
		
		int[][] intArray = new int[aantalActiviteiten][aantalOpdrachten];
		
		for (int i = 0; i < scoresZelftoets.size(); i++)
		{
			JSONArray array = scoresZelftoets.get(i).isArray();
			for (int j = 0; j < array.size(); j++)
			{
				JSONNumber jsonNr = array.get(j).isNumber();
				if (jsonNr != null)
				{
					intArray[i][j] = (int) jsonNr.doubleValue();
				}
			}
		}		
		return intArray;
	}
	
	public void setScoresZelftoets(int[][] scores)
	{
		JSONArray array = new JSONArray();

		for (int i = 0; i < scores.length; i++)
		{
			int[] scoresPerActiviteit = scores[i];
			Integer[] integerScores = new Integer[scoresPerActiviteit.length];
			for (int j = 0; j < scoresPerActiviteit.length; j++)
			{
				integerScores[j] = new Integer(scoresPerActiviteit[j]);
			}
			array.set(i, JSONUtilities.toJSONArray(integerScores));
		}

		this.scoresZelftoets = array;
		this.onsState.put(SCORES_ZELFTOETS, this.scoresZelftoets);
	}
	
	public boolean[][] isCorrectZelftoets()
	{
		if (isCorrectZelftoets == null)
		{
			return null;
		}
		
		int aantalActiviteiten = 0;
		int aantalOpdrachten = 0;
		
		aantalActiviteiten = isCorrectZelftoets.size();
		
		for (int i = 0; i < isCorrectZelftoets.size(); i++)
		{
			JSONArray array = isCorrectZelftoets.get(i).isArray();
			aantalOpdrachten = Math.max(array.size(), aantalOpdrachten);
		}
		
		boolean[][] booleanArray = new boolean[aantalActiviteiten][aantalOpdrachten];
		
		for (int i = 0; i < isCorrectZelftoets.size(); i++)
		{
			JSONArray array = isCorrectZelftoets.get(i).isArray();
			for (int j = 0; j < array.size(); j++)
			{
				JSONBoolean jsonBoolean = array.get(j).isBoolean();
				if (jsonBoolean != null)
				{
					booleanArray[i][j] = jsonBoolean.booleanValue();
				}
			}
		}		
		return booleanArray;
	}
	
	public boolean[][] nakijkenZelftoetsPending()
	{
		if (nakijkenZelftoetsPending == null)
		{
			return null;
		}
		
		int aantalActiviteiten = 0;
		int aantalOpdrachten = 0;
		
		aantalActiviteiten = nakijkenZelftoetsPending.size();
		
		for (int i = 0; i < nakijkenZelftoetsPending.size(); i++)
		{
			JSONArray array = nakijkenZelftoetsPending.get(i).isArray();
			aantalOpdrachten = Math.max(array.size(), aantalOpdrachten);
		}
		
		boolean[][] booleanArray = new boolean[aantalActiviteiten][aantalOpdrachten];
		
		for (int i = 0; i < nakijkenZelftoetsPending.size(); i++)
		{
			JSONArray array = nakijkenZelftoetsPending.get(i).isArray();
			for (int j = 0; j < array.size(); j++)
			{
				JSONBoolean jsonBoolean = array.get(j).isBoolean();
				if (jsonBoolean != null)
				{
					booleanArray[i][j] = jsonBoolean.booleanValue();
				}
			}
		}		
		return booleanArray;
	}
	
	public void setIsCorrectZelftoets(boolean[][] isCorrect)
	{
		JSONArray array = new JSONArray();

		for (int i = 0; i < isCorrect.length; i++)
		{
			boolean[] isCorrectPerActiviteit = isCorrect[i];
			Boolean[] booleanScores = new Boolean[isCorrectPerActiviteit.length];
			for (int j = 0; j < isCorrectPerActiviteit.length; j++)
			{
				booleanScores[j] = new Boolean(isCorrectPerActiviteit[j]);
			}
			array.set(i, JSONUtilities.toJSONArray(booleanScores));
		}

		this.isCorrectZelftoets = array;
		this.onsState.put(GOED_FOUT_ZELFTOETS, this.isCorrectZelftoets);
	}
	
	public void setNakijkenZelftoetsPending(boolean[][] nakijkenZelftoetsPending)
	{
		JSONArray array = new JSONArray();

		for (int i = 0; i < nakijkenZelftoetsPending.length; i++)
		{
			boolean[] nakijkenZelftoetsPendingPerActiviteit = nakijkenZelftoetsPending[i];
			Boolean[] booleanScores = new Boolean[nakijkenZelftoetsPendingPerActiviteit.length];
			for (int j = 0; j < nakijkenZelftoetsPendingPerActiviteit.length; j++)
			{
				booleanScores[j] = new Boolean(nakijkenZelftoetsPendingPerActiviteit[j]);
			}
			array.set(i, JSONUtilities.toJSONArray(booleanScores));
		}

		this.nakijkenZelftoetsPending = array;
		this.onsState.put(NAKIJKEN_ZELFTOETS_PENDING, this.nakijkenZelftoetsPending);
	}
	
	public boolean isTempotoetsVerlopen()
	{
		if (this.tempotoetsLocked == null) // when there haven't been any suspenddata yet
			return false;
		else
			return this.tempotoetsLocked.booleanValue();
	}

	/**
	 * Get the number of seconds left for the tempotoets.
	 * If there is no number available -1 is returned. 
	 * 
	 * @return
	 */
	public int getTempotoetsSecondsLeft()
	{
		if (this.tempotoetsSecondsLeft == null)
			return -1;
		else
			return (int) this.tempotoetsSecondsLeft.doubleValue();
	}

	HandlerRegistration registration;
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(CheckButton.AFRONDEN.equals(event.getCommand())) {
			if(registration == null) return;
			registration.removeHandler();
			registration = null;
			setSessionTimes();
			JSONObject reviewData = new JSONObject();
			reviewData.put("toetsLocked", JSONBoolean.getInstance(true));
			suspendData.put("reviewData", reviewData);
			setValue(SUSPEND_DATA, suspendData.toString());
			// START AFRONDEN
			logger.info("start afronden");
			api.Commit();
			setValue(COMPLETION_STATUS, COMPLETED);
			eindtoetsVerzegeld = true;
			DWOplayer.clientfactory.getEntryView().setReadonly(true);
		}
		
	}

	void setSessionTimes() {
		Date stopDate = new Date();
		long millis = stopDate.getTime() - startDate.getTime();
		String totalStr = getValue(TOTAL_TIME);
		long total = parse(totalStr);
		setValue(SESSION_TIME, format(millis));
		setValue(TOTAL_TIME, format(total + millis));
	}

}
