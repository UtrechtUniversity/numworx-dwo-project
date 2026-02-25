/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblschoolgroup", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSchoolGroup.findAll", query = "SELECT p FROM PersistentSchoolGroup p"),
    @NamedQuery(name = "PersistentSchoolGroup.findBySchoolGroupID", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.schoolGroupID = :schoolGroupID"),
    @NamedQuery(name = "PersistentSchoolGroup.findByGroupID", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.groupID = :groupID"),
    @NamedQuery(name = "PersistentSchoolGroup.findBySchoolID", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentSchoolGroup.findBySchoolIDAndRole", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.schoolID = :schoolID and p.groupID = :groupID"),
    @NamedQuery(name = "PersistentSchoolGroup.findByPasswd", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.passwd = :passwd")})
//@Cache( type=CacheType.SOFT, // Cache everything until the JVM decides memory is low. 
//        size=10000, // Use 64,000 as the initial cache size. 
//        expiry=36000000 // 10 minutes 

public class PersistentSchoolGroup implements Serializable, PersistentEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "schoolGroupID", nullable = false)
    private Long schoolGroupID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "groupID", nullable = false)
    private int groupID;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @PrimaryKeyJoinColumn(name = "groupID")
    @Transient
    private PersistentRole role;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolID", nullable = false)
    private int schoolID;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "schoolID")
    private PersistentSchool school;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "passwd", nullable = false, length = 128)
    private String passwd;
    @Column(name = "optlock")
    @Version Long optlock;

    @Basic(optional = false)
    @Column(name = "lastChangeTimeStamp", nullable = true)
    private long lastChangeTimeStamp;

    public void changeTimestamp() {
      lastChangeTimeStamp = System.currentTimeMillis();
    }
    @PostLoad
    private void loadRoleOnLoad() {
    		getRole();
    }
    
    
    public PersistentSchoolGroup() {
    }

    public PersistentSchoolGroup(Long schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    public PersistentSchoolGroup(Long schoolGroupID, int groupID, int schoolID, String passwd) {
        this.schoolGroupID = schoolGroupID;
        this.groupID = groupID;
        this.schoolID = schoolID;
        this.passwd = passwd;
    }

    public Long getSchoolGroupID() {
        return schoolGroupID;
    }

    public void setSchoolGroupID(Long schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (schoolGroupID != null ? schoolGroupID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchoolGroup)) {
            return false;
        }
        PersistentSchoolGroup other = (PersistentSchoolGroup) object;
        if ((this.schoolGroupID == null && other.schoolGroupID != null) || (this.schoolGroupID != null && !this.schoolGroupID.equals(other.schoolGroupID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSchoolGroup[ schoolGroupID=" + schoolGroupID + " ]";
    }

    /**
     * @return the school
     */
    public PersistentSchool getSchool() {
        return school;
    }

    public RoleType getRoleType() {
    	try {
    		return RoleType.values()[groupID];
    	} catch(Exception oops) {
    		return RoleType.NONE;
    	}
    }
    
    /**
     * @return the role
     * @Deprecated
     */
    public PersistentRole getRole() {
        try {
			return role = PersistentRole.roles[groupID];
		} catch (Exception e) {
			return null;
		}
    }

    /**
     * @param role the role to set
     */
    public void setRole(PersistentRole role) {
    	this.role = role;
    	if (role != null) {
    		groupID = role.getGroupID().intValue();
    	} else 
    		groupID = 0;
        
    }

   /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(schoolGroupID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aSchoolGroupId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aSchoolGroupId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentSchoolGroup.name(), aSchoolGroupId));
        return id;
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

}
