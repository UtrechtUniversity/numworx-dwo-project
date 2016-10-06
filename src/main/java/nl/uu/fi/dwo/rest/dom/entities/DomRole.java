/**
 * Copyrighted Nov 27, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
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
