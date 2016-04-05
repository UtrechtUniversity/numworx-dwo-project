/*Copyrighted 2015. */
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

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblappletconfig", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentAppletConfig.findAll", query = "SELECT p FROM PersistentAppletConfig p"),
    @NamedQuery(name = "PersistentAppletConfig.findByAppletConfigID", query = "SELECT p FROM PersistentAppletConfig p WHERE p.appletConfigID = :appletConfigID"),
    @NamedQuery(name = "PersistentAppletConfig.findByAppletID", query = "SELECT p FROM PersistentAppletConfig p WHERE p.appletID = :appletID"),
    @NamedQuery(name = "PersistentAppletConfig.findByName", query = "SELECT p FROM PersistentAppletConfig p WHERE p.name = :name"),
    @NamedQuery(name = "PersistentAppletConfig.findByLanguage", query = "SELECT p FROM PersistentAppletConfig p WHERE p.language = :language")})
public class PersistentAppletConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "appletConfigID", nullable = false)
    private Long appletConfigID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "appletID", nullable = false)
    private int appletID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name", nullable = false, length = 128)
    private String name;
    @Size(max = 5)
    @Column(name = "language", length = 5)
    private String language;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 16777215)
    @Column(name = "launchdata", nullable = false, length = 16777215)
    private String launchdata;

    public PersistentAppletConfig() {
    }

    public PersistentAppletConfig(Long appletConfigID) {
        this.appletConfigID = appletConfigID;
    }

    public PersistentAppletConfig(Long appletConfigID, int appletID, String name, String launchdata) {
        this.appletConfigID = appletConfigID;
        this.appletID = appletID;
        this.name = name;
        this.launchdata = launchdata;
    }

    public Long getAppletConfigID() {
        return appletConfigID;
    }

    public void setAppletConfigID(Long appletConfigID) {
        this.appletConfigID = appletConfigID;
    }

    public int getAppletID() {
        return appletID;
    }

    public void setAppletID(int appletID) {
        this.appletID = appletID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLaunchdata() {
        return launchdata;
    }

    public void setLaunchdata(String launchdata) {
        this.launchdata = launchdata;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appletConfigID != null ? appletConfigID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentAppletConfig)) {
            return false;
        }
        PersistentAppletConfig other = (PersistentAppletConfig) object;
        if ((this.appletConfigID == null && other.appletConfigID != null) || (this.appletConfigID != null && !this.appletConfigID.equals(other.appletConfigID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentAppletConfig[ appletConfigID=" + appletConfigID + " ]";
    }
    
}
