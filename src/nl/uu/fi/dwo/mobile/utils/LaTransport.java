package nl.uu.fi.dwo.mobile.utils;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

public class LaTransport implements RequestCallback , Logging {

	
	private static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);
	private static final String ENDPOINT = "http://193.61.36.32/logactions";
	private static final String HOST = "lacon.lkl.ac.uk";
	private static final int TIMEOUT_MS = 10000;
	private Logger logger = Logger.getLogger(getClass().getName());
	private OpdrNavIF comRoot;
	
	private final  JSONObject LOG = new JSONObject();
	{
		LOG.put("id", new JSONString("log"));
	}
	private final JSONString VERSION = new JSONString("0.2.0");
	private JSONValue className = new JSONString("fi.wiskopdr.WiskOpdr");
	private JSONValue logID = null;

	private static JSONValue registration; 
	static {
		registration = new JSONString(GUID.get());
	}
	
	public void startSession() {
		registration = new JSONString(GUID.get());
	}
	
	public void stopSession() {
		
	}
	
	public void setClassName(JSONValue className) {
		this.className = className;
	}
	
	public void setClassName(String s) {
		if ( s == null) className = JSONNull.getInstance();
		else className = new JSONString(s);
	}
	
	public void setLogID(JSONValue logID) {
		this.logID = logID;
	}
	
	public void setLogID(String logID) {
		if(logID == null) this.logID = JSONNull.getInstance();
		else {
			(this.logID = new JSONObject()).isObject()
			.put(StubView.getLocale(), new JSONString(logID)); // TODO local zetten.
		}
	}
	
	public LaTransport(OpdrNavIF root) {
		comRoot = root;
	}

	public LaTransport() {
	}

	public void send0( JSONObject object) {
		RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.POST, ENDPOINT);
		requestBuilder.setHeader("Host", HOST);
		requestBuilder.setHeader("Content-Type", "application/json");
		requestBuilder.setTimeoutMillis(TIMEOUT_MS);
		String requestData = object.toString();
		logger.info(requestData);
		try {
			requestBuilder.sendRequest(requestData, this);
		} catch (RequestException e) {
			logger.log(Level.SEVERE, "sendRequest", e);;
		}
	}

	@Override
	public void onResponseReceived(Request request, Response res) {		
		logger.info( "Lacon returned "+
				"response code "+res.getStatusCode()+" - "+
				res.getStatusText());
	}

	@Override
	public void onError(Request request, Throwable exception) {
	}
	
	public JSONObject buildMessage(Map<String, ?> parameters) {
		JSONObject msg = new JSONObject();
		JSONObject actor = new JSONObject();
		JSONObject account = new JSONObject();
		account.put("name", new JSONString(comRoot.getLearnerId()));
		account.put("homePage", new JSONString(getHomePage()));
		actor.put("account", account);
		actor.put("name", new JSONString(comRoot.getLearnerName()));
		msg.put("actor", actor);
		
		JSONObject object = new JSONObject();
		String uuid = comRoot.getUUID();
		object.put("id", new JSONString(uuid));
		JSONObject definition= new JSONObject(); object.put("definition", definition);
		definition.put("type", className );
		definition.put("name", logID);
		msg.put("object", object);
		msg.put("version", VERSION);
		msg.put("timestamp", getTimeStamp());
		msg.put("result", toJSONObject(parameters));
		
		JSONObject context = new JSONObject();
		context.put("registration", registration);
		JSONObject contextActivities = new JSONObject();
		int i = uuid.indexOf('-');
		uuid = uuid.substring(0, i);
		JSONObject parent = new JSONObject();
		parent.put("id", new JSONString(uuid));
		contextActivities.put( "parent", parent);
		context.put("contextActivities", contextActivities);
		msg.put("context", context);
		return msg;
		
	}

	private String getHomePage() {
		String protocol = Window.Location.getProtocol();
		String host     = Window.Location.getHost();
		
		return protocol + "//" + host + "/dwo/dwo.jsp";
	}

	private JSONObject toJSONObject(Map<String, ?> parameters) {
		if (parameters instanceof JSONObjectMapImpl)
			return ((JSONObjectMapImpl) parameters).unwrap();
		JSONObject map = new JSONObject();
		for(Map.Entry<String, ?> entry: parameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof JSONValue) {
				map.put(key, (JSONValue) value);
			} else if(value instanceof Boolean) {
				map.put(key, JSONBoolean.getInstance(((Boolean) value).booleanValue()));
			} else if(value instanceof Number) {
				map.put(key, new JSONNumber(((Number) value).doubleValue()));
			} else if(value instanceof String) {
				map.put(key, new JSONString((String) value));
			} else if(value instanceof Map) {
				map.put(key, toJSONObject((Map<String, ?>) value));
			}
// etc: Collection, Object[]
			
			else
				map.put(key, JSONNull.getInstance());
			
		}
		return map;
	}

	private JSONString getTimeStamp() {
		return new JSONString(Long.toString(System.currentTimeMillis()));
	}
	
	public void log(Map<String,?> result) {
		if(comRoot == null) return;
		JSONObject msg = buildMessage(result);
		send0(msg);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}
}
