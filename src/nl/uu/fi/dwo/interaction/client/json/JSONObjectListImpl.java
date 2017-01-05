package nl.uu.fi.dwo.interaction.client.json;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JSONObjectListImpl extends AbstractList<Object> implements ObjectList {

	private JSONArray list;

	public JSONArray unwrap() {
		return list;
	}
	
	public String toString() {
		return list.toString();
	}
	
	public JSONObjectListImpl(JSONArray o) {
		this.list = o;
	}

	public int size() {
		return list.size();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public Object get(int key) {
		JSONValue v = get0(key);
		return JSONObjectMapImpl.toObject(v);
	}
	
	private JSONValue get0(int key) {
		return list.get(key);
	}
	
	@Override
	public int getInt(int key) {
		return (int) getDouble(key);
	}

	@Override
	public double getDouble(int key) {
		return get0(key).isNumber().doubleValue();
	}

	@Override
	public boolean getBoolean(int key) {
		return get0(key).isBoolean().booleanValue();
	}

	@Override
	public String getString(int key) {
		return get0(key).isString().stringValue();
	}

	@Override
	public Map<String, Object> getMap(int key) {
		JSONObject o = get0(key).isObject();
		if(o == null) return null;
		return new JSONObjectMapImpl(o);
	}

	@Override
	public List<Object> getList(int key) {
		JSONArray o = get0(key).isArray();
		if(o == null) return null;
		return new JSONObjectListImpl(o);
	}

	@Override
	public ObjectMap getObjectMap(int key) {
		JSONObject o = get0(key).isObject();
		if(o == null) return null;
		return new JSONObjectMapImpl(o);
	}

	@Override
	public ObjectList getObjectList(int key) {
		JSONArray o = get0(key).isArray();
		if(o == null) return null;
		return new JSONObjectListImpl(o);

	}

	@Override
	public List<String> getStringList(int key) {
		return Arrays.asList(getStringArray(key));
	}

	@Override
	public List<Integer> getIntegerList(int key) {
		return JSONObjectMapImpl.toIntegerList(get0(key).isArray());
	}

	@Override
	public List<Boolean> getBooleanList(int key) {
		JSONArray array = get0(key).isArray();		
		return JSONObjectMapImpl.toBooleanList(array);
	}

	@Override
	public List<Double> getDoubleList(int key) {
		JSONArray array = get0(key).isArray();		
		return JSONObjectMapImpl.toDoubleList(array);
	}

	@Override
	public double[] getDoubleArray(int key) {
		JSONArray array = get0(key).isArray();		
		return JSONObjectMapImpl.toDoubleArray(array);
	}

	@Override
	public int[] getIntArray(int key) {
		JSONArray array = get0(key).isArray();		
		return JSONObjectMapImpl.toIntArray(array);
	}

	@Override
	public String[] getStringArray(int key) {
		JSONArray array = get0(key).isArray();
		return JSONObjectMapImpl.toStringArray(array);
	}

	@Override
	public boolean[] getBooleanArray(int key) {
		JSONArray array = get0(key).isArray();		
		return JSONObjectMapImpl.toBooleanArray(array);
	}

}
