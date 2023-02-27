/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestLoginContext {
    private DomContext restContext;
    private DomLoginContext loginContext;
    
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
    public DomLoginContext getDomLoginContext() {
        return loginContext;
    }

    /**
     * @param loginContext the domSchool to set
     */
    public void setDomLoginContext(DomLoginContext loginContext) {
        this.loginContext = loginContext;
    }
}
