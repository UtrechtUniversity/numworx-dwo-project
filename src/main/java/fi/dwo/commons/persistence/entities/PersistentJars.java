/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tbljars", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentJars.findAll", query = "SELECT p FROM PersistentJars p"),
    @NamedQuery(name = "PersistentJars.findByKey", query = "SELECT p FROM PersistentJars p WHERE p.key = :key"),
    @NamedQuery(name = "PersistentJars.findByJarname", query = "SELECT p FROM PersistentJars p WHERE p.jarname = :jarname"),
    @NamedQuery(name = "PersistentJars.findByLastdate", query = "SELECT p FROM PersistentJars p WHERE p.lastdate = :lastdate")})
public class PersistentJars implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "key", nullable = false, length = 100)
    private String key;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "jarname", nullable = false, length = 128)
    private String jarname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "lastdate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date lastdate;

    public PersistentJars() {
    }

    public PersistentJars(String key) {
        this.key = key;
    }

    public PersistentJars(String key, String jarname, Date lastdate) {
        this.key = key;
        this.jarname = jarname;
        this.lastdate = lastdate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJarname() {
        return jarname;
    }

    public void setJarname(String jarname) {
        this.jarname = jarname;
    }

    public Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentJars)) {
            return false;
        }
        PersistentJars other = (PersistentJars) object;
        if ((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentJars[ key=" + key + " ]";
    }

}
