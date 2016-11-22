/*Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.PersistentUpdate;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * 
 * 
 * 
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblapplet", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"classname"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentApplet.findAll", query = "SELECT p FROM PersistentApplet p"),
    @NamedQuery(name = "PersistentApplet.findByAppletID", query = "SELECT p FROM PersistentApplet p WHERE p.appletID = :appletID"),
    @NamedQuery(name = "PersistentApplet.findByAppletName", query = "SELECT p FROM PersistentApplet p WHERE p.appletName = :appletName"),
    @NamedQuery(name = "PersistentApplet.findByClassname", query = "SELECT p FROM PersistentApplet p WHERE p.classname = :classname"),
    @NamedQuery(name = "PersistentApplet.findByFeatures", query = "SELECT p FROM PersistentApplet p WHERE p.features = :features"),
    @NamedQuery(name = "PersistentApplet.findByJarname", query = "SELECT p FROM PersistentApplet p WHERE p.jarname = :jarname")})
public class PersistentApplet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "appletID", nullable = false)
    private Long appletID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "appletName", nullable = false, length = 128)
    private String appletName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "classname", nullable = false, length = 128)
    private String classname;
    @Size(max = 100)
    @Column(name = "features", length = 100)
    private String features;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "jarname", nullable = false, length = 128)
    private String jarname;

    public PersistentApplet() {
    }

    public PersistentApplet(Long appletID) {
        this.appletID = appletID;
    }

    public PersistentApplet(Long appletID, String appletName, String classname, String jarname) {
        this.appletID = appletID;
        this.appletName = appletName;
        this.classname = classname;
        this.jarname = jarname;
    }

    public Long getAppletID() {
        return appletID;
    }

    public void setAppletID(Long appletID) {
        this.appletID = appletID;
    }

    public String getAppletName() {
        return appletName;
    }

    public void setAppletName(String appletName) {
        this.appletName = appletName;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getJarname() {
        return jarname;
    }

    public void setJarname(String jarname) {
        this.jarname = jarname;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appletID != null ? appletID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersistentApplet)) {
            return false;
        }
        PersistentApplet other = (PersistentApplet) object;
        if ((this.appletID == null && other.appletID != null) || (this.appletID != null && !this.appletID.equals(other.appletID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentApplet[ appletID=" + appletID + " ]";
    }

    /** Builds a PersistenceId using this object's data.
     * 
     * @return 
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(appletID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param anAppletId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long anAppletId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentApplet.name(), anAppletId));
        return id;
    }
}
