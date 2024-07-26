package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class LoginContextCache {

	private final static Cache<Long, PersistentLoginContext> cache = initializeCache();

	private LoginContextCache() {
	}

	private static Cache<Long, PersistentLoginContext> initializeCache() {		
		return CacheUtilManager.createCache("PersistentLoginContext", Long.class, PersistentLoginContext.class);
	}
	public static void put(PersistentLoginContext role) {
		cache.put(role.getId(), role);
	}
	
	public static PersistentLoginContext get(Long id) {
		return cache.get(id);
	}
	
	public static void remove(Long id) {
		cache.remove(id);
	}

	public static Cache<Long, PersistentLoginContext> cache() {
		return cache;
	}

}
