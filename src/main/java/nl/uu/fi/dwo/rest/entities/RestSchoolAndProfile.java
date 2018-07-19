package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAndProfile;

public class RestSchoolAndProfile {
    DomContext restContext;
    DomSchoolAndProfile domSchoolAndProfile;
 
    public DomContext getRestContext() {
      return restContext;
    }
    public void setRestContext(DomContext restContext) {
      this.restContext = restContext;
    }
    public DomSchoolAndProfile getDomSchoolAndProfile() {
      return domSchoolAndProfile;
    }
    public void setDomSchoolAndProfile(DomSchoolAndProfile domSchoolAndProfile) {
      this.domSchoolAndProfile = domSchoolAndProfile;
    }
}
