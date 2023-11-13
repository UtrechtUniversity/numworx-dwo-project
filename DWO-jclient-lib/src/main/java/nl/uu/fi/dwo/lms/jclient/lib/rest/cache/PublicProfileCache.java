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
	
	private static final String NAME = "DomDwoProfileFull";

	private PublicProfileCache() {
	}

	static volatile private Cache<String, DomDwoProfileFull> _instance;

	static synchronized Cache<String, DomDwoProfileFull> cache() {
		if (_instance == null) {
			CachingProvider cachingProvider = Caching.getCachingProvider();
			Properties properties = new Properties(cachingProvider.getDefaultProperties());
//voor memcache 
			String servers = System.getProperty("MEMCACHED", "test:localhost:11211");
			servers = servers.substring(servers.indexOf(':')+1);
			properties.setProperty(NAME + ".servers", servers);
			CacheManager cacheManager = cachingProvider.getCacheManager(null, null, properties);
			_instance = cacheManager.getCache(NAME, String.class, DomDwoProfileFull.class);
			if (_instance == null) {
				MutableConfiguration<String, DomDwoProfileFull> conf;
				conf = new MutableConfiguration<String, DomDwoProfileFull>().setTypes(String.class, DomDwoProfileFull.class);
// voor memcache
				conf.setStoreByValue(false);
				conf.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_DAY));
				
				_instance = cacheManager.createCache(NAME, conf);
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
		try {
			cache().clear();
		} catch (Exception e) {
		}		
	}

}
