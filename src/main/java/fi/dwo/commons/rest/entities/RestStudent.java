/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomUser;
import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestStudent extends DomUser {
    private RestContext restContext;
    private DomUser domUser;
    
    public RestStudent(PersistentUser u) {
        super(u);
    }

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
     * @return the domUser
     */
    public DomUser getDomUser() {
        return domUser;
    }

    /**
     * @param domUser the domUser to set
     */
    public void setDomUser(DomUser domUser) {
        this.domUser = domUser;
    }
}
