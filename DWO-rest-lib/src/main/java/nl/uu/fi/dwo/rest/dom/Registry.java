package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Object Registry. Primarily for service factory as an alternative for
 * singleton instances. In such a case the name is the full package name of the
 * object.
 *
 * @author Gert van der Plas
 */
public class Registry {

    private static final Logger LOG = Logger.getLogger(SessionedRegistry.class.getName());

    private static Registry instance;

    private final Map<String, Object> registry = new HashMap<String, Object>();

    public static synchronized Registry getInstance() {
        if (instance == null) {
            instance = new Registry();
        }
        return instance;
    }

    public synchronized Object getObject(
            final String name) {
        final Object result;
        if (isRegistered(name)) {
            result = registry.get(name);
        } else {
            return null;
        }
        return result;
    }

    public boolean isRegistered(final String name) {
        return registry.containsKey(name);
    }

    public synchronized void register(final Object object) {

        String key = object.getClass().getName();

        if (!registry.containsKey(key) || registry.get(key) != object) {
            registry.put(key, object);
        }
    }

    public synchronized void unregister(final String key) {
        registry.remove(key);
    }
}

