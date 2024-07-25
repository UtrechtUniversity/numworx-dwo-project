package fi.dwo.server.PersistentDataManagers.cache;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;

public class HasRoleCacheManager {
	
	private static Map<PersistentHasRolePK, PersistentHasRole> cache = Collections.synchronizedMap(new WeakHashMap<>());

	private HasRoleCacheManager() {
	}

	public static void put(PersistentHasRole role) {
		cache.put(role.getPersistentHasRolePK(), role);
	}
	
	public static PersistentHasRole get(PersistentHasRolePK id) {
		return cache.get(id);
	}
	
	public static void remove(PersistentHasRolePK id) {
		cache.remove(id);
	}
}
