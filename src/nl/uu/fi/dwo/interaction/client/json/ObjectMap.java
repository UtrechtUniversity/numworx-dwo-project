package nl.uu.fi.dwo.interaction.client.json;

import java.util.List;
import java.util.Map;

public interface ObjectMap {

	boolean containsKey(String key);
	@Deprecated
	Object get(String key);
	
// specialized getters
	
	int getInt(String key);
	boolean getBoolean(String key);
	Map<String,Object> getMap(String key);

	List<String> getStringList(String key);
	List<Integer> getIntegerList(String key);
	List<Boolean> getBooleanList(String key);
	
	List<Map<String,Object>> getMapList(String key);
	
}
