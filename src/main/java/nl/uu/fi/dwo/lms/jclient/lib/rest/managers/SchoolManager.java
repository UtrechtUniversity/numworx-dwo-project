package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface SchoolManager {
  public Boolean updateSchool(DomSchoolFull submit) throws Dwo2Exception;

}
