package nl.uu.fi.dwo.interaction.client.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JSONObjectMapImpl extends HashMap<String, Object> implements ObjectMap {
	
	private final JSONObject object;
	
	

	public JSONObjectMapImpl(JSONObject object) {
		super();
		this.object = object;
	}

	@Override
	public boolean containsKey(String key) {
		return object.containsKey(key);
	}

	private JSONValue get0(String key) {
		return object.get(key);
	}
	
	@Override
	public Object get(String key) {
		return get0(key);
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
		JSONObject o = get0(key).isObject();
		return new JSONObjectMapImpl(o);
	}

	@Override
	public List<Object> getList(String key) {
		JSONArray o = get0(key).isArray();
		return new JSONObjectListImpl(o);
	}

	@Override
	public ObjectMap getObjectMap(String key) {
		JSONObject o = get0(key).isObject();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> getBooleanList(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Double> getDoubleList(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getMapList(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDoubleArray(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getIntArray(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getStringArray(String key) {
		JSONArray array = get0(key).isArray();
		String[] result = new String[array.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = array.get(i).isString().stringValue();
		}
		return result;
	}

	@Override
	public boolean[] getBooleanArray(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
