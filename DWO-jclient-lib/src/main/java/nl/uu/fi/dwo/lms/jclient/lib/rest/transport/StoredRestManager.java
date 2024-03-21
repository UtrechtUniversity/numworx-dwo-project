package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * StoredRestManager ensures there is only one copy or each rest-fetched persistent object. It
 * checks whether or not the class name of an object starts with
 * "fi.dwo.commons.persistence.entities". Currently storing is disabled!
 *
 * @author G.A.J. van der Plas
 */
@Singleton
public class StoredRestManager extends RestManager {

  @Inject
  public StoredRestManager(RestAuthenticator authenticator) {
    super(authenticator);
  }

  private static final Logger LOG = Logger.getLogger(StoredRestManager.class.getName());

  private static final StoredRestManager storedInstance =
      new StoredRestManager(RestAuthenticator.getInstance());

  private StoredRestManager(StoredRestManager org) {
	  super(org);
	  recover = org.recover;
  }
  
  public StoredRestManager duplicate() {
	  return new StoredRestManager(this);
  }
  
  /**
   * Singleton pattern.
   * 
   * @return the instance
   */
  public static StoredRestManager getInstance() {
    return storedInstance;
  }

  @FunctionalInterface public interface DwoSupplier<T> {
	  T accept() throws Dwo2Exception;
  }
  
  static final Predicate<Dwo2Exception> FALSE = (e) -> false;
  
  private Predicate<Dwo2Exception> recover = FALSE;
  
  public void setRecover(Predicate<Dwo2Exception> recover) {
    if (recover == null) recover = FALSE;
    this.recover = recover;
  }


  private <T> T run(DwoSupplier<T> result) throws Dwo2Exception {
    do {
      try {
        return result.accept();
      } catch (Dwo2Exception e) {
        boolean test = false;
        try {
          test = recover.test(e);
        } catch (RuntimeException e1) {
          if (e1.getCause() instanceof Dwo2Exception)
            e = (Dwo2Exception) e1.getCause();
        }
        if (!test) throw e;
      }
    } while (true);
  }
  
  
@Override
public <T> T get(String path, Class<T> c) throws Dwo2Exception {
	return run (() -> super.get(path, c));
}

@Override
public <T> List<T> getList(String path, RestListClassTypes type) throws Dwo2Exception {
	return run (() -> super.getList(path, type));
}

@Override
public <T> T put(String path, Class<T> c, Object o) throws Dwo2Exception {
	return run(() -> super.put(path, c, o));
}

@Override
public <T> List<T> getPutList(String path, RestListClassTypes type, Object o) throws Dwo2Exception {
	return run(() -> super.getPutList(path, type, o));
}

public DomContext getContext() {
	return getAuthenticator().getContext();
}

}
