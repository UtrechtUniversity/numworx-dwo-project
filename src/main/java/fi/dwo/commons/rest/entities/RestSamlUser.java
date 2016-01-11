/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSamlUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSamlUser {
    private DomContext restContext;
    private DomSamlUser domSamlUser;
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
     * @return the domSamlUser
     */
    public DomSamlUser getDomSamlUser() {
        return domSamlUser;
    }

    /**
     * @param domSamlUser the domSamlUser to set
     */
    public void setDomSamlUser(DomSamlUser domSamlUser) {
        this.domSamlUser = domSamlUser;
    }
    
}
