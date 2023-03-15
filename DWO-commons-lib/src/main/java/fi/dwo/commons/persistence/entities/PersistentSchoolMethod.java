package fi.dwo.commons.persistence.entities;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Entity
@Table(name = "tblschoolmethod")
@NamedQueries({
  @NamedQuery(name = "PersistentSchoolMethod.findAll", query = "SELECT p FROM PersistentSchoolMethod p"),
  @NamedQuery(name = "PersistentSchoolMethod.findBySchoolID", query = "SELECT p FROM PersistentSchoolMethod p WHERE p.id.schoolID = :schoolID")
})
public class PersistentSchoolMethod {
  @EmbeddedId
  private PersistentSchoolMethodPK id;

  @Column(name = "methodID", length=45)
  @Size(max=45)
  private String methodID;

  public PersistentSchoolMethod() {}

  public PersistentSchoolMethod(PersistentSchoolMethodPK id) {
    this.id = id;
  }

  public PersistentSchoolMethodPK getId() {
    return id;
  }

  public void setId(PersistentSchoolMethodPK id) {
    this.id = id;
  }

  public PersistenceId getMethodID() {
    if (methodID == null) return null;
    return new PersistenceId(methodID);
  }

  public void setMethodID(PersistenceId persistenceId) {
    this.methodID = Objects.toString(persistenceId, null);
  }

  public Long getSchoolID() {
    return id.getSchoolID();
  }

  public void setSchoolID(Long schoolID) {
    id.setSchoolID(schoolID);
  }

  public Long getModelID() {
    return id.getModelID();
  }

  public void setModelID(Long modelID) {
    id.setModelID(modelID);
  }

// standard stuff  
  @Column(name = "optlock")
  @Version 
  private Long optlock;
  @NotNull
  @Column(name= "del")
  private DelState delState = DelState.not;

  @Basic(optional = false)
  @Column(name = "lastChangeTimeStamp", nullable = true)
  private Long lastChangeTimeStamp;

  @PrePersist
  @PreUpdate
    private void now() {
      lastChangeTimeStamp = System.currentTimeMillis();
    }

  public Long getOptlock() {
    return optlock;
  }
  public void setOptlock(Long optlock) {
    this.optlock = optlock;
  }
  public DelState getDelState() {
    return delState;
  }
  public void setDelState(DelState delState) {
    this.delState = delState;
  }

  public DomSchoolMethod buildDomSchoolMethod() {
    DomSchoolMethod sm = new DomSchoolMethod();
    sm.setId(PersistentStudentModelContext.buildPersistenceId(getModelID()));
    sm.setOptLock(getOptlock());
    sm.setActiveMethod(getMethodID());
    return sm;
  }

}
