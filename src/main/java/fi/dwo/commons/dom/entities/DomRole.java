/**
 * Copyrighted Nov 27, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentRole;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Role in client domain.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement

public class DomRole {
    private PersistenceId id;
    private String roleName;    
    
    public DomRole(PersistentRole r){
        this.id = MySQLPersistenceId.createPersistentId(r);
        this.roleName=r.getGroupname();
    }

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(PersistenceId id) {
        this.id = id;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
