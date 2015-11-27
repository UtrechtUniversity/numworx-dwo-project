/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestUser {
    private RestContext restContext;
    private DomUser domUser;

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
