package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface ScoContextManager {
  public DomScoContext get(DomScoContext domScoId, DomDwoProfile profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception;

  public List<DomScoContext> getScos(DomCourse course, DomDwoProfile profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception;

  public DomScoData getData(DomScoContextId domScoId, DomDwoProfileId profile, DomSchoolClassId schoolClass) throws Dwo2Exception;

  public List<DomScoContext> getTrash(DomCourse parent, DomDwoProfile profile) throws Dwo2Exception;
  
}
