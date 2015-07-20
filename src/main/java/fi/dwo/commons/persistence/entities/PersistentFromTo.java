/**
 * Copyrighted Jul 20, 2015
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblfromto", catalog = "dwo_productie_v1_2", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblfromto.findAll", query = "SELECT t FROM Tblfromto t"),
    @NamedQuery(name = "Tblfromto.findBySchoolFrom", query = "SELECT t FROM Tblfromto t WHERE t.tblfromtoPK.schoolFrom = :schoolFrom"),
    @NamedQuery(name = "Tblfromto.findBySchoolTo", query = "SELECT t FROM Tblfromto t WHERE t.tblfromtoPK.schoolTo = :schoolTo")})
public class PersistentFromTo implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersistentFromToPK tblfromtoPK;

    public PersistentFromTo() {
    }

    public PersistentFromTo(PersistentFromToPK tblfromtoPK) {
        this.tblfromtoPK = tblfromtoPK;
    }

    public PersistentFromTo(int schoolFrom, int schoolTo) {
        this.tblfromtoPK = new PersistentFromToPK(schoolFrom, schoolTo);
    }

    public PersistentFromToPK getPersistentFromToPK() {
        return tblfromtoPK;
    }

    public void setTblfromtoPK(PersistentFromToPK tblfromtoPK) {
        this.tblfromtoPK = tblfromtoPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tblfromtoPK != null ? tblfromtoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentFromTo)) {
            return false;
        }
        PersistentFromTo other = (PersistentFromTo) object;
        if ((this.tblfromtoPK == null && other.tblfromtoPK != null) || (this.tblfromtoPK != null && !this.tblfromtoPK.equals(other.tblfromtoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.PersistentEntityManagers.Tblfromto[ tblfromtoPK=" + tblfromtoPK + " ]";
    }
    
}
