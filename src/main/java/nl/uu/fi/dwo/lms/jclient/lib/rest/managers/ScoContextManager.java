package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface ScoContextManager {
  public DomScoContext get(DomScoContext domScoId, DomDwoProfile profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception;

  public List<DomScoContext> getScos(DomCourse course, DomDwoProfile profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception;

}
