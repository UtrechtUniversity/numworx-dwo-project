package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.user.client.Timer;

public class TimedBarrier extends Timer {
	
	private Deferred<Void> defer;
	private final TrafficAgent agent;
	
	@Inject
	public TimedBarrier(TrafficAgent agent) {
		this.agent = agent;
	}

	public Promise<Void> getPromise() {
		if (defer == null) {
			return Promises.resolved(null);
		} else 
			return defer.getPromise();
	}
	
	@Override
	public synchronized void cancel() {
		super.cancel();
		if (defer != null) {
			Deferred<Void> org = defer;
			defer = null;
			if (!org.getPromise().isDone()) org.resolve(null);
		}
	}

	@Override
	public void schedule(int delayMillis) {
		if (defer == null) {
			defer = new Deferred<>();
			super.schedule(delayMillis);
		}
	}

	@Override
	public void scheduleRepeating(int periodMillis) {
		schedule(periodMillis);
	}

	@Override
	public void run() {
		cancel();
	}

	public Promise<Void> start(int millis) {
		schedule(millis);
		Promise<Void> promise = getPromise();
		agent.addBarrier(promise);
		return promise;
	}
	
}
