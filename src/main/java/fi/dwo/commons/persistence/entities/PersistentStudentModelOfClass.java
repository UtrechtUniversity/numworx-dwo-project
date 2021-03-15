package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
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

  
}
