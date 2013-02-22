package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
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

/**
 * Class om suspend_data in en uit te pakken. JSON format, Javascript is hier
 * first-class citizen.
 * 
 * @author velth101
 * 
 */
public class Memento
{
	private static final String OPDR_CONT_STATES = "opdrContStates";
	private static final String ONS_STATE = "onsState";
	private static final String SUSPEND_DATA = "cmi.suspendData";
	private static final String SCORE_RAW = "cmi.score.raw";
	private static final String SESSION_TIME = "cmi.sessiontime";

	interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("data.txt")
		TextResource data();
	}

	static String TESTVALUE = Resources.INSTANCE.data().getText();
	private Scorm2004IF api;

	private JSONObject suspendData;
	private JSONObject onsState;
	private JSONArray opdrContStates;

	private String scoreRaw;
	private Date startDate = new Date();

	private Integer score;

	public Memento(Scorm2004IF api)
	{
		this.api = api;
		initialize();
		String value;
		//value = TESTVALUE;
		value = getValue(SUSPEND_DATA);
		scoreRaw = getValue(SCORE_RAW);
		try
		{
			suspendData = (JSONObject) JSONParser.parseStrict(value);
			onsState = (JSONObject) suspendData.get(ONS_STATE);
			opdrContStates = (JSONArray) onsState.get(OPDR_CONT_STATES);
		}
		catch (Exception e)
		{
			opdrContStates = new JSONArray();
			onsState = new JSONObject();
			onsState.put(OPDR_CONT_STATES, opdrContStates);
			suspendData = new JSONObject();
			suspendData.put(ONS_STATE, onsState);
		}
	}

	private String getValue(String key)
	{
		try
		{
			return api.GetValue(key);
		}
		catch (Exception e)
		{
			GWT.log("getValue", e);
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
			GWT.log("initialize Scorm API", e);
		}
	}

	public int getScore()
	{
		if (score == null)
		{
			score = new Integer(scoreRaw);
		}
		return score.intValue();
	}

	public void setScore(int score)
	{
		this.score = new Integer(score);
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
			GWT.log("setValue", e);
		}
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
					array.set(j, toJSONObject(ooo));
				}
			}
			opdrContStates.set(i, array);
		}
		flush();
	}

	private JSONObject toJSONObject(Map<String, Object> value)
	{
		JSONObject result = new JSONObject();
		for (Map.Entry<String, Object> entry : value.entrySet())
		{
			result.put(entry.getKey(), toJSONValue(entry.getValue()));
		}
		return result;
	}

	private JSONValue toJSONValue(Object value)
	{
		if (value instanceof JSONValue)
			return (JSONValue) value;
		if (value instanceof Number)
		{
			return new JSONNumber(((Number) value).doubleValue());
		}
		if (value instanceof Boolean)
		{
			return JSONBoolean.getInstance(((Boolean) value).booleanValue());
		}
		if (value instanceof String)
		{
			return new JSONString(value.toString());
		}
		if (value == null)
			return JSONNull.getInstance();

		if (value instanceof Collection)
		{
			return toJSONArray(((Collection) value).toArray());
		}
		if (value instanceof Object[])
		{
			return toJSONArray((Object[]) value);
		}
		if (value instanceof Map)
		{
			return toJSONObject((Map<String, Object>) value);
		}
		if (value instanceof boolean[])
		{
			return toJSONArray((boolean[]) value);
		}
		throw new IllegalArgumentException("unsupported class " + value.getClass());
		//return null;
	}

	private JSONValue toJSONArray(boolean[] objects)
	{
		JSONArray array = new JSONArray();
		for (int i = 0; i < objects.length; i++)
		{
			array.set(i, JSONBoolean.getInstance(objects[i]));
		}
		return array;
	}

	private JSONValue toJSONArray(Object[] objects)
	{
		JSONArray array = new JSONArray();
		for (int i = 0; i < objects.length; i++)
		{
			array.set(i, toJSONValue(objects[i]));
		}
		return array;
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
			for (int j = 0; j < oo.length; j++)
			{
				JSONValue value = array.get(j);
				oo[j] = value == null ? null : fromJSONObject(value.isObject());
			}
		}
		return o;
	}

	private HashMap<String, Object> fromJSONObject(JSONObject object)
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		if (object != null)
		{
			Set<String> keys = object.keySet();
			for (String key : keys)
			{
				JSONValue value = object.get(key);
				result.put(key, fromJSONValue(value));
			}

		}
		return result;
	}

	private Object[] fromJSONArray(JSONArray array)
	{
		Object[] result = new Object[array.size()];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = fromJSONValue(array.get(i));
		}
		return result;
	}

	private Object fromJSONValue(JSONValue value)
	{
		if (value.isObject() != null)
			return fromJSONObject(value.isObject());
		else if (value.isBoolean() != null)
			return value.isBoolean().booleanValue();
		else if (value.isNumber() != null)
			return value.isNumber().doubleValue();
		else if (value.isString() != null)
			return value.isString().stringValue();
		else if (value.isArray() != null)
			return fromJSONArray(value.isArray());
		else
			// if(value.isNull())
			return null;
	}

	public void flush()
	{
		System.out.println("START SUSPENDDATA-----------");
		System.out.println(suspendData.toString());
		System.out.println("END SUSPENDDATA-----------");
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
		Date stopDate = new Date();
		long millis = stopDate.getTime() - startDate.getTime();
		setValue(SESSION_TIME, format(millis));
		flush();
		try
		{
			api.Terminate();
		}
		catch (Exception e)
		{
		}
	}

	private String format(long millis)
	{
		return "PT" + millis / 1000.0 + "S";
	}

	public static String[] toStringArray(Object object)
	{
		if (object instanceof String[] || object == null)
			return (String[]) object;
		if (object instanceof Object[])
		{
			Object[] objects = (Object[]) object;
			String[] strings = new String[objects.length];
			System.arraycopy(objects, 0, strings, 0, objects.length); // assume object array contains Strings
			return strings;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> toArrayList(Object object)
	{
		if (object instanceof List || object == null)
			return (List<Object>) object;
		if (object instanceof Object[])
		{
			Object[] objects = (Object[]) object;
			return Arrays.asList(objects);
		}
		return null;
	}

}
