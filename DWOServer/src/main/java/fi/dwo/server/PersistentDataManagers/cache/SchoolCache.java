package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class SchoolCache {

	private final static Cache<String, PersistentSchool> cache = initializeCache();

	private SchoolCache() {
	}

	private static Cache<String, PersistentSchool> initializeCache() {		
		return CacheUtilManager.createCache("PersistentSchool", String.class, PersistentSchool.class);
	}
	public static void put(PersistentSchool school) {
		cache.put(school.getSchoolLogin(), school);
	}
	
	public static void putIfPresent(PersistentSchool school) {
		cache.getAndReplace(school.getSchoolLogin(), school);
	}
	
	public static PersistentSchool get(String id) {
		return cache.get(id);
	}
	
	public static void remove(String id) {
		cache.remove(id);
	}

	public static Cache<String, PersistentSchool> cache() {
		return cache;
	}

}
