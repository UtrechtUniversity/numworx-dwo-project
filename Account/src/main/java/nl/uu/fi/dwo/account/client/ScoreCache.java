package nl.uu.fi.dwo.account.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ScoreCache {

	public ScoreCache() {
		cache = new LinkedHashMap<>();
	}
	
	private PersistenceId cs, cc;
	private Map<String, Promise<Map<String,String>>> cache; // cache with global singleton scope

	public Promise<Map<String, String>> get(String name) {
		return cache.get(name);
	}

	public Promise<Map<String, String>> put(String name, Promise<Map<String, String>> result) {
		return cache.put(name, result);		
	}

	public Collection<Promise<Map<String, String>>> values() {
		return cache.values();
	}

	public Optional<ScoreCache> optional() {
		if (cache.isEmpty()) return Optional.empty();
		return Optional.of(this);
	}

	private void init(DomScoContext context) {
		cs = context.getId();
		cc = context.getCourseId();
	}
	
	private void init(DomCourse context) {
		cs = null;
		cc = context.getId();
	}
	
	public void init(Object original) {
		if (original instanceof DomScoContext) init( (DomScoContext) original);
		else if (original instanceof DomCourse) init( (DomCourse) original);
		else {
			cs = null;
			cc = null;
		}
	}
	
}
