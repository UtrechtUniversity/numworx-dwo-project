package nl.uu.fi.dwo.interaction.client.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSONObjectMapImpl extends HashMap<String, Object> implements ObjectMap {
	
	private final JSONObject object;
	
	public JSONObject unwrap() {
		return object;
	}
	
	public String toString() {
		return object.toString();
	}
	
	@Override
	public int size() {
		return object.size();
	}

	@Override
	public boolean isEmpty() {
		return object.size() == 0;
	}

	public JSONObjectMapImpl(JSONObject object) {
		super();
		this.object = object;
	}

	@Override
	public boolean containsKey(String key) {
		return object.containsKey(key);
	}

	public boolean containsKey(Object key) {
		if(key instanceof String)
			return containsKey( (String) key);
		return false;
	}

	private JSONValue get0(String key) {
		return object.get(key);
	}
	
	@Override
	public Object get(String key) {
		JSONValue value = get0(key);
		return toObject(value);
	}

	static Object toObject(JSONValue value) {
		if(value == null)
			return null;
		if(value.isObject() != null)
			return new JSONObjectMapImpl(value.isObject());
		if(value.isNumber() != null)
			return Double.valueOf(value.isNumber().doubleValue());
		if(value.isBoolean() != null)
			return Boolean.valueOf(value.isBoolean().booleanValue());
		if(value.isString() != null)
			return value.isString().stringValue();
		if(value.isArray() != null) 
			return toObjectArray(value.isArray());
		if(value.isNull() != null) 
			return null;
		return value;
	}

	static Object[] toObjectArray(JSONArray array) {
		Object[] result = new Object[array.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = toObject(array.get(i));
		}
		return result;
	}

	@Override
	public Object get(Object key) {
		if(key instanceof String)
			return get((String)key);
		return null;
	}

	@Override
	public int getInt(String key) {
		return (int) get0(key).isNumber().doubleValue();
	}

	@Override
	public double getDouble(String key) {
		return get0(key).isNumber().doubleValue();
	}

	@Override
	public boolean getBoolean(String key) {
		return get0(key).isBoolean().booleanValue();
	}

	@Override
	public String getString(String key) {
		return get0(key).isString().stringValue();
	}

	@Override
	public Map<String, Object> getMap(String key) {
		JSONValue get0 = get0(key);
		if(get0 == null) return null;
		JSONObject o = get0.isObject();
		if(o == null) return null;
		return new JSONObjectMapImpl(o);
	}

	@Override
	public List<Object> getList(String key) {
		JSONArray o = get0(key).isArray();
		return new JSONObjectListImpl(o);
	}

	@Override
	public ObjectMap getObjectMap(String key) {
		JSONValue get0 = get0(key);
		if(get0 == null) return null;
		JSONObject o = get0.isObject();
		if(o == null) return null;
		return new JSONObjectMapImpl(o);
	}

	@Override
	public ObjectList getObjectList(String key) {
		JSONArray o = get0(key).isArray();
		return new JSONObjectListImpl(o);
	}

	@Override
	public List<String> getStringList(String key) {
		return Arrays.asList( getStringArray(key) );
	}

	@Override
	public List<Integer> getIntegerList(String key) {
		JSONArray array = get0(key).isArray();		
		return toIntegerList(array);
	}

	static List<Integer> toIntegerList(JSONArray array) {
		int size = array.size();
		ArrayList<Integer> result = new ArrayList<Integer>(size);
		for(int i = 0; i < size; i++ ) {
			result.add((int) array.get(i).isNumber().doubleValue());
		}
		return result;
	}

	static List<Double> toDoubleList(JSONArray array) {
		int size = array.size();
		ArrayList<Double> result = new ArrayList<Double>(size);
		for(int i = 0; i < size; i++ ) {
			result.add( array.get(i).isNumber().doubleValue());
		}
		return result;
	}

	static double[] toDoubleArray(JSONArray array) {
		int size = array.size();
		double[] result = new double[size];
		for(int i = 0; i < size; i++ ) {
			JSONNumber number = array.get(i).isNumber();
			if(number != null)
				result[i] = number.doubleValue();
			else
				result[i] = Double.NaN;
		}
		return result;
	}

	static boolean[] toBooleanArray(JSONArray array) {
		int size = array.size();
		boolean[] result = new boolean[size];
		for(int i = 0; i < size; i++ ) {
			result[i] = array.get(i).isBoolean().booleanValue();
		}
		return result;
	}

	static int[] toIntArray(JSONArray array) {
		int size = array.size();
		int[] result = new int[size];
		for(int i = 0; i < size; i++ ) {
			result[i] = (int) array.get(i).isNumber().doubleValue();
		}
		return result;
	}

	static List<Boolean> toBooleanList(JSONArray array) {
		int size = array.size();
		ArrayList<Boolean> result = new ArrayList<Boolean>(size);
		for(int i = 0; i < size; i++ ) {
			result.add(array.get(i).isBoolean().booleanValue());
		}
		return result;
	}

	@Override
	public List<Boolean> getBooleanList(String key) {
		JSONArray array = get0(key).isArray();		
		return toBooleanList(array);
	}

	@Override
	public List<Double> getDoubleList(String key) {
		JSONArray array = get0(key).isArray();		
		return toDoubleList(array);
	}

	static List<Map<String,Object>> toMapList(JSONArray array) {
		int size = array.size();
		ArrayList<Map<String,Object>> result = new ArrayList<Map<String,Object>>(size);
		for(int i = 0; i < size; i++ ) {
			JSONObject o = array.get(i).isObject();
			result.add(o== null ? null : new JSONObjectMapImpl(o));
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getMapList(String key) {
		JSONArray array = get0(key).isArray();		
		return toMapList(array);
	}

	@Override
	public double[] getDoubleArray(String key) {
		JSONArray array = get0(key).isArray();		
		return toDoubleArray(array);
	}

	@Override
	public int[] getIntArray(String key) {
		JSONArray array = get0(key).isArray();
		return toIntArray(array);
	}

	@Override
	public String[] getStringArray(String key) {
		JSONArray array = get0(key).isArray();
		return toStringArray(array);
	}

	static String[] toStringArray(JSONArray array) {
		String[] result = new String[array.size()];
		for (int i = 0; i < result.length; i++) {
			JSONString string = array.get(i).isString();
			if(string != null)
				result[i] = string.stringValue();
		}
		return result;
	}

	@Override
	public boolean[] getBooleanArray(String key) {
		JSONArray array = get0(key).isArray();
		return toBooleanArray(array);
	}

	@Override
	public Set<String> keySet() {
		return object.keySet();
	}

}
