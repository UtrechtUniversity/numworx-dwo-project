/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolClass extends DomSchoolClass{
    private RestContext restContext;

    public RestSchoolClass(){
        
    }
        
    public RestSchoolClass(PersistentSchoolClass sc) {
        super(sc);
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

    
}
