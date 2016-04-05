/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import fi.dwo.rest.dom.entities.DomRole;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblgroup", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"groupname"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentRole.findAll", query = "SELECT p FROM PersistentRole p"),
    @NamedQuery(name = "PersistentRole.findByGroupID", query = "SELECT p FROM PersistentRole p WHERE p.groupID = :groupID"),
    @NamedQuery(name = "PersistentRole.findByGroupname", query = "SELECT p FROM PersistentRole p WHERE p.groupname = :groupname")})
public class PersistentRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "groupID", nullable = false)
    private Long groupID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "groupname", nullable = false, length = 20)
    private String groupname;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description", nullable = false, length = 65535)
    private String description;

    public PersistentRole() {
    }

    public PersistentRole(Long groupID) {
        this.groupID = groupID;
    }

    public PersistentRole(RoleType type) {
        this.groupID = (long) type.ordinal();
        this.groupname = type.name();
    }

    public PersistentRole(Long groupID, String groupname, String description) {
        this.groupID = groupID;
        this.groupname = groupname;
        this.description = description;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupID != null ? groupID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentRole)) {
            return false;
        }
        PersistentRole other = (PersistentRole) object;
        if ((this.groupID == null && other.groupID != null) || (this.groupID != null && !this.groupID.equals(other.groupID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentRole[ groupID=" + groupID + " ]";
    }

    public DomRole createDomRole() {
        DomRole role = new DomRole();
        buildDomRole(role);
        return role;
    }

    private void buildDomRole(DomRole role) {
        if (this.groupID != null) {
            role.setId(MySQLPersistenceId.createPersistentId(this));
        }
        role.setRoleName(groupname);
    }

}
