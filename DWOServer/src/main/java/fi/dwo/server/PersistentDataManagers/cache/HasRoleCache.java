package fi.dwo.server.PersistentDataManagers.cache;

import java.security.Principal;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

public class HasRoleCache {
	
	private static Logger LOG = Logger.getLogger(HasRoleCache.class.getName());
	private static Cache<PersistentHasRolePK, PersistentHasRole> cache = initializeCache();

	private HasRoleCache() {
	}

	private static Cache<PersistentHasRolePK, PersistentHasRole> initializeCache() {
		try {
			return CacheUtilManager.createCache("PersistentHasRole", PersistentHasRolePK.class, PersistentHasRole.class);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "no cache for HasRole", e);
			return null;
		}
	}

	public static void put(PersistentHasRole role) {
		if (cache != null)
		{	
			cache.put(role.getPersistentHasRolePK(), role);
		}
	}
	
	public static PersistentHasRole get(Object roleid) {
		if (cache == null || ! (roleid instanceof PersistentHasRolePK)) return null;
		try {
			return cache.get((PersistentHasRolePK) roleid);
		} catch (Exception e) {
			return null; // never crash on cache
		}
	}
	
	public static void remove(PersistentHasRolePK id) {
		if (cache != null)
			cache.remove(id);
	}

	public static Cache<PersistentHasRolePK, PersistentHasRole> cache() {
		return cache;
	}

	public static void remove(SecurityContext sc) {
		Principal principal = sc.getUserPrincipal();
		if (principal instanceof DwoUserPrincipal) {
			DwoUserPrincipal user = (DwoUserPrincipal) principal;
			remove (user.getHr());
		}
	}

	public static void remove(PersistentHasRole hr) {
		if (hr != null) remove(hr.getPersistentHasRolePK());
	}
	
	public static void remove(PersistentSchool school) {
		Long id = school.getSchoolID();
		if (cache != null) {
			Iterator<Cache.Entry<PersistentHasRolePK,PersistentHasRole>> iter = cache.iterator();
			while(iter.hasNext()) {
				Cache.Entry<PersistentHasRolePK,PersistentHasRole> entry = iter.next();
				PersistentHasRole role = entry.getValue();
				if (id.intValue() == role.getSchoolGroup().getSchoolID())
					iter.remove();
			}
		}
	}
}
