package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

@Entity
@Table(name = "tblcoursedata", schema = "")
public class PersistentCourseData implements PersistentEntity {
  @Id
  @Basic(optional = false)
  @Column(name = "courseID", nullable = false)
  private Long courseID;

  @Basic(optional = false)
  @Column(name = "lastChangeTimeStamp", nullable = true)
  private Long lastChangeTimeStamp;

//  For future use in case of optimistic locking.    
  @Version
  @Column(name = "optlock", columnDefinition = "integer DEFAULT 0", nullable = false)
  private Long optlock;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 0, max = 16777215)
  @Column(name = "description", nullable = false, length = 16777215)
  private String description;

  @Lob
  @Column(name = "imageData")
  private byte[] imageData;

  @Lob
  @Column(name = "descriptionbytes")
  private byte[] descriptionbytes;

  @NotNull
  @Column(name="del",nullable = false)
  private DelState delState = DelState.not;

  public void changeTimestamp() {
      lastChangeTimeStamp = System.currentTimeMillis();
  }

  /**
   * @return the courseID
   */
  public Long getCourseID() {
    return courseID;
  }

  /**
   * @param courseID the courseID to set
   */
  public void setCourseID(Long courseID) {
    this.courseID = courseID;
  }

  /**
   * @return the lastChangeTimeStamp
   */
  public Long getLastChangeTimeStamp() {
    return lastChangeTimeStamp;
  }

  /**
   * @param lastChangeTimeStamp the lastChangeTimeStamp to set
   */
  public void setLastChangeTimeStamp(Long lastChangeTimeStamp) {
    this.lastChangeTimeStamp = lastChangeTimeStamp;
  }

  /**
   * @return the optlock
   */
  public Long getOptlock() {
    return optlock;
  }

  /**
   * @param optlock the optlock to set
   */
  public void setOptlock(Long optlock) {
    this.optlock = optlock;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the imageData
   */
  public byte[] getImageData() {
    return imageData;
  }

  /**
   * @param imageData the imageData to set
   */
  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }

  /**
   * @return the descriptionbytes
   */
  public byte[] getDescriptionbytes() {
    return descriptionbytes;
  }

  /**
   * @param descriptionbytes the descriptionbytes to set
   */
  public void setDescriptionbytes(byte[] descriptionbytes) {
    this.descriptionbytes = descriptionbytes;
  }

  public void fillDomCourseStudent(DomCourseStudent course) {
    course.setDescription(getDescription());
    course.setImageData(getImageData());
  }
  
  public void fillDomCourseFull(DomCourseFull course) {
    fillDomCourseStudent(course);
  }
}
