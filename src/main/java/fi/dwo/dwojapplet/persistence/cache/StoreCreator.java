package fi.dwo.dwojapplet.persistence.cache;

import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoProfile;
import fi.dwo.dwojapplet.persistence.DbAccessCreator;
import javax.swing.JOptionPane;

public class StoreCreator {

    private static IStore _instance;

    public static synchronized IStore instance() {
        if (_instance == null) {
// implement read only profile			
            if (DwoProfile.hasRight(DwoProfile.READONLY)) {
                JOptionPane.showMessageDialog(DwoHelper.getApplet(), "Pas op: werk wordt niet opgeslagen", "DWO is READ ONLY", JOptionPane.WARNING_MESSAGE);
                return _instance = new ReadOnly(DbAccessCreator.instance());
            }

            //_instance = new NoCache(DbAccessCreator.instance());
            _instance = new CachingStore(DbAccessCreator.instance());
        }
        return _instance;
    }

    public static synchronized void destroy() {
        if (_instance != null) {
            IStore store = _instance;
            _instance = null;
            store.destroy();
        }
    }
}
