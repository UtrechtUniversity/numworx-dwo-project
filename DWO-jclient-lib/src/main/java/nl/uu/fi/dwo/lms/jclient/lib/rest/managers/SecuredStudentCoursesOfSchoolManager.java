package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentCoursesOfSchoolManager {

  public static 
  DomCoursesOfSchoolClass getCoursesClass(DomSchoolClass schoolClass, DomDwoProfile profile) throws Dwo2Exception {
    
    DomCoursesOfSchoolClass result;
    RestSchoolClassAndProfile rest = new RestSchoolClassAndProfile();
    DomSchoolClassAndProfile  dom  = new DomSchoolClassAndProfile();
    DomContext context = StoredRestManager.getInstance().getContext();
    rest.setRestContext(context);
    rest.setDomSchoolClassAndProfile(dom);
    dom.setDomDwoProfile(profile);
    dom.setDomSchoolClass(schoolClass);
   
    result = StoredRestManager.getInstance().put("rest/sec:"+PathId.getId(context)+"/student/coursesofschoolclass/get", DomCoursesOfSchoolClass.class, rest);
    
    return result;
    
  }
}
