/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomHasRole;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestHasRole {
    private DomContext restContext;
    private DomHasRole domHasRole;
    
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
     * @return the domSchool
     */
    public DomHasRole getDomHasRole() {
        return domHasRole;
    }

    /**
     * @param domSchool the domSchool to set
     */
    public void setDomHasRole(DomHasRole domSchool) {
        this.domHasRole = domSchool;
    }
}
