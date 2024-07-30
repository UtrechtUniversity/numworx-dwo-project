package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;

@XmlRootElement 
public class RestResultsPerTeacherv2 {
  
  public RestResultsPerTeacherv2() {}

  public RestResultsPerTeacherv2(DomContext restContext, DomDwoProfileId domDwoProfile,
      DomResultsPerTeacherv2 domResultsPerTeacher) {
    this.restContext = restContext;
    this.domDwoProfile = domDwoProfile;
    this.domResultsPerTeacher = domResultsPerTeacher;
  }

  private DomContext restContext;
  private DomDwoProfileId domDwoProfile;  
  private DomResultsPerTeacherv2 domResultsPerTeacher;

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
  public DomDwoProfileId getDomDwoProfile() {
    return domDwoProfile;
  }

  /**
   * @param domDwoProfile the domDwoProfile to set
   */
  public void setDomDwoProfile(DomDwoProfileId domDwoProfile) {
    this.domDwoProfile = domDwoProfile;
  }

  /**
   * @return the domResultsPerTeacher
   */
  public DomResultsPerTeacherv2 getDomResultsPerTeacher() {
    return domResultsPerTeacher;
  }

  /**
   * @param domResultsPerTeacher the domResultsPerTeacher to set
   */
  public void setDomResultsPerTeacher(DomResultsPerTeacherv2 domResultsPerTeacher) {
    this.domResultsPerTeacher = domResultsPerTeacher;
  }
  
  
}
