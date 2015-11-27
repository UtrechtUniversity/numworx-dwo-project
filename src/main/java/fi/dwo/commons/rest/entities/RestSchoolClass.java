/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolClass {
    private RestContext restContext;
    private DomSchoolClass domSchoolClass;
    
    public RestSchoolClass(){
        
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
     * @return the domSchoolClass
     */
    public DomSchoolClass getDomSchoolClass() {
        return domSchoolClass;
    }

    /**
     * @param domSchoolClass the domSchoolClass to set
     */
    public void setDomSchoolClass(DomSchoolClass domSchoolClass) {
        this.domSchoolClass = domSchoolClass;
    }

    
}
