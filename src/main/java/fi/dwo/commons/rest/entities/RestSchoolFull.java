/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSchoolFull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolFull {
    private DomContext restContext;
    private DomSchoolFull domSchoolFull;

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
    public DomSchoolFull getDomSchoolFull() {
        return domSchoolFull;
    }

    /**
     * @param domSchoolFull the domUser to set
     */
    public void setDomSchoolFull(DomSchoolFull domSchoolFull) {
        this.domSchoolFull = domSchoolFull;
    }
}
