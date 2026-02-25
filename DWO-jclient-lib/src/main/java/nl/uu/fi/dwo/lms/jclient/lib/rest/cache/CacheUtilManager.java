package nl.uu.fi.dwo.lms.jclient.lib.rest.cache;

import java.util.Properties;
import java.util.function.Supplier;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;


public class CacheUtilManager {

	static CacheManager manager;
	private volatile static Supplier<CachingProvider> supplier = Caching::getCachingProvider;
	
	// This must be public and unobfuscated
	public synchronized static void setCachingProvider(Supplier<CachingProvider> getter) {
		supplier = getter;
	}
	
	private CacheUtilManager() {
	}

	static private CacheManager initCache() {
		if (manager == null && supplier != null) 
		synchronized(CacheUtilManager.class)
		{
			try {
				CachingProvider cachingProvider = supplier.get();
				if (cachingProvider == null) return null;
				Properties properties = new Properties(cachingProvider.getDefaultProperties());
				// last change for configuration....
				manager = cachingProvider.getCacheManager(null, CacheUtilManager.class.getClassLoader(), properties);
			} finally  {
				supplier = null;
			}
		}
		return manager;
	}
	
	public static <K, V> Cache<K,V> createCache(String NAME, Class<K> key, Class<V> value) {
		CacheManager cacheManager = initCache();
		//Properties properties = cacheManager.getProperties(); if more configuration needed
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
