package nl.uu.fi.dwo.mobile.client.sco;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_DWO2 extends SCORM_guest {

	public SCORM_DWO2(int userid, int sgid) {
		this.userID = userid;
		this.sgID = sgid;
		pending = false;
		client.setTimeoutMillis(1000000);
	}

	public SCORM_DWO2(Object userID, Object sgID) {
		this( ((Number) userID).intValue(), ((Number) sgID).intValue());
	}

	static final Logger logger = Logger.getLogger("SCORM_DWO2");
	private int userID, sgID;
	private int scoID;
	private boolean pending;
	boolean inited;

	private Map<String,String> map = new HashMap<String, String>();
	private Map<String,String> dirty = new HashMap<String, String>();
	
	// Same URL als rpchandler.
	private XmlRpcClient client = (XmlRpcClient) DWOplayer.clientfactory.getRPCHandler().getClient().clone();
		
	public int getScoID() {
		return scoID;
	}

	public void setScoID(String scoID) {
		setScoID(Integer.parseInt(scoID));
	}
	
	public void setScoID(int scoID) {
		if(scoID!=this.scoID)
		{			
			if(inited) // Only in Terminated state!!!!
				throw new IllegalArgumentException("setScoID " + scoID + " " + this.scoID);
			
			map.clear();
			if(!dirty.isEmpty())
				logger.severe("wij hebben een probleem setScoID "+ dirty);
			dirty.clear(); // als niet clear dan hebben we een probleem!!!!
		}
		inited = true;
		this.scoID = scoID;
	}

	class Committer implements AsyncCallback<Boolean> {

		private static final double initialRetryDelayInMillis = 1000;
		boolean pending;
		Map<String,String> dirty, copy;
		Object[] params;
		double retry=initialRetryDelayInMillis;//milliseconds never 0
		
		@Override
		public void onFailure(Throwable caught) {
			logger.log(Level.SEVERE, "Commit: "+ caught, caught);
			retry+=retry/2;//exponential delay
			Timer backoff = new Timer() {

				@Override
				public void run() {
					commit();
				}
			};
			backoff.schedule((int) (retry*Math.random()));
			
		}

		@Override
		public void onSuccess(Boolean result) {
			pending = false;
			retry=initialRetryDelayInMillis;
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
		
		Committer(int scoID, int userID, int sgID, Map<String,String> dirty) {
			copy = new HashMap<String,String>(dirty);
			this.dirty = new HashMap<String,String>();
			params = new Object[] { userID, sgID, scoID, copy };
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
				committer = new Committer(scoID, userID, sgID, dirty);
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
			Object[] params = new Object[] { userID, sgID, scoID };
			
			AsyncCallback<HashMap<String,String>> cb = new AsyncCallback<HashMap<String,String>>() {
		
				@Override
				public void onFailure(Throwable caught) {
					logger.log(Level.SEVERE, "Initialize", caught);
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
