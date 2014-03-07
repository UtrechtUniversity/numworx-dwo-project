package nl.uu.fi.dwo.interaction.client.json;

import java.util.List;
import java.util.Map;
import static nl.uu.fi.dwo.interaction.client.JSONUtilities.*;

public class ObjectListImpl implements ObjectList {
	
	private List<?> list;
	
	public ObjectListImpl(List<?> list) {
		this.list = list;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object get(int i) {
		return list.get(i);
	}

	@Override
	public int getInt(int key) {
		return toInt(get(key));
	}

	@Override
	public double getDouble(int key) {
		ObjectMapImpl.toDouble(get(key));
		return 0;
	}

	@Override
	public boolean getBoolean(int key) {
		return ObjectMapImpl.toBoolean(get(key));
	}

	@Override
	public String getString(int key) {
		return (String) get(key);
	}

	@Override
	public Map<String, Object> getMap(int key) {
		return (Map<String,Object>) get(key);
	}

	@Override
	public List<Object> getList(int key) {
		return toArrayList(get(key));
	}

	@Override
	public ObjectMap getObjectMap(int key) {
		return wrapMap(getMap(key));
	}

	@Override
	public ObjectList getObjectList(int key) {
		return wrapList(get(key));
	}

	static  ObjectList wrapList(Object object) {
		return new ObjectListImpl( (List<Object>) object);
	}

	@Override
	public List<String> getStringList(int key) {
		List list = getList(key);
		return list;
	}

	@Override
	public List<Integer> getIntegerList(int key) {
		List list = getList(key);
		return ObjectMapImpl.toIntegerList(list);
	}

	@Override
	public List<Boolean> getBooleanList(int key) {
		List list = getList(key);
		return list;
	}

	@Override
	public List<Double> getDoubleList(int key) {
		List list = getList(key);
		return list;
	}

	@Override
	public double[] getDoubleArray(int key) {
		Object o = get(key);
		if(o instanceof double[] || o == null)
			return (double[])o;
		List<Double> dd = getDoubleList(key);
		return ObjectMapImpl.toDoubleArray(dd);
	}

	@Override
	public int[] getIntArray(int key) {
		Object o = get(key);
		if(o instanceof int[] || o == null )
			return (int[]) o;
		List<Integer> dd = getIntegerList(key);
		int[] result = new int[dd.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = dd.get(i).intValue();
		}
		return result;
	}

	@Override
	public String[] getStringArray(int key) {
		return toStringArray(get(key));	}

	@Override
	public boolean[] getBooleanArray(int key) {
		Object o = get(key);
		if( o instanceof boolean[] || o == null) 
			return (boolean[])o;		
		List<Boolean> dd = getBooleanList(key);
		boolean[] result = new boolean[dd.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = dd.get(i).booleanValue();
		}
		return result;
	}

}
