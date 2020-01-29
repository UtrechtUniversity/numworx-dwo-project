package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.rest.RestListClassTypes;
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

  /**
   * Singleton pattern.
   * 
   * @return the instance
   */
  public static StoredRestManager getInstance() {
    return storedInstance;
  }

  @FunctionalInterface interface DwoSupplier<T> {
	  T accept() throws Dwo2Exception;
  }
  
  private <T> T run( DwoSupplier<T> result) throws Dwo2Exception {
	  try {
		return result.accept();
	} catch (Dwo2Exception e) {
		// inspect E, if Authenticated error, try re-authenticate and again
		throw e;
	}
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

}
