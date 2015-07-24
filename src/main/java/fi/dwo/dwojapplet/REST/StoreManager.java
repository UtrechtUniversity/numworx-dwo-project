/*Copyrighted 2015. */
package fi.dwo.dwojapplet.REST;

import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.PersistenceIdFactory;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Store manager. Reduces multiple copies of data.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class StoreManager {

    protected static final Logger LOG = Logger.getLogger(StoreManager.class.getName());

    protected static final StoreManager instance = new StoreManager();

    //TODO Replace by SoftReferencesHashMap.
    private final static WeakHashMap<PersistenceId, Object> store = new WeakHashMap<PersistenceId, Object>();

//    /**
//     * @return the instance
//     */
//    public static StoreManager getInstance() {
//        return instance;
//    }

    /**
     * Puts an object in the store.
     *
     * @param o Persistent object to be merge into the store.
     * @return 
     */
    public synchronized static <T> T insertOrUpdate(T o) {
        PersistenceId key = PersistenceIdFactory.createPersistenceId((Object) o);
        if (store.containsKey(key)) {
            T cached = (T) store.get(key);
            if (o == cached) {
                return o;
            } else {
                if(o.equals(cached)){
                //TODO REST
                // cached.mergeAllFrom(o);
                // store.put(key, cached);
                    store.put(key, o); // The wrong implementation.
                    return o;
                }else{
                    LOG.log(Level.SEVERE," Can't merge to different data types");
                }
            }
        } else {
            store.put(key, o);
        }
        return o;
    }

    /**
     * Retrieves an object from the storeManager's store.
     *
     * @param <T> Class type to fetch
     * @param id
     * @return
     */
    public synchronized static <T> T getCached(PersistenceId id) {
        return (T) store.get(id);
    }

}
