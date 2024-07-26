package fi.dwo.server.PersistentDataManagers.cache;

import javax.cache.Cache;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class HasRoleCache {
	
	private static Cache<PersistentHasRolePK, PersistentHasRole> cache = initializeCache();

	private HasRoleCache() {
	}

	private static Cache<PersistentHasRolePK, PersistentHasRole> initializeCache() {
		return CacheUtilManager.createCache("PersistentHasRole", PersistentHasRolePK.class, PersistentHasRole.class);
	}

	public static void put(PersistentHasRole role) {
		if (cache != null)
			cache.put(role.getPersistentHasRolePK(), role);
	}
	
	public static PersistentHasRole get(PersistentHasRolePK id) {
		if (cache == null) return null;
		return cache.get(id);
	}
	
	public static void remove(PersistentHasRolePK id) {
		if (cache != null)
			cache.remove(id);
	}

	public static Cache<PersistentHasRolePK, PersistentHasRole> cache() {
		return cache;
	}
}
