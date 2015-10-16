package nl.uu.fi.dwo.interaction.client.event;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class CallBack {
	CBookEventListener listener;
	
	public void call(String topic, JavaScriptObject jso) {
		JSONObject j = new JSONObject(jso);
		ObjectMap map = JSONUtilities.wrapMap(j);
		CBookEvent event = new CBookEvent(map);
		listener.acceptCBookEvent(event);
	}
	CallBack(CBookEventListener listener) {
		this.listener = listener;
	}
}