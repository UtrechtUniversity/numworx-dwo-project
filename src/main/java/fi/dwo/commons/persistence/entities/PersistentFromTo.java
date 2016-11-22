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
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblfromto", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentFromTo.findAll", query = "SELECT t FROM PersistentFromTo t"),
    @NamedQuery(name = "PersistentFromTo.findBySchoolFrom", query = "SELECT t FROM PersistentFromTo t WHERE t.tblfromtoPK.schoolFrom = :schoolFrom"),
    @NamedQuery(name = "PersistentFromTo.findBySchoolTo", query = "SELECT t FROM PersistentFromTo t WHERE t.tblfromtoPK.schoolTo = :schoolTo")})
public class PersistentFromTo implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersistentFromToPK tblfromtoPK;

    public PersistentFromTo() {
    }

    public PersistentFromTo(PersistentFromToPK tblfromtoPK) {
        this.tblfromtoPK = tblfromtoPK;
    }

    public PersistentFromTo(Long schoolFrom, Long schoolTo) {
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

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(tblfromtoPK);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aProfileId
     * @return
     */
    public static PersistenceId buildPersistenceId(PersistentFromToPK aProfileId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d;%020d",
                PersistenceClassType.PersistentDwoProfile.name(), aProfileId.getSchoolFrom(), aProfileId.getSchoolTo()));
        return id;
    }
}
