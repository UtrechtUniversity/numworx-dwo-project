/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblscodata", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentScoData.findAll", query = "SELECT p FROM PersistentScoData p"),
    @NamedQuery(name = "PersistentScoData.findByScoID", query = "SELECT p FROM PersistentScoData p WHERE p.scoID = :scoID")})
public class PersistentScoData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "scoID", nullable = false)
    private Long scoID;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description", nullable = false, length = 65535)
    private String description;
    @Lob
    @Size(max = 16777215)
    @Column(name = "launchdata", length = 16777215)
    private String launchdata;
    @Lob
    @Column(name = "launchdatabytes")
    private byte[] launchdatabytes;

    public PersistentScoData() {
    }

    public PersistentScoData(Long scoID) {
        this.scoID = scoID;
    }

    public PersistentScoData(Long scoID, String description) {
        this.scoID = scoID;
        this.description = description;
    }

    public Long getScoID() {
        return scoID;
    }

    public void setScoID(Long scoID) {
        this.scoID = scoID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLaunchdata() {
        return launchdata;
    }

    public void setLaunchdata(String launchdata) {
        this.launchdata = launchdata;
    }

    public byte[] getLaunchdatabytes() {
        return launchdatabytes;
    }

    public void setLaunchdatabytes(byte[] launchdatabytes) {
        this.launchdatabytes = launchdatabytes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scoID != null ? scoID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentScoData)) {
            return false;
        }
        PersistentScoData other = (PersistentScoData) object;
        if ((this.scoID == null && other.scoID != null) || (this.scoID != null && !this.scoID.equals(other.scoID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentScoData[ scoID=" + scoID + " ]";
    }

   /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(scoID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aScoDataId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aScoDataId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentScoData.name(), aScoDataId));
        return id;
    }    
}
