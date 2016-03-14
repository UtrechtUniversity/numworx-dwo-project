/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomStudent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestStudent {
    private DomContext restContext;
    private DomStudent domStudent;

    public RestStudent() {
        super();
    }

//    
//    public RestStudent(PersistentUser u) {
//        super(u);
//    }

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
    public DomStudent getDomStudent() {
        return domStudent;
    }

    /**
     * @param domStudent
     */
    public void setDomStudent(DomStudent domStudent) {
        this.domStudent = domStudent;
    }
}
