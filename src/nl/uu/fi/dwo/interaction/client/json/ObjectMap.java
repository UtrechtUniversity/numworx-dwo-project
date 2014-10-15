package nl.uu.fi.dwo.interaction.client.json;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ObjectMap {

	boolean containsKey(String key);
	@Deprecated
	Object get(String key);
	
// specialized getters
	
	int getInt(String key);
	double getDouble(String key);
	boolean getBoolean(String key);
	String  getString(String key);
	
	Map<String,Object> getMap(String key);
	List<Object> getList(String key);
	
	ObjectMap getObjectMap(String key);
	ObjectList getObjectList(String key);

	List<String> getStringList(String key);
	List<Integer> getIntegerList(String key);
	List<Boolean> getBooleanList(String key);
	List<Double>  getDoubleList(String key);
	
	List<Map<String,Object>> getMapList(String key);
	
	double[] getDoubleArray(String key);
	int[] getIntArray(String key);
	String[] getStringArray(String key);
	boolean[] getBooleanArray(String key);
	
	boolean isEmpty();
	Set<String> keySet();
	
}
