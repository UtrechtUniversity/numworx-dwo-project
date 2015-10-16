package nl.uu.fi.dwo.interaction.client.event;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.Event;


public class CBookEvent extends Event<CBookEventListener> {

	public static final Type<CBookEventListener> TYPE = new Event.Type<CBookEventListener>();

	@Override
	public Event.Type<CBookEventListener> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CBookEventListener handler) {
		handler.acceptCBookEvent(this);
	}

	private final String command;
	private final String message;
	private final Map<String,?> parameters;

	/**
	 * Construct a CBookEvent.
	 * @param source the cbook widget instance that originated the event.
	 * @param command
	 * @param parameters
	 */
	public CBookEvent(InteractionView source, String command, Map<String,?> parameters) {
		setSource(source);
		this.command = command;
		this.parameters = parameters;
		this.message = null;
	}
	/**
	 * Construct a CBookEvent.
	 * @param source the cbook widget instance that originated the event.
	 * @param command
	 */
	public CBookEvent(InteractionView source, String command) {
		super.setSource(source);
		this.command = command;
		this.parameters = null;
		this.message = null;
	}

	public CBookEvent(String command) {
		this.command = command;
		this.parameters = null;
		this.message = null;
	}

	/**
	 * Construct a CBookEvent.
	 * @param source the cbook widget instance that originated the event.
	 * @param command
	 * @param message
	 */
	public CBookEvent(InteractionView source, String command, String message) {
		super.setSource(source);
		this.command = command;
		this.parameters = null;
		this.message = message;
	}

	public CBookEvent(ObjectMap map) {
		this.command = map.getString("command");
		this.parameters = map.getMap("parameters");
		this.message = map.getString("message");
		setSource(map.getString("source"));
	}
	public CBookEvent(String command, ObjectMap map) {
		this.command = command;
		this.parameters = map.getMap("parameters");
		this.message = map.getString("message");
		setSource(map.getString("source"));
	}
	
	
	public ObjectMap toObjectMap() {
		JSONValue c = JSONUtilities.toJSONValue(command);
		JSONValue m = JSONUtilities.toJSONValue(message);
		JSONValue s = JSONUtilities.toJSONValue(getSource());
		JSONValue p = JSONUtilities.toJSONObject(parameters);
		
		JSONObject o = new JSONObject();
		o.put("message", m);
		o.put("parameters", p);
		o.put("command", c);
		o.put("source", s);		
		return JSONUtilities.wrapMap(o);
	}

	/**
	 * The commmand of the event.
	 * @return a command
	 * @see Constants#USER_INPUT
	 * @see Constants#LOGGING
	 */
	public String getCommand() {
		return command;
	}

	/** 
	 * The Map of parameters.
	 * @return a map
	 */
	public Map<String,?> getParameters() {
		return parameters;
	}

	/**
	 * A single parameter.
	 * @param key name of parameter
	 * @return value of parameter
	 */
	public Object getParameter(String key) {
		if(parameters != null)
			return parameters.get(key);
		return null;
	}

	/**
	 * Get the message.
	 */
	public String getMessage() {
		return message;
	}
	
	@Deprecated // Use constructor!
	public void setSource(InteractionView source) {
		super.setSource(source);
	}

	public void setSource(String uuid) {
		super.setSource(uuid);
	}

}
