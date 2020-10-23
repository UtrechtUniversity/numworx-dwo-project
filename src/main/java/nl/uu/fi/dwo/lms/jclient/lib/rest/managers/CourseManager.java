package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;


public interface CourseManager {

  DomCourseFull update(DomCourseFull edit) throws Dwo2Exception;

  DomCourseFull add(DomCourseFull edit) throws Dwo2Exception;

  Boolean remove(DomCourse course, DomDwoProfile profile) throws Dwo2Exception;

  Boolean trash(DomCourse c, DomDwoProfile dwoProfile) throws Dwo2Exception;

}
