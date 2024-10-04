package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class SchoolDataCache {

	private final static Cache<Long, PersistentSchoolData> cache = initializeCache();

	private SchoolDataCache() {
	}

	private static Cache<Long, PersistentSchoolData> initializeCache() {		
		return CacheUtilManager.createCache("PersistentSchoolData", Long.class, PersistentSchoolData.class);
	}
	public static void put(PersistentSchoolData role) {
		cache.put(role.getSchoolID(), role);
	}
	
	public static void putIfPresent(PersistentSchoolData role) {
		cache.getAndReplace(role.getSchoolID(), role);
	}
	
	public static PersistentSchoolData get(Long id) {
		return cache.get(id);
	}
	
	public static void remove(Long id) {
		cache.remove(id);
	}

	public static Cache<Long, PersistentSchoolData> cache() {
		return cache;
	}

}
