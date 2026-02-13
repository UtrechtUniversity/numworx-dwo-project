package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class LoginContextCache {

	private final static Cache<Long, PersistentLoginContext> cache = initializeCache();

	private LoginContextCache() {
	}

	private static Cache<Long, PersistentLoginContext> initializeCache() {		
		try {
			return CacheUtilManager.createCache("PersistentLoginContext", Long.class, PersistentLoginContext.class);
		} catch (Exception e) {
			return null;
		}
	}
	public static void put(PersistentLoginContext role) {
		if (cache != null)
			cache.put(role.getId(), role);
	}
	
	public static void putIfPresent(PersistentLoginContext role) {
		if (cache != null)
			cache.getAndReplace(role.getId(), role);
	}
	
	public static PersistentLoginContext get(Long id) {
		if (cache == null) return null;
		return cache.get(id);
	}
	
	public static void remove(Long id) {
		if (cache != null)
			cache.remove(id);
	}

	public static Cache<Long, PersistentLoginContext> cache() {
		return cache;
	}

}
