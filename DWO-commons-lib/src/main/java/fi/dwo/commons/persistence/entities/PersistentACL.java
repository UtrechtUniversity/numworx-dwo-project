package fi.dwo.commons.persistence.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Entity
@Table(name = "tblacl", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"courseID", "entity", "dwoProfileID", "schoolID"})})
@NamedQueries({
  @NamedQuery(name = "PersistentACL.findByCourseID", query = "SELECT p FROM PersistentACL p WHERE p.courseID = :courseID"),
  @NamedQuery(name = "PersistentACL.findBySchoolIDProfileID", query = "SELECT p FROM PersistentACL p WHERE p.schoolID = :schoolID AND p.dwoProfileID = :profileID")
})
public class PersistentACL implements Serializable, PersistentEntity {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "aclID", nullable = false)
  private Long aclID;
  
  @Column(name = "courseID", nullable = false)
  private Long courseID;
  @Column(name = "schoolID")
  private Long schoolID;

  @Basic(optional = false)
  @NotNull
  @Column(name = "dwoProfileID", nullable = false)
  private Long dwoProfileID;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "entity", nullable = false, length = 50)
  private String entity;

  @Basic(optional = false)
  @Column(name = "lastChangeTimeStamp", nullable = true)
  private Long lastChangeTimeStamp;

  @Column(name = "optlock")
  @Version 
  private Long optlock;
  @NotNull
  @Column(name= "access")
  private ACL access = ACL.NONE;
  
  public void changeTimestamp() {
    lastChangeTimeStamp = System.currentTimeMillis();
  }
  
  
  /**
   * Builds a persistenceId from the parameters given.
   *
   * @param aCourseId nullable
   * @return
   */
  public static PersistenceId buildPersistenceId(Long aclId) {
      if(aclId == null) return null;
      PersistenceId id = new PersistenceId();
      id.setIdString(String.format("MYSQL;%s;%020d",
              PersistenceClassType.PersistentACL.name(), aclId));
      return id;
  }
  /**
   * Builds a PersistenceId using this object's data.
   *
   * @return
   */
  public PersistenceId buildPersistenceId() {
      return buildPersistenceId(aclID);
  }

  public DomACL buildDomACL() {
    DomACL domACL = new DomACL();
    fillDomACL(domACL);
    return domACL;
  }
  
  private void fillDomACL(DomACL dom) {
    dom.setId(buildPersistenceId());
    dom.setAccess(access);
    dom.setEntity(new PersistenceId(entity));
    dom.setOptLock(optlock);    
  }

  public Long getAclID() {
    return aclID;
  }
  public void setAclID(Long aclID) {
    this.aclID = aclID;
  }
  public Long getCourseID() {
    return courseID;
  }
  public void setCourseID(Long courseID) {
    this.courseID = courseID;
  }
  public Long getSchoolID() {
    return schoolID;
  }
  public void setSchoolID(Long schoolID) {
    this.schoolID = schoolID;
  }
  public Long getDwoProfileID() {
    return dwoProfileID;
  }
  public void setDwoProfileID(Long dwoProfileID) {
    this.dwoProfileID = dwoProfileID;
  }
  public String getEntity() {
    return entity;
  }
  public void setEntity(String entity) {
    this.entity = entity;
  }
  public Long getLastChangeTimeStamp() {
    return lastChangeTimeStamp;
  }
  public void setLastChangeTimeStamp(Long lastChangeTimeStamp) {
    this.lastChangeTimeStamp = lastChangeTimeStamp;
  }
  public Long getOptlock() {
    return optlock;
  }
  public void setOptlock(Long optlock) {
    this.optlock = optlock;
  }
  public ACL getAccess() {
    return access;
  }
  public void setAccess(ACL access) {
    this.access = access;
  }
  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  
}
