package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class DwoSystemParametersCache {
	private final static Cache<String, PersistentDwoSystemParameters> cache = initializeCache();

	private DwoSystemParametersCache() {
	}

	private static Cache<String, PersistentDwoSystemParameters> initializeCache() {		
		return CacheUtilManager.createCache("PersistentDwoSystemParameters", String.class, PersistentDwoSystemParameters.class);
	}

	public static void put(PersistentDwoSystemParameters parameter) {
		cache.put(parameter.getName(), parameter);
	}
	
	public static void putIfPresent(PersistentDwoSystemParameters parameter) {
		cache.getAndReplace(parameter.getName(), parameter);
	}
	
	public static PersistentDwoSystemParameters get(String id) {
		return cache.get(id);
	}
	
	public static void remove(String id) {
		cache.remove(id);
	}

	public static Cache<String, PersistentDwoSystemParameters> cache() {
		return cache;
	}

}
