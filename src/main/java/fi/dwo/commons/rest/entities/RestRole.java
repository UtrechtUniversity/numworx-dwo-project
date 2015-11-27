/**
 * Copyrighted Nov 27, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomRole;

/**
 * Role transported over the REST interface.
 * 
 * @author Gert van der Plas
 */
public class RestRole {
    private RestContext restContext;
    private DomRole role;

    /**
     * @return the restContext
     */
    public RestContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(RestContext restContext) {
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
