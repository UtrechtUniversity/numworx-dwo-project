package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

@Singleton
public class TrafficAgent implements Failure, Success<Object, Void> { 

	List<Promise<?>> list = new ArrayList<>();
	private Deferred<Void> defer;
	
	@Inject public TrafficAgent() {
	}

	@SuppressWarnings("unchecked")
	public Promise<Void> barrier() {
		if(defer != null) {
			return defer.getPromise();
		}
		defer = new Deferred<>();
		@SuppressWarnings("rawtypes")
		Collection list = copy(this.list);
		Promise<Void> promise = defer.getPromise();
		Promises.all(list).then(this, this);
		return promise;
	}
	
	@SuppressWarnings("rawtypes")
	private Collection copy(List<Promise<?>> list) {
		if(list == null||list.isEmpty())
			return Collections.EMPTY_SET;
		List<Promise<?>> result = new ArrayList<Promise<?>>();
		Iterator<Promise<?>> i = list.iterator();
 		while (i.hasNext()) {
			Promise<?> promise = (Promise<?>) i.next();
			if( promise.isDone())
				i.remove();
			else
				result.add(promise);
			
		}
		return result;
	}

	public void addBarrier(Promise<?> p) {
		list.add(p);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Promise<Void> call(Promise<Object> resolved) throws Exception {
		Collection leftover = copy(list);
		if(leftover.isEmpty())
		{	Deferred<Void> d = defer;
			defer = null;
			if (!d.getPromise().isDone()) d.resolve(null);
			return d.getPromise();
		}
		return Promises.all(leftover).then(this, this);
	}

	@Override
	public void fail(Promise<?> resolved) throws Exception {
		
		Promises.all(copy(list)).then(this, this);
		
	}

	public void reset(Throwable t ) {
		//if (defer == null) defer = new Deferred<>();
		list.clear();
		if (defer != null && !defer.getPromise().isDone()) defer.fail(t);
	}

	
}
