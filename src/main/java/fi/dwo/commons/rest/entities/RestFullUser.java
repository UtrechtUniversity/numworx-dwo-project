/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.dom.entities.DomUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestFullUser {
    private DomContext restContext;
    private DomFullUser domFullUser;

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
     * @return the domUser
     */
    public DomFullUser getDomFullUser() {
        return domFullUser;
    }

    /**
     * @param domUser the domUser to set
     */
    public void setDomFullUser(DomFullUser domUser) {
        this.domFullUser = domUser;
    }
}
