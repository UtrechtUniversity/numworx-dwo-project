package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureUserResultsManager {

  private static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }
  
  public static List<DomStudentScoContext> getCourseResults(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
    RestCourse rest = new RestCourse();
    DomContext context = getContext();
    rest.setDomDwoProfile(profile);
    rest.setRestContext(context);
    rest.setDomCourse(course);
    List<DomStudentScoContext> result;
    
    result = StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(context) + "/user/results/getCourseResults", RestListClassTypes.DomStudentScoContext, rest);
    
    return result;
  }
  
}
