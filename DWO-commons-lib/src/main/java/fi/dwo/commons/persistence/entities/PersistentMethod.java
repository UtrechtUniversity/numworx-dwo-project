package fi.dwo.commons.persistence.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;

import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Entity
@Table(name = "tblmethod", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentMethod.findAll", query = "SELECT p FROM PersistentMethod p"),
    @NamedQuery(name = "PersistentMethod.findBySchoolID", query = "SELECT q FROM PersistentMethod q WHERE q.schoolID = 0 OR q.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentMethod.findBySchoolIDorProfile", query = "SELECT q FROM PersistentMethod q WHERE (q.dwoProfileID = :dwoProfileID and q.schoolID = 0) OR q.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentMethod.findBySchoolIDandProfile", query = "SELECT q FROM PersistentMethod q WHERE q.dwoProfileID = :dwoProfileID and (q.schoolID = 0 OR q.schoolID = :schoolID)")
})
@Cache( type=CacheType.SOFT, // Cache everything until the JVM decides memory is low. 
        size=10, // Use 64,000 as the initial cache size. 
        expiry=36000000 // 10 minutes 
)
public class PersistentMethod implements Serializable {

    public PersistentMethod() {
      dwoProfileID = Long.valueOf(77); // FIXME temporaly
    }

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "methodID", nullable = false)
    private String methodID;
    @Column(name = "schoolID", nullable = false)
    private Long schoolID;
    @Column(name = "optlock")
    @Version 
    private Long optlock;
    @NotNull
    @Column(name= "del")
    private DelState delState = DelState.not;
 
    @Basic(optional = false)
    @Column(name = "lastChangeTimeStamp", nullable = true)
    private Long lastChangeTimeStamp;
    @Column(name = "dwoProfileID")
    private Long dwoProfileID;

    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 0, max = 16777215)
    @Column(name = "method", nullable = false, length = 16777215)
    private String method;

    
    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aCourseId nullable
     * @return persistenceId
     */
    public PersistenceId buildPersistenceId() {
        if(getMethodID() == null) return null;
        PersistenceId id = new PersistenceId(getMethodID());
        return id;
    }

    public String getMethodID() {
      return methodID;
    }
    public void setMethodID(String methodID) {
      this.methodID = methodID;
    }
    public Long getSchoolID() {
      return schoolID;
    }
    public void setSchoolID(Long schoolID) {
      this.schoolID = schoolID;
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
    public String getMethod() {
      return method;
    }
    public void setMethod(String method) {
      this.method = method;
    }

    @PrePersist
    @PreUpdate
      private void now() {
        lastChangeTimeStamp = System.currentTimeMillis();
      }

    public Long getDwoProfileID() {
      return dwoProfileID;
    }

    public void setDwoProfileID(Long dwoProfileID) {
      this.dwoProfileID = dwoProfileID;
    }

}
