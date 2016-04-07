/**
 * Copyrighted Nov 27, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomRole;

/**
 * Role transported over the REST interface.
 * 
 * @author Gert van der Plas
 */
public class RestRole {
    private DomContext restContext;
    private DomRole role;

    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the role
     */
    public DomRole getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(DomRole role) {
        this.role = role;
    }
}
