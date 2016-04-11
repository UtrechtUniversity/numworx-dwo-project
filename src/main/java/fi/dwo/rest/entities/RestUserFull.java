/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomUserFull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestUserFull {
    private DomContext restContext;
    private DomUserFull domUserFull;

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
    public DomUserFull getDomUserFull() {
        return domUserFull;
    }

    /**
     * @param domUser the domUser to set
     */
    public void setDomUserFull(DomUserFull domUser) {
        this.domUserFull = domUser;
    }
}
