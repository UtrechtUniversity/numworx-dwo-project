package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.fusesource.restygwt.client.FailedResponseException;
import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import fi.wiskopdr.text.Text;

@Deprecated
public class SCORM_DWO3 extends SCORM_guest {

	@Inject SCORM_DWO3() {
		pending = false;
	}

	enum Status { NORMAL, DIRTY, BUSY, RETRY };
	Status status = Status.NORMAL;
	
	private void setStatus(Status s) {
		status = s;
		RootLayoutPanel p = RootLayoutPanel.get();
		p.setStyleName("status-busy", false);
		p.setStyleName("status-retry", false);
		switch(s) {
		case DIRTY: 
		case NORMAL: break;
		case BUSY:
				p.setStyleName("status-busy", true);
				break;
		case RETRY:
				p.setStyleName("status-retry", true);
		}
	}
	

	static final Logger logger = Logger.getLogger("SCORM_DWO3");
	private int scoID;
	private boolean pending;
	boolean inited;

	private Map<String,String> map = new HashMap<String, String>();
	private Map<String,String> dirty = new HashMap<String, String>();
	
	@Inject RPCHandler client;
	@Inject TrafficAgent agent;
	
	private <T> Promise<T> ag(Promise<T> p) {
		agent.addBarrier(p);
		return p;
	}
		
	public int getScoID() {
		return scoID;
	}

	public void setScoID(String scoID) {
		setScoID(Integer.parseInt(scoID));
	}
	
	public void setScoID(int scoID) {
log("setScoID " + scoID);
		if(scoID!=this.scoID)
		{			
			if(inited) // Only in Terminated state!!!!
			{	
				IllegalArgumentException e = new IllegalArgumentException("setScoID " + scoID + " " + this.scoID);
				log("Not in inited state",e);throw e;
			}
			
			map.clear();
			if(!dirty.isEmpty())
				logger.severe("wij hebben een probleem setScoID "+ dirty);
			dirty.clear(); // als niet clear dan hebben we een probleem!!!!
		}
		inited = true;
		this.scoID = scoID;
	}
	
	static final private int MAX_TIMES = 10;
	static final private int MAX_CNT = 5;
	List<Float> times = new LinkedList<>();
	
	void addTimes(long ms) {
		while(times.size() > MAX_TIMES ) times.remove(0);
		times.add(Float.valueOf(ms/1000.0f));
	}
	
	
	class Committer implements Failure, Success<Object,Void> {

		private static final double initialRetryDelayInMillis = 1000;
		Deferred<String> deferred = new Deferred<String>();

		Promise<String> getPromise() {
			return (deferred.getPromise());
		}
		
 		boolean pending;
		Map<String,String> dirty, copy;
		double retry=initialRetryDelayInMillis;//milliseconds never 0
		long started;
		int cnt;
		
		void addTime() {
			addTimes(System.currentTimeMillis()-started);
		}
		
		float meanTime() {
			float sum = 0.0f;
			for(Float f : times) sum += f.floatValue();
			return sum / times.size();
		}
		
		@Override
		public void fail(Promise<?> t) {
			addTime();
			Throwable caught = t.getFailure();
			logger.log(Level.SEVERE, "Commit failed: "+ caught, caught);
			if(caught instanceof FailedResponseException) {
				FailedResponseException f= (FailedResponseException)caught;
				int code = f.getStatusCode();
				log("Failed statuscode = " + code);
				log("Failed response = " + f.getResponse().getHeadersAsString());
				log("mean time =" + meanTime());
// FIXME betere foutmelding, message voor cancel?
				if(cnt > MAX_CNT && !Window.confirm(
						(code == 0 ? Text.constants.noInternet() : Text.constants.serverError() ) +
						"\nCode " + code + " " + f.getResponse().getStatusText()  + "\n"
						+ Text.constants.opnieuwKnopLabel() +"?"))
				{
					deferred.fail(caught);
					return;
				}
			}
			setStatus(Status.RETRY);
			retry+=retry/2;//exponential delay
			Timer backoff = new Timer() {

				@Override
				public void run() {
					cnt += 1;
					commit();
				}
			};
			backoff.schedule(Math.max(1, (int) (retry*Math.random())));
			
		}

		@Override
		public Promise<Void> call(Promise<Object> t) {
			addTime();
			Object result = t.getValue();
			logger.info("Commit success: " + result);
			pending = false;
			retry=initialRetryDelayInMillis;
			if(!Boolean.TRUE.equals(result)) fail(Promises.resolved(null));
			else {
				cnt = 0;
				copy.clear();
				if(!dirty.isEmpty()) commit();
				else {
					setStatus(Status.NORMAL);
					deferred.resolve("");
				}
			}
			return null;
		}
		
		public void commit() {
			copy.putAll(dirty);
			dirty.clear();
			pending = true;
			logger.info("committing " + copy.keySet());
			started = System.currentTimeMillis();
			client.setValues(scoID, copy).then(this, this);
		}
		
		Committer(int scoID, Map<String,String> dirty) {
			copy = new HashMap<String,String>(dirty);
			this.dirty = new HashMap<String,String>();
			setStatus(Status.BUSY);
			ag(deferred.getPromise());
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
	
	public synchronized Promise<String> Commit() {
log("Commit " + dirty.isEmpty());
		if(!dirty.isEmpty())
		{
			if(committer == null||committer.getPromise().isDone()) {
				committer = new Committer(scoID, dirty);
			} else {
				committer.add(dirty);
			}
			dirty.clear();
			if(!committer.pending) committer.commit();
			else log("pending commit");
			return committer.getPromise();
		}
		if(committer != null) return committer.getPromise();
		return super.Commit();
	}
	

	@Override
	public String GetValue(String name) {
		String result = map.get(name);
		if(result == null) return "";
		return result;
	}

	private void log(String msg, Throwable e) {
		msg = "Sco=" + scoID + ": " + msg;
 		logger.log(Level.INFO, msg, e);
	}
	private void log(String msg) {
		log(msg, null);
	}
	
	@Override
	public String SetValue(String name, String value) {
log("SetValue " + name);
		if(status == Status.NORMAL) setStatus(Status.DIRTY);
		map.put(name, value);
		dirty.put(name, value);
		return "true";
	}

	public synchronized void Initialize(final AsyncCallback<Void> callback) {
log("Initialize "+ pending);
		if(!pending) {
			pending = true;
			final Failure failure = new Failure() {
				@Override
				public void fail(Promise<?> resolved) throws Exception {
					Throwable caught = resolved.getFailure();
					logger.log(Level.SEVERE, "Initialize", caught);
					pending = false;
					if(callback!=null)callback.onFailure(caught);
				}};
	
				final Success<Map<String,String>, Void> success = new Success<Map<String,String>, Void>() {
				@Override
				public Promise<Void> call(Promise<Map<String, String>> resolved) throws Exception {
					Map<String,String> result = resolved.getValue();
log("initialized " +result.keySet());
					map.putAll(result);
					dirty.clear();
					//map.putAll(dirty);
					pending = false;
					if(callback!=null)callback.onSuccess(null);					
					return null;
				}
			};
			agent.barrier().then(new Success<Void,Void>(){

				@Override
				public Promise<Void> call(Promise<Void> resolved) throws Exception {
					return ag(client.getValues(scoID, KEYS).then(success, failure));
				}});
			
			
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

	Promise<String> terminated;
	@Override
	public Promise<String> Terminate() {
		if(!inited) return super.Terminate();
		if(terminated != null) return terminated;
		Promise<String> p = Commit();
		return ag(terminated = p.map(new Function<String,String>() {
			public void run() {
				committer = null; // no access possible
				inited = false;
				terminated = null;
			}

			@Override
			public String apply(String t) {
				run();
				return t;
			}
			
		}));
		
	}


}
