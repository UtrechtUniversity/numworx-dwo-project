/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tbldwoprofile", schema = "")
@XmlRootElement
@NamedQueries({
    //Profiles are to ordered for the junit test. Though this is usually the case.
    @NamedQuery(name = "PersistentDwoProfile.findAll", query = "SELECT p FROM PersistentDwoProfile p order by p.dwoProfileID"),
    @NamedQuery(name = "PersistentDwoProfile.findByDwoProfileID", query = "SELECT p FROM PersistentDwoProfile p WHERE p.dwoProfileID = :dwoProfileID"),
    @NamedQuery(name = "PersistentDwoProfile.findByDwoProfileID", query = "SELECT p FROM PersistentDwoProfile p WHERE p.dwoProfileID = :dwoProfileID"),
    @NamedQuery(name = "PersistentDwoProfile.findByDwoProfileName", query = "SELECT p FROM PersistentDwoProfile p WHERE p.dwoProfileName = :dwoProfileName"),
    @NamedQuery(name = "PersistentDwoProfile.findByDwoProfileRights", query = "SELECT p FROM PersistentDwoProfile p WHERE p.dwoProfileRights = :dwoProfileRights"),
    @NamedQuery(name = "PersistentDwoProfile.findByDwoProfileDescription", query = "SELECT p FROM PersistentDwoProfile p WHERE p.dwoProfileDescription = :dwoProfileDescription")})
public class PersistentDwoProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "dwoProfileID", nullable = false)
    private Long dwoProfileID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "dwoProfileName", nullable = false, length = 100)
    private String dwoProfileName;
    @Lob
    @Size(max = 16777215)
    @Column(name = "dwoProfileText", length = 16777215)
    private String dwoProfileText;
    @Size(max = 100)
    @Column(name = "dwoProfileRights", length = 100)
    private String dwoProfileRights;
    @Size(max = 100)
    @Column(name = "dwoProfileDescription", length = 100)
    private String dwoProfileDescription;

    public PersistentDwoProfile() {
    }

    public PersistentDwoProfile(Long dwoProfileID) {
        this.dwoProfileID = dwoProfileID;
    }

    public PersistentDwoProfile(Long dwoProfileID, String dwoProfileName) {
        this.dwoProfileID = dwoProfileID;
        this.dwoProfileName = dwoProfileName;
    }

    public Long getDwoProfileID() {
        return dwoProfileID;
    }

    public void setDwoProfileID(Long dwoProfileID) {
        this.dwoProfileID = dwoProfileID;
    }

    public String getDwoProfileName() {
        return dwoProfileName;
    }

    public void setDwoProfileName(String dwoProfileName) {
        this.dwoProfileName = dwoProfileName;
    }

    public String getDwoProfileText() {
        return dwoProfileText;
    }

    public void setDwoProfileText(String dwoProfileText) {
        this.dwoProfileText = dwoProfileText;
    }

    public String getDwoProfileRights() {
        return dwoProfileRights;
    }

    public void setDwoProfileRights(String dwoProfileRights) {
        this.dwoProfileRights = dwoProfileRights;
    }

    public String getDwoProfileDescription() {
        return dwoProfileDescription;
    }

    public void setDwoProfileDescription(String dwoProfileDescription) {
        this.dwoProfileDescription = dwoProfileDescription;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dwoProfileID != null ? dwoProfileID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersistentDwoProfile)) {
            return false;
        }
        PersistentDwoProfile other = (PersistentDwoProfile) object;
        if ((this.dwoProfileID == null && other.dwoProfileID != null) || (this.dwoProfileID != null && !this.dwoProfileID.equals(other.dwoProfileID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentDwoProfile[ dwoProfileID=" + dwoProfileID + " ]";
    }

    public DomDwoProfile buildDomDwoProfile() {
        DomDwoProfile profile = new DomDwoProfile();
        PersistentDwoProfile.this.fillDomDwoProfile(profile);
        return profile;
    }

    private void fillDomDwoProfile(DomDwoProfile profile) {
        profile.setId(buildPersistenceId());
        profile.setDwoProfileName(getDwoProfileName());
        profile.setDwoProfileRights(getDwoProfileRights());
    }

    public DomDwoProfileFull buildDomDwoProfileFull() {
        DomDwoProfileFull profile = new DomDwoProfileFull();
        PersistentDwoProfile.this.fillDomDwoProfileFull(profile);
        return profile;
    }

    private void fillDomDwoProfileFull(DomDwoProfileFull profile) {
        fillDomDwoProfile(profile);
        profile.setDwoProfileDescription(getDwoProfileDescription());
        profile.setDwoProfileText(getDwoProfileText());
    }

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(dwoProfileID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aProfileId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aProfileId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentDwoProfile.name(), aProfileId));
        return id;
    }
}
