package nl.uu.fi.dwo.rest.dom;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A Dwo factory registry. This singleton class stores references to factories
 * based on a session-key.
 *
 * Its purpose is to support multiple factories within a single app, each
 * related to a unique session. This would allow to instance multiple
 * applications with different global configurations.
 *
 * @author Gert van der Plas
 */
public class SessionedRegistry {

    private static final Logger LOG = Logger.getLogger(SessionedRegistry.class.getName());

    private static SessionedRegistry instance;
    private static int sessionId = 0;

    private final List<Registry> sessionedRegistry = new ArrayList<Registry>();

    public static synchronized SessionedRegistry getInstance() {
        if (instance == null) {
            instance = new SessionedRegistry();
        }
        return instance;
    }

    public synchronized int getNumberOfSessions() {
        return sessionedRegistry.size();
    }

    public synchronized int newSession(Registry registry) {
        sessionedRegistry.add(registry);
        return sessionedRegistry.size() - 1;
    }

    public synchronized void removeSession(int sessionId) {
        sessionedRegistry.remove(sessionId);
    }

    public synchronized Registry getRegistry(int sessionId) {
        if (sessionId >= 0 && sessionId < sessionedRegistry.size()) {
            return sessionedRegistry.get(sessionId);
        } else {
            return null;
        }
    }

    private boolean isRegistered(int sessionId, final String name) {
        return sessionedRegistry.get(sessionId).isRegistered(name);
    }


}
