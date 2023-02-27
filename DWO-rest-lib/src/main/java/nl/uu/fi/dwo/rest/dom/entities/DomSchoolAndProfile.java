package nl.uu.fi.dwo.rest.dom.entities;

public class DomSchoolAndProfile {
  private DomSchoolId domSchool;
  private DomDwoProfile domDwoProfile;
  public DomSchoolId getDomSchool() {
    return domSchool;
  }
  public void setDomSchool(DomSchoolId domSchool) {
    this.domSchool = domSchool;
  }
  public DomDwoProfile getDomDwoProfile() {
    return domDwoProfile;
  }
  public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
    this.domDwoProfile = domDwoProfile;
  }
  
}
