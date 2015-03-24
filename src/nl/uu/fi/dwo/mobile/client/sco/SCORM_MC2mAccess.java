package nl.uu.fi.dwo.mobile.client.sco;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_MC2mAccess extends SCORM_guest implements Scorm2004IF {

	static final Logger logger = Logger.getLogger("SCORM_MC²mAccess");
	private String userID;
	private String scoID;
	
	private String learner_id;
	private String learner_name;
	private boolean pending;
	boolean inited;

	private Map<String,String> map = new HashMap<String, String>();
	private Map<String,String> dirty = new HashMap<String, String>();
	
	private XmlRpcClient client;
		
	public SCORM_MC2mAccess(String userID, String learner_id, String learner_name) {
		
		this.learner_id = learner_id;
		this.learner_name = learner_name;
		String host = Window.Location.getHost();
		String http = Window.Location.getProtocol();
		if(!GWT.isProdMode())
			host = "9-dot-mc2dme.appspot.com";

		client = new XmlRpcClient(http + "//" + host + "/dwoapp");
		
		this.userID = userID;
		pending = false;
		client.setTimeoutMillis(1000000);
	}

	public Object getScoID() {
		return scoID;
	}

	public void setScoID(String scoID) {
		if(!scoID.equals(this.scoID))
		{
			map.clear();
			if(!dirty.isEmpty())
				logger.severe("wij hebben een probleem setScoID "+ dirty);
			dirty.clear(); // als niet clear dan hebben we een probleem!!!!
		}
		inited = true;
		this.scoID = scoID;
	}

	class Committer implements AsyncCallback<Boolean> {

		boolean pending;
		Map<String,String> dirty, copy;
		Object[] params;
		
		@Override
		public void onFailure(Throwable caught) {
			logger.severe("Commit: "+ caught);
			commit();
		}

		@Override
		public void onSuccess(Boolean result) {
			pending = false;
			if(!Boolean.TRUE.equals(result)) onFailure(null);
			else {
				copy.clear();
				if(!dirty.isEmpty()) commit();
			}
				
		}
		
		public void commit() {
			copy.putAll(dirty);
			dirty.clear();
			XmlRpcRequest<Boolean> request;
			pending = true;
			request = new XmlRpcRequest<Boolean>(client, "Commit", params, this);
			request.execute();
		}
		
		Committer(String scoID, String userID, Map<String,String> dirty) {
			copy = new HashMap<String,String>(dirty);
			this.dirty = new HashMap<String,String>();
			params = new Object[] { userID, scoID, copy };
		}

		public void add(Map<String, String> dirty) {
			this.dirty.putAll(dirty);
		}
	}
	
	Committer committer;
	
	public synchronized String Commit() {
		if(!dirty.isEmpty())
		{
			if(committer == null) {
				committer = new Committer(scoID, userID, dirty);
			} else {
				committer.add(dirty);
			}
			dirty.clear();
			if(!committer.pending) committer.commit();
			else logger.info("pending commit");
			
		}
		return super.Commit();
	}
	
	public synchronized String Commitxxx() {
		if(!pending && !dirty.isEmpty())
		{	pending = true;
			XmlRpcRequest<Boolean> request;
			final HashMap<String, String> copy = new HashMap<String, String>(dirty);
			dirty.clear();
			Object[] params = new Object[] { userID, scoID, copy };
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					logger.severe("Commit: "+ caught);
					pending = false;
					copy.putAll(dirty);
					dirty.putAll(copy);
					if (!dirty.isEmpty()) Commit(); // continue until dirty is empty
				}

				@Override
				public void onSuccess(Boolean result) {
					pending = false;
					if(!Boolean.TRUE.equals(result)) onFailure(null);
					else
					if (!dirty.isEmpty()) Commit(); // continue until dirty is empty
						
				}};
				request = new XmlRpcRequest<Boolean>(client, "Commit", params, callback );
				request.execute();
		} else {
			logger.info("pending commit");
		}
		return super.Commit();
	}

	@Override
	public String GetValue(String name) {
		if(Memento.LEARNER_ID.equals(name)) return learner_id;
		if(Memento.LEARNER_NAME.equals(name)) return learner_name;
		String result = map.get(name);
		if(result == null) return "";
		return result;
	}

	@Override
	public String SetValue(String name, String value) {
		map.put(name, value);
		dirty.put(name, value);
		return super.SetValue(name, value);
	}

	public synchronized void Initialize(final AsyncCallback<Void> callback) {
		if(!pending) {
			pending = true;
			XmlRpcRequest<HashMap<String,String>> request;
			Object[] params = new Object[] { userID, scoID };
			
			AsyncCallback<HashMap<String,String>> cb = new AsyncCallback<HashMap<String,String>>() {
		
				@Override
				public void onFailure(Throwable caught) {
					logger.severe("Initialize: "+ caught);
					pending = false;
					if(callback!=null)callback.onFailure(caught);
				}
		
				@Override
				public void onSuccess(HashMap<String, String> result) {
					
					map.putAll(result);
					map.putAll(dirty);
					pending = false;
					if(callback!=null)callback.onSuccess(null);
				}};
			
			request = new XmlRpcRequest<HashMap<String,String>>(client, "Initialize", params, cb );
			request.execute();
		} else {
			logger.info("still pending, wait a little");
			Timer t = new Timer()
			{
				@Override
				public void run()
				{
					Initialize(callback);
				}
			};
			t.schedule(100);

		}
	}

	@Override
	public String Terminate() {
		Commit();
		committer = null; // no access possible
		inited = false;
		return super.Terminate();
	}

	
}
