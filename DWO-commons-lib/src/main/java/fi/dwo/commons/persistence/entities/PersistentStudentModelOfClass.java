package fi.dwo.commons.persistence.entities;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

@Entity
@Table(name = "tblstudentmodelof", schema = "", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"classID", "modelID"})})
@NamedQueries({
  @NamedQuery(name = "PersistentStudentModelOfClass.findAll", query = "SELECT p FROM PersistentStudentModelOfClass p"),
  @NamedQuery(name = "PersistentStudentModelOfClass.findByModelID", query = "SELECT p FROM PersistentStudentModelOfClass p WHERE p.id.modelID = :modelID and p.id.schoolID = :schoolID"),
  @NamedQuery(name = "PersistentStudentModelOfClass.findByClassID", query = "SELECT p FROM PersistentStudentModelOfClass p WHERE p.id.classID = :classID and p.id.schoolID = :schoolID"),
  @NamedQuery(name = "PersistentStudentModelOfClass.findBySchoolID", query = "SELECT p FROM PersistentStudentModelOfClass p WHERE p.id.schoolID = :schoolID")})
public class PersistentStudentModelOfClass {

  
  @EmbeddedId
  private PersistentStudentModelOfClassPK id;

  @Column(name = "optlock")
  @Version
  private Long optlock;
  @Column(name = "lastChangeTimeStamp")
  private long lastChangeTimeStamp;

  @PrePersist
  @PreUpdate
  void changeTimestamp() {
      lastChangeTimeStamp = System.currentTimeMillis();
  }

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 0, max = 65535)
  @Column(name = "value", nullable = false, length = 65535)
  private String value = "{}";

  @NotNull
  @Column(name="del",nullable = false)
  private DelState delState = DelState.not;

  public PersistentStudentModelOfClassPK getId() {
    return id;
  }

  public void setId(PersistentStudentModelOfClassPK id) {
    this.id = id;
  }

  public Long getOptlock() {
    return optlock;
  }

  public void setOptlock(Long optlock) {
    this.optlock = optlock;
  }

  public long getLastChangeTimeStamp() {
    return lastChangeTimeStamp;
  }

  public void setLastChangeTimeStamp(long lastChangeTimeStamp) {
    this.lastChangeTimeStamp = lastChangeTimeStamp;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public DelState getDelState() {
    return delState;
  }

  public void setDelState(DelState delState) {
    this.delState = delState;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PersistentStudentModelOfClass other = (PersistentStudentModelOfClass) obj;
    return Objects.equals(id, other.id);
  }

  
}
