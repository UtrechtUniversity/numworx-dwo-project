package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherClassCourseRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.entities.RestClassCourseFull;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredTeacherClassCourseManager {

  private static final SecuredTeacherClassCourseRestCaller service = GWT.create(SecuredTeacherClassCourseRestCaller.class);

  public Promise<DomClassCourseFull> update(DomContext context, DomClassCourseFull dom) {
    PromiseCallback<DomClassCourseFull> defer = new PromiseCallback<>();
    RestClassCourseFull rest = new RestClassCourseFull();
    rest.setRestContext(context);
    rest.setDomCourse(dom);
    service.update(PathId.getId(context), rest, defer);
    return defer.getPromise();
  }
}
