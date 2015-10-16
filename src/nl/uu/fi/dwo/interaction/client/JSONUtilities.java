package nl.uu.fi.dwo.interaction.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.interaction.client.json.JSONObjectListImpl;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectListImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.json.ObjectMapImpl;

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


	public static JSONValue toJSONObject(Map<String, ? extends Object> value)
	{
		if (value != null)
		{
			if(value instanceof JSONObjectMapImpl)
				return ((JSONObjectMapImpl) value).unwrap();
			
			JSONObject result = new JSONObject();
			for (Map.Entry<String, ? extends Object> entry : value.entrySet())
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

		if (value instanceof JSONObjectListImpl)
			return ((JSONObjectListImpl) value).unwrap();

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
		if (value instanceof double[]) 
		{
			return toJSONArray((double[]) value);
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

	private static JSONValue toJSONArray(double[] objects) {
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
		if (object instanceof Collection)
			return new ArrayList<Object>( (Collection<?>) object );
// FIXME fout voor primitive[] moet dan een List<Primitieve> komen.
		return null;
	}
	
	public static int toInt(Object object) {
		if( object instanceof Number)
			return ((Number)object).intValue();
		throw new NullPointerException();
	}
	
	public static ObjectMap wrapMap(Map<String, ? > map) {
		if(map instanceof ObjectMap) return (ObjectMap) map;
		if(map == null) return null;
		return new ObjectMapImpl(map);
	}
	

	public static JSONObjectMapImpl wrapMap(JSONObject object) {
		if(object == null) return null;
		return new JSONObjectMapImpl(object);
	}
	
	public static ObjectList wrapList(List<?> list) {
		if( list instanceof ObjectList) return (ObjectList) list;
		if( list == null) return null;
		return new ObjectListImpl(list);
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
		if( object instanceof Collection) {
			Collection c = (Collection) object;
			String[] strings = new String[c.size()];
			c.toArray(strings);
			return strings;
		}
		
		return null;
	}


	public static JSONValue toJSONObject(ObjectMap innerMap) {
		if(innerMap instanceof JSONObjectMapImpl)
			return ((JSONObjectMapImpl) innerMap).unwrap();
		if(innerMap instanceof ObjectMapImpl) 
			return toJSONObject( ((ObjectMapImpl) innerMap).unwrap() );
		return JSONNull.getInstance();
	}
	
	

}
