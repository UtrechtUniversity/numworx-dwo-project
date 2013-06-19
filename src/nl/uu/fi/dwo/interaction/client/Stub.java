package nl.uu.fi.dwo.interaction.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


public class Stub implements OpdrNavIF {
	
	private InteractionStub view;
	
	private Stub(InteractionStub view) {
		this.view = view;
	}
	
	private String getState() {
		Map<String,Object> map = view.getState();
		return JSONUtilities.toJSONObject(map).toString();
	}
	
	private void setState(String jso) {
		JSONObject js = JSONParser.parseLenient(jso).isObject();
		HashMap<String,Object> result = JSONUtilities.fromJSONObject(js);
		view.setState(result);
	}
	
	private void init(String launchdata, JavaScriptObject randomValues) {
		Map<String, Number> numbers = new HashMap<String,Number>();
		Map<String,Object> result;

		Set<String> keys;
		JSONObject values;
		
		values = new JSONObject(randomValues);
		keys = values.keySet();
		for(String key: keys) {
			numbers.put(key, values.get(key).isNumber().doubleValue());
		}

		result  = JSONUtilities.fromJSONObject(JSONParser.parseLenient(launchdata).isObject());
		
		view.init(result, numbers);
		view.setCommunicationRoot(this);
	}
	
	
	private static native Object publish0(Object o) /*-{
		$wnd.inner = {
			getScore: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::getScore()();
			},
			getState: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getState()();
			},
			setState: function(jso) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::setState(Ljava/lang/String;)(jso);
			},
			isCorrect: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::isCorrect()();
			},
			init: function(launchdata,values) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::init(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(launchdata,values);
			}
		};
		$wnd.inner.stub = o;
		$wnd.publish($wnd.inner, $wnd.outer);
		return $wnd.inner
		
	}-*/;
	
	public static void publish(InteractionStub view) 
	{
		publish0(new Stub(view));
	}

	@Override
	public void setChanged() {
		setChanged0();
	}
	
	private static native void setChanged0() /*-{
		$wnd.setChanged($wnd.outer);
	}-*/;
	
	
	
	

}
