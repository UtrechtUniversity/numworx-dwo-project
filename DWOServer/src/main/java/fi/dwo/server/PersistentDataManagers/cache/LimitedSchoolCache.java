package fi.dwo.server.PersistentDataManagers.cache;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.cache.Cache;

import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.CacheUtilManager;

@SuppressWarnings("rawtypes")
public class LimitedSchoolCache {

	private final static Cache<Long, Set> cache = initializeCache();

	private LimitedSchoolCache() {		
	}
	
	private static Cache<Long, Set> initializeCache() {
		return CacheUtilManager.createCache("LimitedSchoolCache", Long.class, Set.class);
	}

	public static boolean isLimitedSchool(Long profileID, Long schoolID) {
		Set set = cache.get(profileID);
		if (set == null)
			set = initCache(profileID);
		if (set != null) {
			return set.contains(schoolID);
		}
		return true;
	}

	// read properties from ${CDN}/resources/schools-<profileID>.properties;
	private static Set initCache(Long profileID) {
		try {
			Set set = new HashSet();
			URL u;
			u = new URL(System.getProperty("CDNURL", "http://cdn.dwo.nl")+ "/resources/schools-"+profileID + ".properties");
			Properties p = new Properties();
			p.load(u.openStream());
			int number = Integer.parseInt(p.getProperty("number"));
			for (int i = 1 ; i <= number; i++) {
				if ("true".equals(p.getProperty("access."+i))) {
					long schoolid = Long.parseLong(p.getProperty("school."+i));
					set.add(schoolid);
				}
			}
			cache.put(profileID, set);
			return set;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
