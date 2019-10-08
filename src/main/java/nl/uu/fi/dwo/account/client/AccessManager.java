package nl.uu.fi.dwo.account.client;

import java.util.List;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;

public class AccessManager implements Function<List<DomCourseStudent>, List<DomCourseStudent>> {

  public static final Promise<Boolean> TRUE = Promises.resolved(Boolean.TRUE);

  Promise<Boolean> access(DomCourseStudent course) {
    return TRUE;
  }

  @Override
  public List<DomCourseStudent> apply(List<DomCourseStudent> t) {
    return t;
  }
   
}
