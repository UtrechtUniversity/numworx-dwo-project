/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomSchool;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchool {
    private RestContext restContext;
    private DomSchool domSchool;
    
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
     * @return the domSchool
     */
    public DomSchool getDomSchool() {
        return domSchool;
    }

    /**
     * @param domSchool the domSchool to set
     */
    public void setDomSchool(DomSchool domSchool) {
        this.domSchool = domSchool;
    }
}
