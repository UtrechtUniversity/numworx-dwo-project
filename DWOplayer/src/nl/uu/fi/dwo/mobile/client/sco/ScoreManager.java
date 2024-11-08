package nl.uu.fi.dwo.mobile.client.sco;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.ResettableEventBus;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.dagger.ActivityScope;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;

@ActivityScope
public class ScoreManager implements ScoreWidgetIF, CBookEventListener {
	
	final Scorm2004IF delegate; // not sure 
	final EventBus bus;
	final Optional<DwoGlobalVars> vars;
	
	Map<String, Promise<Map<String,String>>> cache;

	@Inject ScoreManager(@Named("API") Scorm2004IF api, ResettableEventBus bus, Optional<DwoGlobalVars> instance) {
		this.delegate = api;
		this.bus = bus; // 
		this.vars = instance;
		if (instance.isPresent()) {
			cache = instance.get().getScoreCache();
		}
		bus.addHandler(CBookEvent.TYPE, this);
	}

	@Override
	public String SetValue(String name, String value) {
		return delegate.SetValue(name, value);
	}

	@Override
	public String GetValue(String name) {
		return delegate.GetValue(name);
	}

	@Override
	public Promise<String> Commit() {
		return delegate.Commit();
	}

	static class Mapper implements Function<List<Object>, Map<String,String>>, Runnable, Failure {
		final Collection<String> names;
		private Deferred<Map<String, String>> defer;
		private HashMap<String, String> result;

		public Mapper(Collection<String> names, Deferred<Map<String,String>> defer) {
			this.names = names;
			this.defer = defer;
		}

		@Override
		public Map<String, String> apply(List<Object> t) {
			result = new HashMap<>();
			for (Object o: t) {
				@SuppressWarnings("unchecked")
				Map<String,String> m = (Map<String,String>) o;
				result.putAll(m);
			}
			result.keySet().retainAll(names);
			return result;
		}

		@Override
		public void run() {
			if (!defer.getPromise().isDone()) 
				defer.resolve(result);
		}

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			defer.fail(resolved.getFailure());			
		}
	}
	
	@Override
	public Promise<Map<String, String>> getValuesPromise(Collection<String> names) {
		if (cache == null) return delegate.getValuesPromise(names);
		
		List<String> set = new ArrayList<>(names);
		Map<String,Promise<Map<String,String>>> all = new LinkedHashMap<>();
		Promise<Map<String, String>>result;
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			result = cache.get(name);
			if(result != null) {
				all.put(name, result);
				iter.remove();
			}
		}
		if (!set.isEmpty()) {
			result = delegate.getValuesPromise(set);
			for(String name: set) cache.put(name, result);
			all.put(null, result);
		}
		Deferred<Map<String,String>> defer = new Deferred<Map<String,String>>();
		Mapper mapper = new Mapper(names, defer);
		Promises.all(all.values()).map(mapper).then(null, mapper).onResolve(mapper); // must be Asynchroon.
		return defer.getPromise();
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if ("setChanged".equals(event.getCommand())) {
			ObjectMap parameters = JSONUtilities.wrapMap(event.getParameters());
			String unitId = parameters.getString("unitId");
			int location = parameters.getInt("location");
			String dot = "." + String.valueOf(location+1) + ".id";
			// find unit id in de cache
			Collection<Promise<Map<String, String>>> collection = cache.values();
			for(Promise<Map<String, String>> promise : collection) {
				if (promise.isDone() && promise.getFailure() == null) {
					Map<String, String> map = promise.getValue();
					Optional<String> key = map.keySet().stream().filter(t -> t.endsWith(dot)).findAny();
					Optional<String> value = key.map(k -> map.get(k)).filter(v -> unitId.equals(v));
					if (value.isPresent()) {
// int score.raw, Boolean success
						String k = key.get().substring(0,key.get().length()-2);
						Boolean b = (Boolean) parameters.get("success");
						map.computeIfPresent(k+"success_status", (kk,v) -> {
							if (b == null) return "";
							if (b.booleanValue()) return "passed";
							return "failed";
						});
						map.computeIfPresent(k + "score.raw", (kk,v) -> String.valueOf(parameters.getInt("score.raw")));
						ObjectList l = parameters.getObjectList("visited");
						map.computeIfPresent(k + "entry", (kk,v) -> (l == null || l.size() > 0) ? "ab-initio" : "resume" );
						// visited list/null/empty-list
						// entry ab-initio	resume					
						
					}
				}
			}
			
		}
		
	}
}
