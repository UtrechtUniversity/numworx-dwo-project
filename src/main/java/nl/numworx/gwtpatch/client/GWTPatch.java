package nl.numworx.gwtpatch.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectListImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class GWTPatch {

	final Logger LOG = Logger.getLogger(getClass().getName());
	
	public interface Builder {
		Map<String,Object> createMap();
		List<Object> createList(int size);

		default ObjectMap toObjectMap(Map<String,Object> map) {
			return JSONUtilities.wrapMap(map);
		}
		default ObjectList toObjectList(List<Object> list) {
			return JSONUtilities.wrapList(list);
		}
	}
	
	static class DefaultBuilder implements Builder {

		@Override
		public Map<String, Object> createMap() {
			return new TreeMap<String,Object>();
		}

		@Override
		public List<Object> createList(int size) {
			return new ArrayList<Object>(size);
		}
	};
	
	public String createPatch(String old, String now) {
		JSONObject o = JSONParser.parseLenient(old).isObject();
		JSONObject n = JSONParser.parseLenient(now).isObject();
		ObjectList result = createDiff(o,n);
		return JSONUtilities.toJSONValue(result).toString();
	}
	
	
	private DiffFactory factory;
	
	public GWTPatch() {
		factory = new DiffFactory(new DefaultBuilder());
	}
	
	public GWTPatch(Builder builder) {
		factory = new DiffFactory(builder);
	}

	protected ObjectList createDiff(Object old, Object now ) {
		createDiff(old, now, "");
		ObjectList patches;
		patches = factory.getPatches();
		return patches;
	}

	private void createDiff(Object old, Object now, String pointer) {
		if( old == null) {
			if(now != null) {
				factory.addModify(pointer, now);
				return;
			}
			return;
		}
		try {
			if( now != null && old.equals(now)) // fails in debugger
				return;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "createDiff equals", e);
		}
		ObjectMap obj = asMap(old);
		if(obj != null) {
			ObjectMap nowobj = asMap(now);
			if(nowobj == null) {
				factory.addModify(pointer, now);
				return;
			} else {
				createDiff(obj, nowobj, pointer);
				return;
			}
		} else {
			ObjectList arr = asList(old);
			if (arr != null) {
				ObjectList nowarr = asList(now);
				if (nowarr == null) {
					factory.addModify(pointer, now);
					return;
				} else {
					createDiff(arr, nowarr, pointer);
					return;
				}	
			}
		}
		factory.addModify(pointer, now);
	}
	
	private ObjectList asList(Object o) {
		if(o instanceof ObjectList)
			return (ObjectList) o;
		if(o instanceof List) {
			return JSONUtilities.wrapList((List<?>)o);
		}
		if (o instanceof JSONArray) 
			return new JSONObjectListImpl((JSONArray) o); // FIXME missing in JSONUtilities
		if(o instanceof Object[]) 
			return JSONUtilities.wrapList((Object[]) o);
		return null;
	}

	private ObjectMap asMap(Object o) {
		if(o instanceof ObjectMap) {
			return (ObjectMap) o;
		}
		if (o instanceof Map) {
			return JSONUtilities.wrapMap((Map)o);
		}
		if (o instanceof JSONObject) {
			return JSONUtilities.wrapMap((JSONObject) o);
		}
		return null;
	}

	private void createDiff(ObjectMap old, ObjectMap now, String pointer) {
		Set<String> oldset = old.keySet();
		Set<String> nowset = now.keySet();
		Set<String> rmset = new HashSet<String>(oldset); rmset.removeAll(nowset);
		Set<String> addset = new HashSet<String>(nowset); addset.removeAll(oldset);
		Set<String> diffset = new HashSet<String>(oldset); diffset.retainAll(nowset);
		for(String key: rmset) factory.addRemove(combine(pointer, key));
		for(String key: addset) factory.addAdd(combine(pointer, key), now.get(key));
		for(String key: diffset) {
			String path = combine(pointer, key);
			Object o = old.get(key);
			Object n = now.get(key);
			createDiff(o, n, path);
		}
	}
	
	private void createDiff(ObjectList old, ObjectList now, String pointer) {
		int oldsize = old.size();
		int nowsize = now.size();
		if(oldsize > nowsize) {
			for(int i = oldsize-1 ; i >= nowsize; i--) { // BACKWARDS!
				factory.addRemove(combine(pointer, i));
			}
		} else if (oldsize < nowsize) {
			for (int i = oldsize; i < nowsize; i++ ) {
				factory.addAdd(combine(pointer, i), now.get(i));
			}
		}
		int size = Math.min(oldsize, nowsize);
		for(int i = 0; i < size; i++) {
			String path = combine(pointer, i);
			createDiff(old.get(i), now.get(i), path);
		}
	}

	private String combine(String pointer, int i) {
		return pointer + "/" + i;
	}

	private String combine(String pointer, String key) {
		// done / -> ~1 ~ -> ~0
		return pointer + "/" + encode(key);
	}

  private String encode(String key) {    
    return key.replace("~", "~0").replace("/", "~1");
  }
}
