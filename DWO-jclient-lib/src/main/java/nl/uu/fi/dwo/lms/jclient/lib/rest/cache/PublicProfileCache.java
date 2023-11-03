package nl.uu.fi.dwo.lms.jclient.lib.rest.cache;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicProfileCache {
	
	private static final String NAME = "DomDwoProfileFull";

	private PublicProfileCache() {
	}

	static volatile private Cache<String, DomDwoProfileFull> _instance;

	static synchronized Cache<String, DomDwoProfileFull> cache() {
		if (_instance == null) {
			_instance = Caching.getCache(NAME, String.class, DomDwoProfileFull.class);
			if (_instance == null) {
				Configuration conf;
				conf = new MutableConfiguration<String, DomDwoProfileFull>().setTypes(String.class, DomDwoProfileFull.class);
				_instance = Caching.getCachingProvider().getCacheManager().createCache(NAME, conf);
			}
		}
		return _instance;
	}

	public static DomDwoProfileFull get(String name) throws Dwo2Exception {
		DomDwoProfileFull result = null;
		try {
			result = cache().get(name);
		} catch (Exception e) {
		}
		if (result == null) {
			result = PublicProfileManager.get(name);
			try {
				cache().put(name, result);
			} catch (Exception e) {
			}
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
		cache().clear();
		
	}

}
