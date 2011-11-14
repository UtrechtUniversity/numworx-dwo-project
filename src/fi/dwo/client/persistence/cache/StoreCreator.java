package fi.dwo.client.persistence.cache;

import fi.dwo.client.persistence.DbAccessCreator;

public class StoreCreator {

	private static IStore _instance;
	
	public static synchronized IStore instance() {
		if(_instance == null)
		{
			//_instance = new NoCache(DbAccessCreator.instance());
			_instance = new CachingStore(DbAccessCreator.instance());
		}
		return _instance;
	}
	
	public static synchronized void destroy() {
		if (_instance != null)
		{
			IStore store = _instance;
			_instance = null;
			store.destroy();
		}
	}
}
