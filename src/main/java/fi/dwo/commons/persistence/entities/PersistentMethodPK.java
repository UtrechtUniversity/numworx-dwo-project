package fi.dwo.commons.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PersistentMethodPK {
  @Column(name = "schoolID", nullable = false)
  private Long schoolID;
  
  @Column(name = "methodID", nullable = false)
  private String methodID;

  /**
   * @return the methodID
   */
  public String getMethodID() {
    return methodID;
  }

  /**
   * @param methodID the methodID to set
   */
  public void setMethodID(String methodID) {
    this.methodID = methodID;
  }

  /**
   * @return the schoolID
   */
  public Long getSchoolID() {
    return schoolID;
  }

  /**
   * @param schoolID the schoolID to set
   */
  public void setSchoolID(Long schoolID) {
    this.schoolID = schoolID;
  }
  
}
