package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;

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

/**
 * Class om suspend_data in en uit te pakken. JSON format, Javascript is hier
 * first-class citizen.
 * 
 * @author velth101
 * 
 */
public class Memento implements ClosingHandler, CloseHandler<Window>
{
	static Memento _instance;
	static private Logger logger = Logger.getLogger("Memento");

	static public native void instalOnBeforeUnload() /*-{
		$wnd.onbeforeunload = @nl.uu.fi.dwo.mobile.client.sco.Memento::unload();
		$wnd.onunload = @nl.uu.fi.dwo.mobile.client.sco.Memento::unload();
	}-*/;

	static void unload()
	{
		//Window.alert("unload");
		if (_instance != null)
			_instance.close();
	}

	private static final String OPDR_CONT_STATES = "opdrContStates";
	private static final String STRAFPUNTEN = "strafpunten"; // optional!
	private static final String GOED_FOUT = "orGoedFout";
	private static final String SCORES = "orScores"; // TODO correct name? getPagina score gebruikt deze naam
	private static final String ONS_STATE = "onsState";
	static final String SUSPEND_DATA = "cmi.suspend_data";
	static final String SCORE_RAW = "cmi.score.raw";
	static final String COMPLETION_STATUS = "cmi.completion_status";
	private static final String SESSION_TIME = "cmi.session_time";
	private static final String TOTAL_TIME = "cmi.total_time";
	
	static final String COMPLETE = "completed";
	static final String INCOMPLETE = "incomplete";

	private Scorm2004IF api;

	private JSONObject suspendData;
	private JSONObject onsState;
	private JSONArray opdrContStates, opdrStrafpunten, opdrGoedFout, opdrScores;

	private String scoreRaw;
	private Date startDate = new Date();

	private Number score;

	public Memento(Scorm2004IF api)
	{
		this.api = api;
		_instance = this;
		Window.addWindowClosingHandler(this);
		Window.addCloseHandler(this);
		initialize();
		String value;
		value = getValue(SUSPEND_DATA);
		//value = TESTVALUE;
		scoreRaw = getValue(SCORE_RAW);
		try
		{
			suspendData = (JSONObject) JSONParser.parseStrict(value);
			onsState = (JSONObject) suspendData.get(ONS_STATE);
			opdrContStates = (JSONArray) onsState.get(OPDR_CONT_STATES);
			opdrStrafpunten = (JSONArray) onsState.get(STRAFPUNTEN);
			opdrGoedFout  = (JSONArray) onsState.get(GOED_FOUT);
			opdrScores    = (JSONArray) onsState.get(SCORES);
		}
		catch (Exception e)
		{
			opdrContStates = new JSONArray();
			onsState = new JSONObject();
			onsState.put(OPDR_CONT_STATES, opdrContStates);
			suspendData = new JSONObject();
			suspendData.put(ONS_STATE, onsState);
		}

		instalOnBeforeUnload();
	}

	private String getValue(String key)
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

	private void setValue(String key, String value)
	{
		try
		{
			api.SetValue(key, value);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE,"setValue", e);
		}
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
		JSONNumber number = array.get(j).isNumber();
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
		flush();
	}

//	private JSONValue toJSONObject(Map<String, Object> value)
//	{
//		if (value != null)
//		{
//			JSONObject result = new JSONObject();
//			for (Map.Entry<String, Object> entry : value.entrySet())
//			{
//				result.put(entry.getKey(), toJSONValue(entry.getValue()));
//			}
//			return result;
//		}
//		else
//			return JSONNull.getInstance();
//	}
//
//	private JSONValue toJSONValue(Object value)
//	{
//		if (value instanceof JSONValue)
//			return (JSONValue) value;
//		if (value instanceof Number)
//		{
//			return new JSONNumber(((Number) value).doubleValue());
//		}
//		if (value instanceof Boolean)
//		{
//			return JSONBoolean.getInstance(((Boolean) value).booleanValue());
//		}
//		if (value instanceof String)
//		{
//			return new JSONString(value.toString());
//		}
//		if (value == null)
//			return JSONNull.getInstance();
//
//		if (value instanceof Collection)
//		{
//			return toJSONArray(((Collection) value).toArray());
//		}
//		if (value instanceof Object[])
//		{
//			return toJSONArray((Object[]) value);
//		}
//		if (value instanceof Map)
//		{
//			return toJSONObject((Map<String, Object>) value);
//		}
//		if (value instanceof boolean[])
//		{
//			return toJSONArray((boolean[]) value);
//		}
//		throw new IllegalArgumentException("unsupported class " + value.getClass());
//		//return null;
//	}
//
//	private JSONValue toJSONArray(boolean[] objects)
//	{
//		JSONArray array = new JSONArray();
//		for (int i = 0; i < objects.length; i++)
//		{
//			array.set(i, JSONBoolean.getInstance(objects[i]));
//		}
//		return array;
//	}
//
//	private JSONValue toJSONArray(Object[] objects)
//	{
//		JSONArray array = new JSONArray();
//		for (int i = 0; i < objects.length; i++)
//		{
//			array.set(i, toJSONValue(objects[i]));
//		}
//		return array;
//	}

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
			for (int j = 0; j < oo.length; j++)
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
		setValue(SUSPEND_DATA, suspendData.toString());
		try
		{
			api.Commit();
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
		runner.run();
		Date stopDate = new Date();
		long millis = stopDate.getTime() - startDate.getTime();
		String totalStr = getValue(TOTAL_TIME);
		long total = parse(totalStr);
		setValue(SESSION_TIME, format(millis));
		setValue(TOTAL_TIME, format(total+millis));
		try
		{
			api.Terminate();
			api = null;
		}
		catch (Exception e)
		{
		}
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
		onsState.put("opdrachtNr", new JSONNumber(currentOpdracht));
	}
	
	public int getCurrentOpdracht() {
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
		api.SetValue(COMPLETION_STATUS, complete?COMPLETE:INCOMPLETE);
	}

}
