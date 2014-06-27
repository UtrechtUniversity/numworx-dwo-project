package fi.dwo.client.persistence.cache;

import javax.swing.JOptionPane;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.persistence.DbAccessCreator;

public class StoreCreator {

	private static IStore _instance;
	
	public static synchronized IStore instance() {
		if(_instance == null)
		{
// implement read only profile			
			if(DwoProfile.hasRight(DwoProfile.READONLY))
			{
				JOptionPane.showMessageDialog(DwoHelper.getApplet(), "Pas op: werk wordt niet opgeslagen", "DWO is READ ONLY", JOptionPane.WARNING_MESSAGE);
				return _instance = new ReadOnly(DbAccessCreator.instance());
			}

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
