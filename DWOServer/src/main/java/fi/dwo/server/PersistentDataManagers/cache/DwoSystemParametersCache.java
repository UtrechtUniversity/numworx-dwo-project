package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class DwoSystemParametersCache {
	private final static Cache<String, PersistentDwoSystemParameters> cache = initializeCache();

	private DwoSystemParametersCache() {
	}

	private static Cache<String, PersistentDwoSystemParameters> initializeCache() {		
		try {
			return CacheUtilManager.createCache("PersistentDwoSystemParameters", String.class, PersistentDwoSystemParameters.class);
		} catch (Exception e) {
			return null;
		}
	}

	public static void put(PersistentDwoSystemParameters parameter) {
		if (cache != null) cache.put(parameter.getName(), parameter);
	}
	
	public static void putIfPresent(PersistentDwoSystemParameters parameter) {
		if (cache != null) cache.getAndReplace(parameter.getName(), parameter);
	}
	
	public static PersistentDwoSystemParameters get(String id) {
		if (cache != null) return cache.get(id);
		return null;
	}
	
	public static void remove(String id) {
		if (cache != null) cache.remove(id);
	}

	static Cache<String, PersistentDwoSystemParameters> cache() {
		return cache;
	}

	public static void clear() {
		if (cache != null) cache.clear();
		
	}

}
