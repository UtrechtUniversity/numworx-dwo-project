package nl.uu.fi.dwo.lms.jclient.lib.rest.cache;

import javax.cache.Cache;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class PublicProfileCache {
	
	static final String NAME = "DomDwoProfileFull";
	static final DomDwoProfileFull NULL = new DomDwoProfileFull(); // no == only equals

	private PublicProfileCache() {
	}

	static volatile private Cache<String, DomDwoProfileFull> _instance;

	public static synchronized Cache<String, DomDwoProfileFull> cache() {
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
			} catch(Dwo2Exception ex) {
				if (ex.getDwo2Code() == Dwo2ExceptionCode.Rest_ResourceNotFound)
					putInCache(name, NULL);				
			} catch (Exception|NoClassDefFoundError e) {
			}
		} else if (result.getId() == null)
			return null;		
		return result;
	}

	public static void putInCache(String name, DomDwoProfileFull result) {
		try {
			cache().put(name, result);
		} catch (Exception|NoClassDefFoundError e) {
		} catch (Error oops) {
			oops.printStackTrace();
		}
	}

	public static boolean containsKey(String name) {
		try {
			return cache().containsKey(name);
		} catch(Throwable oops) {
			return false;
		}
	}
	
	public static DomDwoProfileFull getFromCache(String name) {
		DomDwoProfileFull result = null;
		try {
			result = cache().get(name);
		} catch (Exception|NoClassDefFoundError e) {
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
		} catch (Exception|NoClassDefFoundError e) {
		} catch (Error oops) {
			oops.printStackTrace();
		}
	}

}
