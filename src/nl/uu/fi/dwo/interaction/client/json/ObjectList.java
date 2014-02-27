package nl.uu.fi.dwo.interaction.client.json;

import java.util.List;
import java.util.Map;

public interface ObjectList {
	int size();
	@Deprecated
	Object get(int i);
	
	// specialized getters
	
	int getInt(int key);
	double getDouble(int key);
	boolean getBoolean(int key);
	String  getString(int key);
	
	Map<String,Object> getMap(int key);
	List<Object> getList(int key);
	
	ObjectMap  getObjectMap(int key);
	ObjectList getObjectList(int key);

	List<String> getStringList(int key);
	List<Integer> getIntegerList(int key);
	List<Boolean> getBooleanList(int key);
	List<Double>  getDoubleList(int key);
	
	
	double[] getDoubleArray(int key);
	int[] getIntArray(int key);
	String[] getStringArray(int key);
	boolean[] getBooleanArray(int key);
	
		

}
