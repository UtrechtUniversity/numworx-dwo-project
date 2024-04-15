package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherClassCourseRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestClassCourseFull;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredTeacherClassCourseManager {

  private static final SecuredTeacherClassCourseRestCaller service = GWT.create(SecuredTeacherClassCourseRestCaller.class);

  public Promise<DomClassCourseFull> update(DomContext context, DomClassCourseFull dom) {
    RestClassCourseFull rest = new RestClassCourseFull();
    rest.setRestContext(context);
    rest.setDomCourse(dom);
    return F(service::update,PathId.getId(context), rest);
  }
  
  public Promise<String> getSettingsUI(DomContext context, DomClassCourse dom, DomDwoProfile profile) {
	  RestClassCourse rest = new RestClassCourse();
	  rest.setRestContext(context);
	  rest.setDomClassCourse(dom);
	  rest.setDomDwoProfile(profile);
	  return F(service::getSettingsUI, PathId.getId(context), rest).map((JSONValue v) -> v.isObject().get("url").isString().stringValue());
  }

  public Promise<String> getDashboardUI(DomContext context, DomClassCourse dom, DomDwoProfile profile) {
	  RestClassCourse rest = new RestClassCourse();
	  rest.setRestContext(context);
	  rest.setDomClassCourse(dom);
	  rest.setDomDwoProfile(profile);
	  return F(service::getDashboardUI, PathId.getId(context), rest).map((JSONValue v) -> v.isObject().get("url").isString().stringValue());
  }
 
}
