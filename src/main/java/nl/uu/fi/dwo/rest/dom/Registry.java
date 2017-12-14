package nl.uu.fi.dwo.rest.dom;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class Registry {

    private static final Logger LOG = Logger.getLogger(DwoFactoryRegistry.class.getName());
 
  private static Registry instance;
 
  private final Map<String,Reference> registry = new HashMap<String,Reference>();
 
  public static synchronized Registry getInstance() {
    if (instance == null) {
      instance = new Registry();
    }
    return instance;
  }
 
  public synchronized Reference getReference(
      final String name) {
    final Reference result;
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
//    final Reference reference = getReference(
//        object.getName());
//    if (!reference.hasObject() 
//        || reference.getObject() != object) {
//      reference.setObject(object);
//    }
  }
 
  public synchronized void unregister(
      final Object object) { //should be Reference<T>
      
//    if (isRegistered(object.getName())) {
//      final Reference reference = getReference(
//          object.getName());
//      if (reference.hasObject()) {
//        reference.setObject(null);
//      }
//    }
  }
 
}