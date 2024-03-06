package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public abstract class AbstractCourseManager implements CourseManager {
  final StoredRestManager manager;

  AbstractCourseManager(StoredRestManager manager) {
    this.manager = manager;
  }

  DomContext getContext() {
    return manager.getContext();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager#update(nl.uu.fi.dwo.rest.dom.entities.
   * DomCourseFull)
   */
  @Override
  public abstract DomCourseFull update(DomCourseFull edit) throws Dwo2Exception;

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager#add(nl.uu.fi.dwo.rest.dom.entities.
   * DomCourseFull)
   */
  @Override
  public abstract DomCourseFull add(DomCourseFull edit) throws Dwo2Exception;

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.uu.fi.dwo.lms.jclient.lib.rest.managers.CourseManager#remove(nl.uu.fi.dwo.rest.dom.entities.
   * DomCourse, nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile)
   */
  @Override
  public abstract Boolean remove(DomCourse course, DomDwoProfile profile) throws Dwo2Exception;
}
