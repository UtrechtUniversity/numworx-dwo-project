package nl.numworx.gwtpatch.client;

import java.util.Map;

import nl.numworx.gwtpatch.client.GWTPatch.Builder;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

class Remove extends DiffItem {

	public Remove(String pointer) {
		setPath(pointer);
		setOp("remove");
	}

	@Override
	ObjectMap toObjectMap(Builder builder) {
		Map<String, Object> map = builder.createMap();
		map.put("op", op);
		map.put("path", path);
		return builder.toObjectMap(map);
	}

}
