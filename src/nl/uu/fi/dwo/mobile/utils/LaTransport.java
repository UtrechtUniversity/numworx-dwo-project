package nl.uu.fi.dwo.mobile.utils;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;

import com.google.gwt.core.client.JavaScriptObject;
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

public class LaTransport implements Logging {
	
	private static native void installLacon(LaconSender lacon)
	/*-{ (function (lacon) { 
			$wnd.laconSender = function (obj) {
				lacon.@nl.uu.fi.dwo.mobile.utils.LaTransport.LaconSender::send00(Lcom/google/gwt/core/client/JavaScriptObject;)(obj)
			}
		})(lacon)
	}-*/
	;
	

	public static Logging newInstance() { 
		return new LaTransport(new PairSender(new LaconSender(), new LogSender()));		
	}

	public static Logging newJSInstance() {
		installLacon(new LaconSender());
		return new LaTransport(new PairSender(new JSSender(), new LogSender()));
	}
	
	private Sender sender;
	
	abstract static class Sender {
		protected Logger lg = Logger.getLogger(getClass().getName());

		void send0(JSONObject object) {
			lg.fine("send0 " + object.toString());
		}
	}
	
	static class LaconSender extends Sender implements RequestCallback {
		void send0( JSONObject object) {
			RequestBuilder requestBuilder = new RequestBuilder(
					RequestBuilder.POST, ENDPOINT);
			requestBuilder.setHeader("Content-Type", "application/json");
			requestBuilder.setTimeoutMillis(TIMEOUT_MS);
			String requestData = object.toString();
			lg.info(requestData);
			try {
				requestBuilder.sendRequest(requestData, this);
			} catch (RequestException e) {
				lg.log(Level.SEVERE, "sendRequest", e);;
			}
		}

		void send00 (JavaScriptObject jso) {
			send0(new JSONObject(jso));
		}
		@Override
		public void onResponseReceived(Request request, Response res) {		
			lg.info( "Lacon returned "+
					"response code "+res.getStatusCode()+" - "+
					res.getStatusText());
		}

		@Override
		public void onError(Request request, Throwable exception) {
			lg.log(Level.SEVERE, "send0", exception);
		}	
	}
	
	static class LogSender extends Sender {
		static native void send00(JavaScriptObject jso) /*-{
			$wnd.logAction(jso)
		}-*/;
		
		void send0(JSONObject jso) {
			try {
				send00(jso.getJavaScriptObject());
			} catch(Exception e) {
				lg.log(Level.SEVERE, "logAction", e);
			}
		}
	}
	
	static class PairSender extends Sender {
		Sender a,b;
		PairSender(Sender a, Sender b) {
			this.a = a;
			this.b = b;
		}
		void send0(JSONObject jso) {
			a.send0(jso);
			b.send0(jso);
		}
	}
	
	static class JSSender extends Sender {
		static native void send00(JavaScriptObject jso)/*-{
				$wnd.updateHandler(jso)
		}-*/;
		
		void send0(JSONObject jso) {
			try {
				send00(jso.getJavaScriptObject());
			} catch (Exception e) {
				lg.log(Level.SEVERE, "updateHandler", e);
			}
		}
	}
	
	
	
	private static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);
	private static final String ENDPOINT = "http://lacon.lkl.ac.uk/logactions";
	private static final int TIMEOUT_MS = 10000;
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
		JSONObject msg = buildUnitMessage("started", null);
		sender.send0(msg);
	}
	
	public void stopSession() {
		sender.send0(buildUnitMessage("stopped", null));
	}
		
	public void setLocation(String location) {
		JSONObject result = new JSONObject();
		result.put("location", new JSONString(location));
		JSONObject msg = buildUnitMessage("location", result);
		sender.send0(msg);
	}
	
	public void unitLog(Map<String, Object> response) {
		JSONObject msg = buildUnitMessage("log", toJSONObject(response));
		
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
	
	private LaTransport(Sender laconSender) {
		this.sender = laconSender;
	}
	public JSONObject buildUnitMessage(String id, JSONValue result) {
		JSONObject msg = new JSONObject();
		insertActor(msg);
		JSONObject verb = new JSONObject();
		verb.put("id", new JSONString(id));
		msg.put("verb", verb);
// object
		JSONObject object = new JSONObject();
		String uuid = comRoot.getUUID();
		int i = uuid.indexOf('-');
		uuid = uuid.substring(0, i);
		object.put("id", new JSONString(uuid));
		JSONObject definition = new JSONObject();
		definition.put("type", className);
		object.put("definition", definition);
		msg.put("object", object);
// version/context/timestamp
		msg.put("version", VERSION);
		msg.put("timestamp", getTimeStamp());
		JSONObject context = new JSONObject();
		context.put("registration", registration);
		msg.put("context", context);

		if(result != null) {
			msg.put("result", result);
		}
		
		return msg;
	}
	
	public JSONObject buildMessage(Map<String, ?> parameters) {
		JSONObject msg = new JSONObject();
		insertActor(msg);
		
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

	void insertActor(JSONObject msg) {
		JSONObject actor = new JSONObject();
		JSONObject account = new JSONObject();
		account.put("name", new JSONString(getLearnerId()));
		account.put("homePage", new JSONString(getHomePage()));
		actor.put("account", account);
		actor.put("name", new JSONString(getLearnerName()));
		msg.put("actor", actor);
	}

	private String getLearnerName() {
		String learnerName = comRoot.getLearnerName();
		if(learnerName == null || learnerName.isEmpty())
			return "Guest, Anonymous";
		return learnerName;
	}

	private String getLearnerId() {
		String learnerId = comRoot.getLearnerId();
		if(null == learnerId || learnerId.isEmpty())
			return "guest";
		return learnerId;
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
		sender.send0(msg);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}
}
