package nl.uu.fi.dwo.interaction.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

public class ListenerForEvents {
	private CBookEventListener listener;
	void accept(String event) {
		JSONValue value = JSONParser.parse(event);
		listener.acceptCBookEvent(new CBookEvent(JSONUtilities.wrapMap(value.isObject())));
	}
	
	void accept(JavaScriptObject obj) {
		JSONObject value = new JSONObject(obj);
		listener.acceptCBookEvent(new CBookEvent(JSONUtilities.wrapMap(value)));
	}

	public ListenerForEvents(CBookEventListener listener) {
		this.listener = listener;
	}
	
}