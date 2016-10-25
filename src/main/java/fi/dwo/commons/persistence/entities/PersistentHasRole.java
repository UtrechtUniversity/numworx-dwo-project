/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblhasrole", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"schoolGroupID", "userID"})})
@NamedQueries({
    @NamedQuery(name = "PersistentHasRole.findAll", query = "SELECT p FROM PersistentHasRole p"),
    @NamedQuery(name = "PersistentHasRole.findByUserID", query = "SELECT p FROM PersistentHasRole p WHERE p.persistentHasRolePK.userID = :userID"),
    @NamedQuery(name = "PersistentHasRole.findByClassID", query = "SELECT p FROM PersistentHasRole p WHERE p.classID = :classID"),
    @NamedQuery(name = "PersistentHasRole.findBySchoolGroupID", query = "SELECT p FROM PersistentHasRole p WHERE p.persistentHasRolePK.schoolGroupID = :schoolGroupID"),
    @NamedQuery(name = "PersistentHasRole.findByRegisterDate", query = "SELECT p FROM PersistentHasRole p WHERE p.registerDate = :registerDate"),
    @NamedQuery(name = "PersistentHasRole.findByRights", query = "SELECT p FROM PersistentHasRole p WHERE p.rights = :rights"),
    @NamedQuery(name = "PersistentHasRole.findByLastLogin", query = "SELECT p FROM PersistentHasRole p WHERE p.lastLogin = :lastLogin")})
//@Cache( type=CacheType.SOFT, // Cache everything until the JVM decides memory is low. 
//        size=10000, // Use 64,000 as the initial cache size. 
//        expiry=36000000 // 10 minutes 
//)
public class PersistentHasRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersistentHasRolePK persistentHasRolePK;
    @Column(name = "classID")
    private Long classID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classID", insertable = false, updatable = false)
    private PersistentSchoolClass schoolClass;
    @Basic(optional = false)
    @Column(name = "registerDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registerDate;
    @Column(name = "rights", length = 100)
    private String rights;
    @Column(name = "lastLogin")
    @Temporal(TemporalType.DATE)
    private Date lastLogin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schoolGroupID", insertable = false, updatable = false)
    private PersistentSchoolGroup schoolGroup;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", insertable = false, updatable = false)
    private PersistentUser user;

    public PersistentHasRole() {
    }

    public PersistentHasRole(PersistentHasRolePK persistentHasRolePK) {
        this.persistentHasRolePK = persistentHasRolePK;
    }

    public PersistentHasRole(PersistentHasRolePK persistentHasRolePK, Date registerDate) {
        this.persistentHasRolePK = persistentHasRolePK;
        this.registerDate = registerDate;
    }

    public PersistentHasRole(Long userID, Long schoolGroupID) {
        this.persistentHasRolePK = new PersistentHasRolePK(userID, schoolGroupID);
    }

    public PersistentHasRolePK getPersistentHasRolePK() {
        return persistentHasRolePK;
    }

    public void setPersistentHasRolePK(PersistentHasRolePK persistentHasRolePK) {
        this.persistentHasRolePK = persistentHasRolePK;
    }

    public Long getClassID() {
        return classID;
    }

    public void setClassID(Long classID) {
        this.classID = classID;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (persistentHasRolePK != null ? persistentHasRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentHasRole)) {
            return false;
        }
        PersistentHasRole other = (PersistentHasRole) object;
        return !((this.persistentHasRolePK == null && other.persistentHasRolePK != null)
                || (this.persistentHasRolePK != null
                && !this.persistentHasRolePK.equals(other.persistentHasRolePK)));
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentHasRole[ persistentHasRolePK=" + persistentHasRolePK + " ]";
    }

    /**
     * @return the schoolClass
     */
    public PersistentSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @return the schoolGroup
     */
    public PersistentSchoolGroup getSchoolGroup() {
        return schoolGroup;
    }

    /**
     * @return the user
     */
    public PersistentUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(PersistentUser user) {
        this.user = user;
    }

    public DomHasRole buildDomHasRole() {
        DomHasRole hr = new DomHasRole();
        if (this.persistentHasRolePK != null) {
            hr.setId(MySQLPersistenceId.createPersistentId((PersistentHasRole) this));
            hr.setSchoolGroupId(MySQLPersistenceId.createPersistenceId(this.persistentHasRolePK.getSchoolGroupID().longValue(), PersistenceClassType.PersistentSchoolGroup));
            hr.setUserId(MySQLPersistenceId.createPersistenceId(this.persistentHasRolePK.getUserID().longValue(), PersistenceClassType.PersistentUser));
        }
        hr.setRights(this.getRights());
        return hr;
    }

}
