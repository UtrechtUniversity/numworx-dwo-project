package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SCORM_DWO3 extends SCORM_guest {

	public SCORM_DWO3() {
		pending = false;
	}


	static final Logger logger = Logger.getLogger("SCORM_DWO3");
	private int scoID;
	private boolean pending;
	boolean inited;

	private Map<String,String> map = new HashMap<String, String>();
	private Map<String,String> dirty = new HashMap<String, String>();
	
	// Same URL als rpchandler.
	private RPCHandler client = DWOplayer.clientfactory.getRPCHandler();
		
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

	class Committer implements Failure, Success<Object,Void> {

		private static final double initialRetryDelayInMillis = 1000;
		boolean pending;
		Map<String,String> dirty, copy;
		double retry=initialRetryDelayInMillis;//milliseconds never 0
		
		@Override
		public void fail(Promise<?> t) {
			Throwable caught = t.getFailure();
			logger.log(Level.SEVERE, "Commit: "+ caught, caught);
			retry+=retry/2;//exponential delay
			Timer backoff = new Timer() {

				@Override
				public void run() {
					commit();
				}
			};
			backoff.schedule(Math.max(1, (int) (retry*Math.random())));
			
		}

		@Override
		public Promise<Void> call(Promise<Object> t) {
			Object result = t.getValue();
			pending = false;
			retry=initialRetryDelayInMillis;
			if(!Boolean.TRUE.equals(result)) fail(Promises.resolved(null));
			else {
				copy.clear();
				if(!dirty.isEmpty()) commit();
			}
			return null;
		}
		
		public void commit() {
			copy.putAll(dirty);
			dirty.clear();
			pending = true;
			client.setValues(scoID, copy).then(this, this);
		}
		
		Committer(int scoID, Map<String,String> dirty) {
			copy = new HashMap<String,String>(dirty);
			this.dirty = new HashMap<String,String>();
		}

		public void add(Map<String, String> dirty) {
			this.dirty.putAll(dirty);
		}
	}
	
	Committer committer;
	private static final Collection<String> KEYS = Arrays.asList( 
	        "cmi.suspend_data",
	        "cmi.score.raw",
	        "cmi.total_time",
	        "cmi.location",
	        "cmi.completion_status",
	        "cmi.comments_from_lms.0.comment"
	);
	
	public synchronized String Commit() {
		if(!dirty.isEmpty())
		{
			if(committer == null) {
				committer = new Committer(scoID, dirty);
			} else {
				committer.add(dirty);
			}
			dirty.clear();
			if(!committer.pending) committer.commit();
			else logger.info("pending commit");
			
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
			Failure failure = new Failure() {
				@Override
				public void fail(Promise<?> resolved) throws Exception {
					Throwable caught = resolved.getFailure();
					logger.log(Level.SEVERE, "Initialize", caught);
					pending = false;
					if(callback!=null)callback.onFailure(caught);
				}};
	
				Success<Map<String,String>, Void> success = new Success<Map<String,String>, Void>() {
				@Override
				public Promise<Void> call(Promise<Map<String, String>> resolved) throws Exception {
					Map<String,String> result = resolved.getValue();
					map.putAll(result);
					map.putAll(dirty);
					pending = false;
					if(callback!=null)callback.onSuccess(null);					
					return null;
				}
			};		
			client.getValues(scoID, KEYS).then(success, failure);
			
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
