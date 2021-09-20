package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PersistentSchoolMethodPK {
  @Basic(optional = false)
  @Column(name = "schoolID", nullable = false)
  private Long schoolID;

  @Basic(optional = false)
  @Column(name = "modelID", nullable = false)
  private Long modelID;

  public Long getSchoolID() {
    return schoolID;
  }

  public void setSchoolID(Long schoolID) {
    this.schoolID = schoolID;
  }

  public Long getModelID() {
    return modelID;
  }

  public void setModelID(Long modelID) {
    this.modelID = modelID;
  }

  public PersistentSchoolMethodPK(Long schoolID, Long modelID) {
    this.schoolID = schoolID;
    this.modelID = modelID;
  }

  public PersistentSchoolMethodPK() {
  }
  
}
