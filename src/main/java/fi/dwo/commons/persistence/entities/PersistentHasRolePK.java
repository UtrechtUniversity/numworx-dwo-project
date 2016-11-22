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
        if (!(object instanceof PersistentHasRolePK)) {
            return false;
        }
        PersistentHasRolePK other = (PersistentHasRolePK) object;
        //test for null due to constructor.
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }        
        //test for null due to constructor.
        if ((this.schoolGroupID == null && other.schoolGroupID != null) || (this.schoolGroupID != null && !this.schoolGroupID.equals(other.schoolGroupID))) {
            return false;
        }        
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentHasRolePK[ userID=" + userID + ", schoolGroupID=" + schoolGroupID + " ]";
    }

}
