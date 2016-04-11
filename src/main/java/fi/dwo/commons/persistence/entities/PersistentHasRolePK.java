/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Primary key for PersistentHasRole entity.
 *
 * {
 *
 * @see PersistentHasRole}
 *
 * @author G.A.J. van der Plas
 */
@Embeddable
public class PersistentHasRolePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "userID", nullable = false)
    private Long userID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolGroupID", nullable = false)
    private Long schoolGroupID;

    public long getId() {
        long id = schoolGroupID;
        id = id << 32;
        id = id & userID;
        return (id);
    }

    public PersistentHasRolePK() {
    }

    public PersistentHasRolePK(Long userID, Long schoolGroupID) {
        this.userID = userID;
        this.schoolGroupID = schoolGroupID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getSchoolGroupID() {
        return schoolGroupID;
    }

    public void setSchoolGroupID(Long schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) (userID % ((long) Integer.MAX_VALUE));
        hash += (int) (schoolGroupID % ((long) Integer.MAX_VALUE));
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentHasRolePK)) {
            return false;
        }
        PersistentHasRolePK other = (PersistentHasRolePK) object;
        if ((long) this.userID != (long) other.userID) {
            return false;
        }
        if ((long) this.schoolGroupID != (long) other.schoolGroupID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentHasRolePK[ userID=" + userID + ", schoolGroupID=" + schoolGroupID + " ]";
    }

}
