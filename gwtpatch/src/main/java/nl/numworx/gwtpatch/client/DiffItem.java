package nl.numworx.gwtpatch.client;

import java.util.Map;

import nl.numworx.gwtpatch.client.GWTPatch.Builder;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

class DiffItem {
	
	String op;
	Object value;
	String path;

	ObjectMap toObjectMap(Builder builder) {		
		Map<String, Object> map = builder.createMap();
		map.put("op", op);
		map.put("path", path);
		map.put("value", value);
		return builder.toObjectMap(map);
	}
	
	void setOp(String op) {
		this.op = op;
	}

	void setValue(Object object) {
		value = object;
	}

	void setPath(String path) {
		this.path = path;
	}
	
}
