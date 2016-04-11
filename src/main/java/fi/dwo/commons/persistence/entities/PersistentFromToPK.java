/**
 * Copyrighted Jul 20, 2015
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author G.A.J. van der Plas
 */
@Embeddable
public class PersistentFromToPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolFrom")
    private Long schoolFrom;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolTo")
    private Long schoolTo;

    public PersistentFromToPK() {
    }

    public PersistentFromToPK(Long schoolFrom, Long schoolTo) {
        this.schoolFrom = schoolFrom;
        this.schoolTo = schoolTo;
    }

    public Long getSchoolFrom() {
        return schoolFrom;
    }

    public void setSchoolFrom(Long schoolFrom) {
        this.schoolFrom = schoolFrom;
    }

    public Long getSchoolTo() {
        return schoolTo;
    }

    public void setSchoolTo(Long schoolTo) {
        this.schoolTo = schoolTo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) (schoolFrom % ((long) Integer.MAX_VALUE));
        hash += (int) (schoolTo % ((long) Integer.MAX_VALUE));
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentFromToPK)) {
            return false;
        }
        PersistentFromToPK other = (PersistentFromToPK) object;
        if ((long) this.schoolFrom != (long) other.schoolFrom) {
            return false;
        }
        if ((long) this.schoolTo != (long) other.schoolTo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.PersistentEntityManagers.TblfromtoPK[ schoolFrom=" + schoolFrom + ", schoolTo=" + schoolTo + " ]";
    }

}
