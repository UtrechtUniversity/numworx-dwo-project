package nl.uu.fi.dwo.lms.jclient.lib.rest.cache;

import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicProfileCache {
	
	static final String NAME = "DomDwoProfileFull";

	private PublicProfileCache() {
	}

	static volatile private Cache<String, DomDwoProfileFull> _instance;

	static synchronized Cache<String, DomDwoProfileFull> cache() {
		if (_instance == null) {
				_instance = CacheUtilManager.createCache(NAME, String.class, DomDwoProfileFull.class);

		}
		return _instance;
	}

	public static DomDwoProfileFull get(String name) throws Dwo2Exception {
		DomDwoProfileFull result = getFromCache(name);
		if (result == null) {
			try {
				result = PublicProfileManager.get(name);
				putInCache(name, result);
			} catch (Exception e) {
			}
		}
		return result;
	}

	public static void putInCache(String name, DomDwoProfileFull result) {
		try {
			cache().put(name, result);
		} catch (Exception e) {
		} catch (Error oops) {
			oops.printStackTrace();
		}
	}

	public static DomDwoProfileFull getFromCache(String name) {
		DomDwoProfileFull result = null;
		try {
			result = cache().get(name);
		} catch (Exception e) {
		} catch (Error oops) {
			oops.printStackTrace();
		}
		return result;
	}

	public static DomDwoProfileFull get(long id) throws Dwo2Exception {
		return get(Long.toString(id));
	}

	public static DomDwoProfileFull get(int id) throws Dwo2Exception {
		return get(Integer.toString(id));
	}

	public static void clear() {
		try {
			cache().clear();
		} catch (Exception e) {
		} catch (Error oops) {
			oops.printStackTrace();
		}
	}

}
