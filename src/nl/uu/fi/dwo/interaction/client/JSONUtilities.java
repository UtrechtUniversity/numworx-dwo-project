package nl.uu.fi.dwo.interaction.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * Utility class voor JSON <-> Java primitieven
 * @author velth101
 *
 */
public class JSONUtilities {
	private JSONUtilities() {}


	public static JSONValue toJSONObject(Map<String, Object> value)
	{
		if (value != null)
		{
			JSONObject result = new JSONObject();
			for (Map.Entry<String, Object> entry : value.entrySet())
			{
				result.put(entry.getKey(), toJSONValue(entry.getValue()));
			}
			return result;
		}
		else
			return JSONNull.getInstance();
	}

	public static JSONValue toJSONValue(Object value)
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
		if (value instanceof int[]) 
		{
			return toJSONArray((int[]) value);
		}
		throw new IllegalArgumentException("unsupported class " + value.getClass());
		//return null;
	}

	private static JSONValue toJSONArray(int[] objects) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < objects.length; i++)
		{
			array.set(i, new JSONNumber(objects[i]));
		}
		return array;
	}


	public static JSONValue toJSONArray(boolean[] objects)
	{
		JSONArray array = new JSONArray();
		for (int i = 0; i < objects.length; i++)
		{
			array.set(i, JSONBoolean.getInstance(objects[i]));
		}
		return array;
	}

	public static JSONValue toJSONArray(Object[] objects)
	{
		JSONArray array = new JSONArray();
		for (int i = 0; i < objects.length; i++)
		{
			array.set(i, toJSONValue(objects[i]));
		}
		return array;
	}

	public static HashMap<String, Object> fromJSONObject(JSONObject object)
	{
		if (object != null)
		{
			HashMap<String, Object> result = new HashMap<String, Object>();
			Set<String> keys = object.keySet();
			for (String key : keys)
			{
				JSONValue value = object.get(key);
				result.put(key, fromJSONValue(value));
			}
			return result;
		}
		return null;
	}

	public static Object[] fromJSONArray(JSONArray array)
	{
		Object[] result = new Object[array.size()];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = fromJSONValue(array.get(i));
		}
		return result;
	}

	public static Object fromJSONValue(JSONValue value)
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

}
