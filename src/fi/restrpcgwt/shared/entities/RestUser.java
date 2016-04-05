/**
 * Copyrighted Sep 24, 2015
 */
package fi.restrpcgwt.shared.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestUser {
    private DomContext restContext;
    private DomUser domUser;

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
