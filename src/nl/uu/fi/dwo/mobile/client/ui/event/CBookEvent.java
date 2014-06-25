package nl.uu.fi.dwo.mobile.client.ui.event;

import java.util.Map;

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
	public CBookEvent(Object source, String command, Map<String,?> parameters) {
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
	public CBookEvent(Object source, String command) {
		setSource(source);
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
	public CBookEvent(Object source, String command, String message) {
		setSource(source);
		this.command = command;
		this.parameters = null;
		this.message = message;
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
	
}
