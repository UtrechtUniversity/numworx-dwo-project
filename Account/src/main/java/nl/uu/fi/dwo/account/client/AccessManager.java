package nl.uu.fi.dwo.account.client;

import java.util.List;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

public class AccessManager implements Function<List<DomCourseStudent>, List<DomCourseStudent>>, Success<List<DomCourseStudent>, List<DomCourseStudent>> {

  public static final Promise<Boolean> TRUE = Promises.resolved(Boolean.TRUE);

  Promise<Boolean> access(DomCourseStudent course) {
    return TRUE;
  }

  @Override
  public List<DomCourseStudent> apply(List<DomCourseStudent> t) {
    return t;
  }

  @Override
  public Promise<List<DomCourseStudent>> call(Promise<List<DomCourseStudent>> resolved)
      throws Exception {
    apply(resolved.getValue());
    return resolved;
  }

  // Deeplink, throw Dwo2Exception.Rest_LoginNeeded
  
  public Promise<DomCourseStudent> single(Promise<DomCourseStudent> resolved) {
    return resolved;
  }

  public Promise<DomScoContext> sco(Promise<DomScoContext> resolved) {
    return resolved;
  }
  
}
