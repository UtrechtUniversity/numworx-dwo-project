package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SecureDwoAdminCourseManager extends AbstractCourseManager {

  public SecureDwoAdminCourseManager(StoredRestManager manager) {
    super(manager);
    // TODO Auto-generated constructor stub
  }
  
  public SecureDwoAdminCourseManager() {
    this(StoredRestManager.getInstance());
  }

  @Override
  public DomCourseFull update(DomCourseFull edit) throws Dwo2Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DomCourseFull add(DomCourseFull edit) throws Dwo2Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean remove(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
    // TODO Auto-generated method stub
    return null;
  }


}
