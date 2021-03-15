package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class PersistentStudentModelOfClassPK {

  public PersistentStudentModelOfClassPK(Long classID, Long modelID, Long schoolID) {
    this.classID = classID;
    this.modelID = modelID;
    this.schoolID = schoolID;
  }
 
  public PersistentStudentModelOfClassPK() {
  }


  @Basic(optional = false)
  @NotNull
  @Column(name = "classID", nullable = false)
  private Long classID;
  @Basic(optional = false)
  @Column(name = "modelID", nullable = false)
  @NotNull
  private Long modelID;
  @Basic(optional = false)
  @Column(name = "schoolID", nullable = false)
  @NotNull
  private Long schoolID;
  public Long getClassID() {
    return classID;
  }
  public void setClassID(Long classID) {
    this.classID = classID;
  }
  public Long getModelID() {
    return modelID;
  }
  public void setModelID(Long modelID) {
    this.modelID = modelID;
  }
  public Long getSchoolID() {
    return schoolID;
  }
  public void setSchoolID(Long schoolID) {
    this.schoolID = schoolID;
  }

}
