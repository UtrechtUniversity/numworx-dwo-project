package nl.numworx.gwtpatch.client;

import com.google.gwt.json.client.JSONValue;

class Modify extends DiffItem {

	Modify(String pointer, Object now) {
		setOp("replace");
		setValue(now);
		setPath(pointer);
	}


}
