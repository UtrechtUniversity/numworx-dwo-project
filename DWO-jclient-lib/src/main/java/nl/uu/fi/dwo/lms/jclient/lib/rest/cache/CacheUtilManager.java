package nl.uu.fi.dwo.lms.jclient.lib.rest.cache;

import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;


public class CacheUtilManager {

	static CacheManager manager;
	
	private CacheUtilManager() {
	}

	static private CacheManager initCache() {
		if (manager == null) {
			CachingProvider cachingProvider = Caching.getCachingProvider();
			Properties properties = new Properties(cachingProvider.getDefaultProperties());
//voor memcache-jcache
//			String servers = System.getProperty("MEMCACHED", "test:localhost:11211");
//			servers = servers.substring(servers.indexOf(':')+1);
//			properties.setProperty(PublicProfileCache.NAME + ".servers", servers);
			manager = cachingProvider.getCacheManager(null, null, properties);
		}
		return manager;
	}
	
	public static <K, V> Cache<K,V> createCache(String NAME, Class<K> key, Class<V> value) {
		CacheManager cacheManager = initCache();
		Properties properties = cacheManager.getProperties();
// Voor MEMCACHED
//		properties.putIfAbsent(NAME + ".servers", properties.getProperty(PublicProfileCache.NAME + ".servers"));
		Cache<K, V> _instance = cacheManager.getCache(NAME, key, value);
		if (_instance == null) {
			MutableConfiguration<K, V> conf;
			conf = new MutableConfiguration<K, V>().setTypes(key, value);
			conf.setStoreByValue(false);
			conf.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_DAY));
			_instance = cacheManager.createCache(NAME, conf);
		}
		return _instance;
	}
}
