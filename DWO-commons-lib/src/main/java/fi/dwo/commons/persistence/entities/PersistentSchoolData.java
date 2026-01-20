/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolDataFull;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblschooldata", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSchoolData.findAll", query = "SELECT p FROM PersistentSchoolData p"),
    @NamedQuery(name = "PersistentSchoolData.findBySchoolID", query = "SELECT p FROM PersistentSchoolData p WHERE p.schoolID = :schoolID")
})
 //@Cache( type=CacheType.SOFT, // Cache everything until the JVM decides memory is low. 
//        size=10000, // Use 64,000 as the initial cache size. 
//        expiry=36000000 // 10 minutes 
//)
public class PersistentSchoolData implements Serializable, PersistentEntity {

    private static final long serialVersionUID = 1L;
    @Id
    private Long schoolID;

    @Column(name = "optlock")
    @Version 
    private Long optlock;
    
    @NotNull
    @Column(name= "del")
    private DelState delState = DelState.not;
 
    @Column(name = "lastChangeTimeStamp")
    private long lastChangeTimeStamp;

    public void changeTimestamp() {
        lastChangeTimeStamp = System.currentTimeMillis();
    }
    
    @Column(name="schoolData")
    private String schoolData = "{}";
    
    public PersistentSchoolData() {
    }

    public PersistentSchoolData(Long schoolID) {
        this.schoolID = schoolID;
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

    public String getSchoolData() {
		return schoolData;
	}

	public void setSchoolData(String schoolData) {
		this.schoolData = schoolData;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (schoolID != null ? schoolID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchoolData)) {
            return false;
        }
        PersistentSchoolData other = (PersistentSchoolData) object;
        if ((this.schoolID == null && other.schoolID != null) || (this.schoolID != null && !this.schoolID.equals(other.schoolID))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSchoolData[ schoolID=" + schoolID + " ]";
    }

   /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(schoolID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aSchoolId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aSchoolId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentSchoolData.name(), aSchoolId));
        return id;
    }

    public DomSchoolDataFull buildDomSchoolDataFull() {
    	DomSchoolDataFull data = new DomSchoolDataFull();
    	fillDomSchoolDataFull(data);
    	return data;
    }

	private void fillDomSchoolDataFull(DomSchoolDataFull data) {
		data.setId(buildPersistenceId());
		data.setOptLock(getOptlock());
		data.setSchoolData(getSchoolData());	
	}
    
    
}
