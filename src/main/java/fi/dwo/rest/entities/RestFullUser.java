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
public class RestFullUser {
    private DomContext restContext;
    private DomUserFull domFullUser;

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
    public DomUserFull getDomFullUser() {
        return domFullUser;
    }

    /**
     * @param domUser the domUser to set
     */
    public void setDomFullUser(DomUserFull domUser) {
        this.domFullUser = domUser;
    }
}
