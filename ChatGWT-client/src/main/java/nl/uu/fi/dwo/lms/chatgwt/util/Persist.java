package nl.uu.fi.dwo.lms.chatgwt.util;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.storage.client.Storage;

import nl.uu.fi.dwo.lms.chatgwt.ChatGWT;

public class Persist implements PersistIF {
	
	//private static final Date NULL = new Date(0L);	

	public static class NodeList {
		public String jid;
		public Map<String, String> nodes;
		
		public NodeList() { }
		public NodeList(String jid) {
			this(jid, new HashMap<>());
		}
		public NodeList(String jid, Map<String, String> nodes) {
			this.jid = jid;
			this.nodes = nodes;
		}		
	}

	interface NodeListCodec extends JsonEncoderDecoder<NodeList> { }
	
	static final NodeListCodec CODEC = GWT.create(NodeListCodec.class); 
	
	private static String PERSISTKEY = "nl.uu.fi.dwo.lms.chatgwt.util.Persist$";
	
	private Storage storage;
	private NodeList current;
	
	public Persist() {
		storage = Storage.getLocalStorageIfSupported();
	}

	public void init(String jid) {
		String persist = storage.getItem(PERSISTKEY + jid);
		if (persist != null) {
			try {
				current = CODEC.decode(persist);
				return;
			} catch(Exception oops) { }
		}
		current = new NodeList(jid);
	}

	public void flush() {
		String persist = CODEC.encode(current).toString();
		storage.setItem(PERSISTKEY + current.jid, persist);
	}
	
	public void seen(String jid) {
		current.nodes.put(jid, ChatGWT.now());
	}

	public String nonnull(String date) {
		if (date == null || date.isEmpty()) {
			return ChatGWT.utc();
		}
		return date;
	}
	
	public void seen(String jid, String date) {
		date = nonnull(date);
		if (isSeen(jid, date))
			current.nodes.put(jid, date);
	}
	
	public boolean isSeen(String jid, String date) {
		date = nonnull(date);
		if (current.nodes.containsKey(jid)) {
			boolean after = date.compareTo(current.nodes.get(jid))>0;
			return after;
		}
		return true; // date after null
	}
}
