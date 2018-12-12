package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;

@XmlRootElement 
public class RestResultsPerTeacher {
  
  public RestResultsPerTeacher() {}

  public RestResultsPerTeacher(DomContext restContext, DomDwoProfile domDwoProfile,
      DomResultsPerTeacher domResultsPerTeacher) {
    this.restContext = restContext;
    this.domDwoProfile = domDwoProfile;
    this.domResultsPerTeacher = domResultsPerTeacher;
  }

  private DomContext restContext;
  private DomDwoProfile domDwoProfile;
  
  private DomResultsPerTeacher domResultsPerTeacher;

  /**
   * @return the restContext
   */
  public DomContext getRestContext() {
    return restContext;
  }

  /**
   * @param restContext the restContext to set
   */
  public void setRestContext(DomContext restContext) {
    this.restContext = restContext;
  }

  /**
   * @return the domDwoProfile
   */
  public DomDwoProfile getDomDwoProfile() {
    return domDwoProfile;
  }

  /**
   * @param domDwoProfile the domDwoProfile to set
   */
  public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
    this.domDwoProfile = domDwoProfile;
  }

  /**
   * @return the domResultsPerTeacher
   */
  public DomResultsPerTeacher getDomResultsPerTeacher() {
    return domResultsPerTeacher;
  }

  /**
   * @param domResultsPerTeacher the domResultsPerTeacher to set
   */
  public void setDomResultsPerTeacher(DomResultsPerTeacher domResultsPerTeacher) {
    this.domResultsPerTeacher = domResultsPerTeacher;
  }
  
  
}
