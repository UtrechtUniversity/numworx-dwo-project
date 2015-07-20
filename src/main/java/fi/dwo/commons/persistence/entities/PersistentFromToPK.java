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
    private int schoolFrom;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolTo")
    private int schoolTo;

    public PersistentFromToPK() {
    }

    public PersistentFromToPK(int schoolFrom, int schoolTo) {
        this.schoolFrom = schoolFrom;
        this.schoolTo = schoolTo;
    }

    public int getSchoolFrom() {
        return schoolFrom;
    }

    public void setSchoolFrom(int schoolFrom) {
        this.schoolFrom = schoolFrom;
    }

    public int getSchoolTo() {
        return schoolTo;
    }

    public void setSchoolTo(int schoolTo) {
        this.schoolTo = schoolTo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) schoolFrom;
        hash += (int) schoolTo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentFromToPK)) {
            return false;
        }
        PersistentFromToPK other = (PersistentFromToPK) object;
        if (this.schoolFrom != other.schoolFrom) {
            return false;
        }
        if (this.schoolTo != other.schoolTo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.PersistentEntityManagers.TblfromtoPK[ schoolFrom=" + schoolFrom + ", schoolTo=" + schoolTo + " ]";
    }
    
}
