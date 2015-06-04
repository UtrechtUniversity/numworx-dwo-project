/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

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

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblsamluser", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"samlorgid", "samluserid"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSamlUser.findAll", query = "SELECT p FROM PersistentSamlUser p"),
    @NamedQuery(name = "PersistentSamlUser.findById", query = "SELECT p FROM PersistentSamlUser p WHERE p.id = :id"),
    @NamedQuery(name = "PersistentSamlUser.findBySamlorgid", query = "SELECT p FROM PersistentSamlUser p WHERE p.samlorgid = :samlorgid"),
    @NamedQuery(name = "PersistentSamlUser.findBySamluserid", query = "SELECT p FROM PersistentSamlUser p WHERE p.samluserid = :samluserid"),
    @NamedQuery(name = "PersistentSamlUser.findByUserID", query = "SELECT p FROM PersistentSamlUser p WHERE p.userID = :userID")})
public class PersistentSamlUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "samlorgid", nullable = false, length = 255)
    private String samlorgid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "samluserid", nullable = false, length = 255)
    private String samluserid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "userID", nullable = false)
    private int userID;

    public PersistentSamlUser() {
    }

    public PersistentSamlUser(Integer id) {
        this.id = id;
    }

    public PersistentSamlUser(Integer id, String samlorgid, String samluserid, int userID) {
        this.id = id;
        this.samlorgid = samlorgid;
        this.samluserid = samluserid;
        this.userID = userID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSamlorgid() {
        return samlorgid;
    }

    public void setSamlorgid(String samlorgid) {
        this.samlorgid = samlorgid;
    }

    public String getSamluserid() {
        return samluserid;
    }

    public void setSamluserid(String samluserid) {
        this.samluserid = samluserid;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSamlUser)) {
            return false;
        }
        PersistentSamlUser other = (PersistentSamlUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSamlUser[ id=" + id + " ]";
    }
    
}
