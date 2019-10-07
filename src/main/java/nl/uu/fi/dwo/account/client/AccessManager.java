package nl.uu.fi.dwo.account.client;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;

public class AccessManager {

  public static final Promise<Boolean> TRUE = Promises.resolved(Boolean.TRUE);

  Promise<Boolean> access(DomCourseStudent course) {
    return TRUE;
  }
}
