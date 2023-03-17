package nl.numworx.gwtpatch.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import nl.numworx.gwtpatch.client.GWTPatch.Builder;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectListImpl;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class JSONBuilder implements Builder {

	public JSONBuilder() {
	}

	@Override
	public Map<String, Object> createMap() {
		return new JSONObjectMapImpl(new JSONObject());
	}

	@Override
	public List<Object> createList(int size) {
		return new JSONObjectListImpl(new JSONArray()) {

			@Override
			public boolean add(Object e) {
				JSONValue value = JSONUtilities.toJSONValue(e);
				unwrap().set(size(), value);
				return true;
			} };
	}

	@Override
	public ObjectMap toObjectMap(Map<String, Object> map) {
		return (ObjectMap) map;
	}

	@Override
	public ObjectList toObjectList(List<Object> list) {
		return (ObjectList) list;
	}
	
}