package nl.uu.fi.dwo.interaction.client.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.uu.fi.dwo.interaction.client.JSONUtilities.*;

public class ObjectMapImpl implements ObjectMap {

	private Map<String,? extends Object> map;

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	public Object get(String key){
		return map.get(key);
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public ObjectMapImpl(Map<String, ? extends Object> map) {
		super();
		this.map = map;
	}

	@Override
	public int getInt(String key) {
		return toInt(get(key));
	}

	@Override
	public boolean getBoolean(String key) {
		return Boolean.TRUE.equals(get(key));
	}

	@Override
	public List<String> getStringList(String key) {
		return Arrays.asList(toStringArray(get(key)));
	}

	@Override
	public List<Integer> getIntegerList(String key) {
		List<Integer> result = new ArrayList<Integer>();
		List<?> values = toArrayList(get(key));
		for (Object object : values) {
			if(object != null)
				result.add( toInt(object) );
			else
				result.add(null);
		}
		return result;
	}

	@Override
	public List<Boolean> getBooleanList(String key) {
		List<Boolean> result = new ArrayList<Boolean>();
		List<?> values = toArrayList(get(key));
		for (Object object : values) {
			if(object != null)
				result.add( toBoolean(object) );
			else
				result.add(null);
		}
		return result;
	}

	private Boolean toBoolean(Object object) {
		if(object instanceof Boolean)
			return (Boolean) object;
		return Boolean.FALSE;
	}

	@Override
	public Map<String, Object> getMap(String key) {
		return (Map<String,Object>) get(key);
	}

	@Override
	public List<Map<String, Object>> getMapList(String key) {
		List array = toArrayList(get(key));
		return array;
	}

	@Override
	public double getDouble(String key) {
		Object n = get(key);
		if(n != null)
			return ((Number)n).doubleValue();
		return Double.NaN;
	}

	@Override
	public String getString(String key) {
		return (String) get(key);
	}

	@Override
	public List<Double> getDoubleList(String key) {
		List<Double> result = new ArrayList<Double>();
		List<?> values = toArrayList(get(key));
		for (Object object : values) {
			if(object != null)
				result.add( toDouble(object) );
			else
				result.add(null);
		}
		return result;
	}

	private Double toDouble(Object object) {
		if(object instanceof Double)
			return (Double) object;
		return null;
	}

	@Override
	public double[] getDoubleArray(String key) {
		List<Double> dd = getDoubleList(key);
		double[] result = new double[dd.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = dd.get(i).doubleValue();
		}
		return result;
	}

	@Override
	public int[] getIntArray(String key) {
		List<Integer> dd = getIntegerList(key);
		int[] result = new int[dd.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = dd.get(i).intValue();
		}
		return result;
	}

	@Override
	public String[] getStringArray(String key) {
		return toStringArray(get(key));
	}

	@Override
	public boolean[] getBooleanArray(String key) {
		List<Boolean> dd = getBooleanList(key);
		boolean[] result = new boolean[dd.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = dd.get(i).booleanValue();
		}
		return result;
	}
	
	
}
