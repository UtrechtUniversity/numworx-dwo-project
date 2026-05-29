package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class SchoolCache {

	private final static Cache<String, PersistentSchool> cache = initializeCache();

	private SchoolCache() {
	}

	private static Cache<String, PersistentSchool> initializeCache() {		
		try {
			return CacheUtilManager.createCache("PersistentSchool", String.class, PersistentSchool.class);
		} catch (Exception e) {
			return null;
		}
	}
	public static void put(PersistentSchool school) {
		if (cache != null) cache.put(school.getSchoolLogin(), school);
	}
	
	public static void putIfPresent(PersistentSchool school) {
		if (cache != null) cache.getAndReplace(school.getSchoolLogin(), school);
	}
	
	public static PersistentSchool get(String id) {
		if (cache != null) return cache.get(id);
		return null;
	}
	
	public static void remove(String id) {
		if (cache != null) cache.remove(id);
	}
	
	public static void remove(PersistentSchool s) {
		remove(s.getSchoolLogin());
	}

	static Cache<String, PersistentSchool> cache() {
		return cache;
	}

}
