package nl.uu.fi.dwo.mobile.client.sco;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.fusesource.restygwt.client.FailedResponseException;
import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.numworx.gwtpatch.client.GWTPatch;
import nl.numworx.gwtpatch.client.JSONBuilder;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ConfirmEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.PathId;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.CallManagers.Digest;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.StudentScoDataManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import fi.dwo.gwt.lib.rest.util.RestyException;
import fi.wiskopdr.text.Text;

public class SCORM_DWO5 extends SCORM_guest {

    public static final String SUSPEND_DIGEST = "cmi.suspend_digest";
    Digest digest;
	private Lazy<ConfirmEventHandler> confirmHandler;
	private final NeedLogin oops;
	private final DwoGlobalVars vars;
  
// DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole();
// DWOplayer.PARAMETERS.getSecureMode() == SecureMode.SEB

  @Inject
  public SCORM_DWO5(DomSchoolClassId dsci, 
                    DomHasRole hr, 
                    TrafficAgent barrier, 
                    @Named("secure") boolean secure, 
                    EventBus bus,
                    Lazy<ConfirmEventHandler> confirmHandler,
                    NeedLogin oops, 
                    DwoGlobalVars vars
		  	) {
	    this.oops = oops;
	    this.vars = vars;
		pending = false;
		schoolClassID = dsci;
		context = new DomContext();
		context.setDomHasRole(hr);
		context.setRealm(vars.getCurrentLoginContext().getRealm());
		scoDataManager = new SecuredStudentScoDataManager(secure); 
		this.barrier = barrier;
		this.bus = bus;
		digest = new Digest();
		this.confirmHandler = confirmHandler;
		map.put(Memento.LEARNER_ID, PathId.getId(context));
		map.put(Memento.LEARNER_NAME, getUsername(vars));// FIX als realm niet null is.
	}

	private String getUsername(DwoGlobalVars vars) {
		String realm = vars.getCurrentLoginContext().getRealm();
		String username = vars.getCurrentUser().getUserName();
		if (realm != null) {
			if (username.endsWith("@")) {
				username = username.substring(0, username.length()-1);
			} else if (! username.contains("@")) {
				username += "@" + realm;
			}
		}
		return username;
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
	

	static final Logger logger = Logger.getLogger("SCORM_DWO4");
	private final StudentScoDataManager scoDataManager; // not supported!
	private final DomSchoolClassId schoolClassID;
	private final DomContext context;
	private final TrafficAgent barrier;
	private final EventBus bus;

	@Deprecated private int scoID;
	private DomScoContext sco;
	private boolean pending;
	boolean inited;

	private Map<String,String> map = new HashMap<String, String>();
	private Map<String,String> dirty = new HashMap<String, String>();
		
	private <T> Promise<T> ag(Promise<T> p) {
		barrier.addBarrier(p);
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
			map.put(Memento.LEARNER_ID, PathId.getId(context));
			map.put(Memento.LEARNER_NAME, getUsername(vars));

			if(!dirty.isEmpty())
				logger.severe("wij hebben een probleem setScoID "+ dirty);
			dirty.clear(); // als niet clear dan hebben we een probleem!!!!
		}
		inited = true;
		this.scoID = scoID;
		sco = new DomScoContext();
		PersistenceId id = new PersistenceId();
		PersistenceClassType type = PersistenceClassType.PersistentScoContext;
		id.setIdString("MYSQL;" + type + ";" + scoID);
		sco.setId(id);
	}
	
	static final private int MAX_TIMES = 10;
	static final private int MAX_CNT = 5;
	List<Float> times = new LinkedList<>();
	
	void addTimes(long ms) {
		while(times.size() > MAX_TIMES ) times.remove(0);
		times.add(Float.valueOf(ms/1000.0f));
	}
	
	String lastSuspendData, lastETag;
	
	Promise<Boolean> confirm(String message) {
		return confirmHandler.get().confirm(message);
	}
	
	
	class Committer implements Failure, Success<String,Void> {

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
			if ( oops.needed(t)) {
				oops.apply(t);
				return;
			}
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
				if(cnt > MAX_CNT) {
					Promise<Boolean> ok = 
					confirm(
						(code == 0 ? Text.constants.noInternet() : Text.constants.serverError() ) +
						"\nCode " + code + " " + f.getResponse().getStatusText()  + "\n"
						+ Text.constants.opnieuwKnopLabel() +"?");
					ok.then(p -> {
						if (!p.getValue()) deferred.fail(caught);
						else backoffRetry();
						return p;});
				} return;
			} else {
				if(caught instanceof Dwo2Exception ) {
					Dwo2Exception de = (Dwo2Exception) caught;
					Dwo2ExceptionCode code = de.getDwo2Code();
					String message = de.getDwo2Message();
// Fatal errors that cannot be retried
					boolean fail = "readonly".equals(message) && code == Dwo2ExceptionCode.User_IllegalAction;
					fail |= code == Dwo2ExceptionCode.Exam_AuthenticationError;
					fail |= code == Dwo2ExceptionCode.Exam_InvalidSession;
					fail |= code == Dwo2ExceptionCode.Rest_LoginNeeded;
					fail |= code == Dwo2ExceptionCode.User_AuthenticationError;
					if(fail) 
					{
							switch(code) {
							case User_AuthenticationError: code = Dwo2ExceptionCode.Rest_LoginNeeded;
							case Rest_LoginNeeded: break;
							
							default: code = Dwo2ExceptionCode.Exam_InvalidSession;
							}
						setStatus(Status.NORMAL);
						bus.fireEvent(new DialogEvent(code));
						deferred.fail(caught);
						return;
					}
				}
				log("Failed exception: " + caught);
				confirm(caught.getLocalizedMessage() + "\n" + Text.constants.opnieuwKnopLabel() +"?")
				.then(p ->
				{
					if (p.getValue()) backoffRetry();
					else deferred.fail(caught);
					return p;
				}); return;
			}
			//backoffRetry();
			
		}

		private void backoffRetry() {
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
		public Promise<Void> call(Promise<String> t) {
			addTime();
			String result = t.getValue();
			if(result instanceof String) {
				lastETag = result.toString();
			} else {
				lastETag = null;
			}
			logger.info("Commit success: " + result);
			pending = false;
			retry=initialRetryDelayInMillis;
			cnt = 0;
			if(copy.containsKey(Memento.SUSPEND_DATA))
				lastSuspendData = copy.get(Memento.SUSPEND_DATA);
			copy.clear();
			if(!dirty.isEmpty()) commit();
			else {
				setStatus(Status.NORMAL);
				deferred.resolve("");
			}
			return null;
		}
		
		private boolean notempty(String s) {
		  return s != null && s.length()>=2;
		}
		public void commit() {
			copy.putAll(dirty);
			dirty.clear();
			pending = true;
			logger.info("committing " + copy.keySet());
			String suspendData = copy.get(Memento.SUSPEND_DATA);
			if(notempty(suspendData) && notempty(lastSuspendData) && lastETag != null) {
				try {
					String patch = new GWTPatch(new JSONBuilder()).createPatch(lastSuspendData, suspendData);
					logger.warning("compression: "  + suspendData.length() + " to " + patch.length());
					
					Map<String,String> patchMap = new LinkedHashMap<String,String>();
					patchMap.put("ETag", lastETag);
					if (DWOplayer.PROFILE_ID != 108)
						patchMap.put(SUSPEND_DIGEST, digest.digest(suspendData));
	                patchMap.put(Memento.SUSPEND_DATA, patch);
	                started = System.currentTimeMillis();
					Promise<String> then = scoDataManager.patchValues(sco, schoolClassID, context, patchMap).then(
							p -> {
								Object tag = p.getValue();
								Map copy2 = new HashMap(copy);
								copy2.remove(Memento.SUSPEND_DATA);
								if(tag instanceof String) 
									copy2.put("ETag", tag);
								return scoDataManager.setValuesETag(sco, schoolClassID, context, copy2);
							}
							);
					then
							.recoverWith((Promise<?> p)-> 
							{   if (oops.needed(p)) return (Promise<String>)p;
								return (Promise<String>) scoDataManager.setValuesETag(sco, schoolClassID, context, copy);
							})
							.then(this,this);
					return;
				} catch (Throwable e) {
					logger.log(Level.SEVERE,"gwtpatch failes", e);
				}
			}
			started = System.currentTimeMillis();
			scoDataManager.setValuesETag(sco, schoolClassID, context, copy).then(this, this);
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
	        //Memento.REVIEW_CHECK, // alleen bij testen reviewmode
	        "cmi.comments_from_lms.0.comment",
	        "cmi.comments_from_lms.2.comment"
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
		return map.getOrDefault(name, "");
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
	
//if (! name.startsWith("cmi.comments")) return "true";

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
					if (caught instanceof RestyException) 
						caught = caught.getCause(); // getValues returns resty exception
					logger.log(Level.SEVERE, "Initialize", caught);
					pending = false;
					if(callback!=null)callback.onFailure(caught);
				}};
	
				final Success<Map<String,String>, Void> success = new Success<Map<String,String>, Void>() {
				@Override
				public Promise<Void> call(Promise<Map<String, String>> resolved) throws Exception {
					Map<String,String> result = resolved.getValue();
log("initialized " +result.keySet());
					lastETag = result.remove("ETag");
					lastSuspendData = result.get(Memento.SUSPEND_DATA);
					map.putAll(result);
					dirty.clear();
					//map.putAll(dirty);
					pending = false;
					if(callback!=null)callback.onSuccess(null);					
					return null;
				}
			};
			barrier.barrier().then(new Success<Void,Void>(){

				@Override
				public Promise<Void> call(Promise<Void> resolved) throws Exception {
					return ag(scoDataManager.getValues(sco, schoolClassID, context, KEYS).then(success, failure));
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

	@Override
	public String getAuthorization() {
		return RestAuthenticator.instance.getAuthorization();
	}

	@Override
	public Promise<String> getValuePromise(String name) {
		Collection<String> keys = Collections.singleton(name);
		Promise<String> result = scoDataManager.getValues(sco, schoolClassID, context, keys).map(item -> item.get(name));
		barrier.addBarrier(result);
		result.then(null, p -> logger.log(Level.SEVERE, "getvalue promised", p.getFailure()));
		return result.recover(oops-> "");
	}

	@Override
	public Promise<Map<String, String>> getValuesPromise(Collection<String> keys) {
		Promise<Map<String,String>> result = scoDataManager.getValues(sco, schoolClassID, context, keys);
		barrier.addBarrier(result);
		result.then(null, p -> logger.log(Level.SEVERE, "getvalue promised", p.getFailure()));
		return result;
	}

	@Override
	public Role getRole() {
		try {
			if (vars.getRoleType() == RoleType.TEACHER) return Role.Instructor;
		} catch (Exception oops) {
		}
		return super.getRole();
	}


}
